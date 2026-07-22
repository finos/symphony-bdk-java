## Context

The message override SPI (`MessageSenderOverride`, `MessageRetrieverOverride`) lets an extension replace `MessageService`'s agent-API calls with its own transport. It currently only works in bot context:

- Overrides are extracted in `SymphonyBdk` (Step 3), then handed to a 6-arg `ServiceFactory` (Step 4) which builds the bot `MessageService` (`SymphonyBdk.java:148-162`, `ServiceFactory.java:190-205`).
- The dedicated OBO path — `SymphonyBdk.obo(AuthSession)` → `new OboServices(config, oboSession)` → 3-arg `ServiceFactory` (`OboServices.java:30-40`, `ServiceFactory.java:88-90`) — passes `null` overrides. So `bdk.obo(session).messages()` never invokes the override.
- `MessageService.obo(oboSession)` (`MessageService.java:171-175`) does copy the override *reference* into an OBO-bound `MessageService`, but the override instance only ever received the **bot** `AuthSession` via `BdkAuthenticationAware` (`ExtensionService.java:67-74`). It therefore cannot tell that a given call is OBO, and has no OBO session to act with.

The override method signatures carry no session (`MessageSenderOverride.java:32-46`, `MessageRetrieverOverride.java:35-47`); the Javadoc pushes bot-vs-OBO routing onto the implementation but gives it nothing to route with. Both capability specs already assert OBO delegation, so this is a conformance gap, not a new feature.

Constraints: the SPI is `@API(EXPERIMENTAL)`; the sole external consumer is the symphony-agent agentless-send extension (`integrate-bdk-messaging-send`), which we control. A bot commonly serves many OBO users concurrently, so any solution must be safe under concurrent bot + multi-OBO invocation.

## Goals / Non-Goals

**Goals:**
- A registered override is invoked for OBO calls through every OBO entry point (`SymphonyBdk.obo(...)`, `OboServices.messages()`, `MessageService.obo(...)`).
- The override knows, per call, which `AuthSession` (bot or OBO) the operation runs under, and can act on behalf of that session.
- A single override instance is safe under concurrent bot + multi-OBO use (no per-user instance state).

**Non-Goals:**
- Changing the provider detection interfaces, single-active-override selection, or exception-propagation contract.
- OBO support for pod-backed reads (out of scope, as in the retriever spec) or for `DatafeedEventSource`.
- Per-OBO-session override instances or caching.

## Decisions

### Decision 1: Pass the active `AuthSession` as the first parameter of every override method
`MessageSenderOverride` / `MessageRetrieverOverride` methods gain a leading `AuthSession session` parameter. `MessageService` already holds its active session (`this.authSession`, bot or OBO); it passes it on each override call. The override stays a single stateless instance and routes per call from the argument.

- **Why**: makes the session explicit at the call site; the override is naturally thread-safe across concurrent bot/OBO calls; fulfils the existing "same instance handles both" Javadoc intent instead of contradicting it.
- **Alternative — session-bound factory** (`getMessageSenderOverride(AuthSession)` producing a fresh OBO-bound override): rejected. It multiplies instances per OBO user and pushes lifecycle/caching onto extension authors, fragmenting the single-instance model.
- **Alternative — `*Aware` OBO injection** (a `setOboAuthSession(...)` hook, signatures unchanged): rejected. A shared instance carrying a mutable "current OBO session" is a data race as soon as two OBO users' calls interleave.

### Decision 2: Thread extracted overrides into the dedicated OBO service path
`SymphonyBdk` retains the extracted `messageSenderOverride` / `messageRetrieverOverride` as instance fields (today they are constructor locals). `SymphonyBdk.obo(AuthSession)` passes them into a new `OboServices(config, oboSession, senderOverride, retrieverOverride)` constructor overload, which forwards them to the existing 6-arg `ServiceFactory` (with `null` datafeed event source). `OboServices.messages()` then returns an override-backed, OBO-session-bound `MessageService`.

- **Why**: closes the `null`-override gap on the primary OBO entry point using existing wiring; no change to `ServiceFactory`'s constructor set.
- The `MessageService.obo(...)` copy path already forwards the reference and now also carries the OBO session into each override call via Decision 1 — no further change needed there.

### Decision 3: Update Javadoc and the consumer in lockstep
Rewrite the interface Javadoc to describe per-call session routing (dropping "receive auth sessions via Aware injection"). Update the symphony-agent agentless-send extension and all BDK unit tests that implement/stub the overrides to the new signatures as part of this change.

- **Why**: it is a breaking signature change to an `EXPERIMENTAL` API with one known consumer; adopting it in the same change keeps the tree green and gives the consumer a concrete migration reference.

## Risks / Trade-offs

- [Breaking signature change to `EXPERIMENTAL` SPI] → acceptable: `EXPERIMENTAL` status permits it and the only consumer is updated in lockstep (Decision 3). Release notes call it out.
- [Override implementations might ignore the passed session and keep using an injected bot session] → the Javadoc and spec make per-call routing normative; OBO scenarios in both specs assert the OBO session is the one passed.
- [`AuthSession` still injected via `BdkAuthenticationAware` for construction-time needs] → retained and unchanged; it is now advisory for the override's own setup, while per-call routing uses the parameter. No conflict.
- [Concurrency] → single stateless instance + per-call session eliminates the shared-mutable-state hazard the rejected alternatives carried.

## Migration Plan

1. Add the `AuthSession` parameter to both SPI interfaces and update Javadoc.
2. Update `MessageService` call sites to pass `this.authSession` into each override call.
3. Add the `OboServices` constructor overload; retain overrides as `SymphonyBdk` fields and pass them from `obo(AuthSession)`.
4. Update BDK unit tests and the symphony-agent agentless-send extension to the new signatures.
5. Validate with `./gradlew :symphony-bdk-core:test` and the consumer's build.

Rollback: revert the change set; because the consumer is updated in lockstep, there is no partial-adoption state to unwind.

## Open Questions

None.
