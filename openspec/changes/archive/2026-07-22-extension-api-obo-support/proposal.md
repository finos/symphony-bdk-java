## Why

The `MessageSenderOverride` / `MessageRetrieverOverride` extension SPI currently works only in bot context. The dedicated OBO path (`SymphonyBdk.obo(AuthSession)` → `OboServices` → 3-arg `ServiceFactory`) constructs `MessageService` with `null` overrides, so a registered override never fires for OBO calls. Even on the one path that does forward the override (`bdk.messages().obo(session)`), the override is a single shared instance that is only ever given the **bot** `AuthSession` at construction — it has no way to know an individual call is running on behalf of an OBO user, so it cannot route correctly. The existing sender/retriever specs already promise OBO delegation ("with the OBO auth context available to the override implementation"), but the implementation does not deliver it. This blocks the first consumer (the symphony-agent agentless-send extension) from operating in OBO mode.

## What Changes

- **BREAKING** Add an `AuthSession session` parameter as the first argument of every `MessageSenderOverride` and `MessageRetrieverOverride` method. `MessageService` passes its own active `authSession` (bot or OBO) on each call, so a single stateless override instance can route bot- vs OBO-context operations without holding per-user state (thread-safe under concurrent OBO users).
- Thread the extracted `MessageSenderOverride` / `MessageRetrieverOverride` into the dedicated OBO service path: `SymphonyBdk` retains the extracted overrides as fields and passes them to a new `OboServices` constructor overload, which forwards them to the 6-arg `ServiceFactory` so `OboServices.messages()` returns an override-backed `MessageService`.
- Update the `MessageSenderOverride` / `MessageRetrieverOverride` Javadoc to describe per-call session routing (replacing the "receive auth sessions via Aware injection" guidance).
- Update the first consumer (symphony-agent agentless-send extension) and all BDK unit tests to the new method signatures.

The provider detection interfaces (`BdkMessageSenderOverrideProvider`, `BdkMessageRetrieverOverrideProvider`) and the single-active-override / exception-propagation semantics are unchanged.

## Capabilities

### New Capabilities
<!-- none -->

### Modified Capabilities
- `message-sender-override`: the `MessageSenderOverride` SPI methods gain an `AuthSession` parameter, and the OBO delegation requirement is tightened from "OBO auth context available to the override" to "MessageService passes the active OBO `AuthSession` on each covered call, including via the `SymphonyBdk.obo(...)` / `OboServices` path".
- `message-retriever-override`: the `MessageRetrieverOverride` SPI methods gain an `AuthSession` parameter, with the same OBO threading requirement for read operations.

## Impact

- **API (BREAKING, `EXPERIMENTAL`)**: `MessageSenderOverride`, `MessageRetrieverOverride` method signatures.
- **Code**: `SymphonyBdk` (retain override fields, new OBO construction), `OboServices` (new constructor overload), `MessageService` (pass `authSession` into override calls), Javadoc on the two override interfaces.
- **Consumers**: symphony-agent agentless-send extension (`integrate-bdk-messaging-send`) must adopt the new signatures.
- **Tests**: existing `MessageService` / extension unit tests that stub the overrides, plus new OBO coverage.
- No configuration, dependency, or wire-protocol changes.
