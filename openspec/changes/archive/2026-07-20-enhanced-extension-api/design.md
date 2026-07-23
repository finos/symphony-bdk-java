## Context

The BDK extension mechanism was introduced as an experimental plugin system. Today it supports only additive extensions (new services bolted on top of BDK) via constructor-injection of four resources (`AuthSession`, `ApiClientFactory`, `RetryWithRecoveryBuilder`, `BdkConfig`). It has no lifecycle hooks, no typed configuration, and — critically — no way to influence how BDK's own core services (`MessageService`, `DatafeedLoopV2`) are constructed or behave.

The immediate driver for this enhancement is the **agentless messaging mode**: a deployment model where the BDK bypasses the Symphony Agent entirely. Sending a message, which today is a single call to `/agent/v4/stream/{sid}/message/create`, becomes a multi-call orchestration handled by a proprietary `messaging-crypto-library` (key fetch → local encrypt → direct pod call). The datafeed, which today polls `/agent/v5/datafeed/{id}/read` and receives clear-text events, must instead poll a different endpoint, receive a completely different response shape, and decrypt locally. Neither of these can be expressed as an additive extension — they require replacing internal service behavior.

The design must preserve zero changes to the developer-facing API (`bdk.messages().send(...)`, `bdk.datafeed().start()`) while introducing enough hook points for the agentless extension to fully own the messaging and datafeed paths.

## Goals / Non-Goals

**Goals:**
- Lifecycle hooks (`onBdkStarted`, `onBdkStopped`) for all extensions
- Typed per-extension configuration injected from `BdkConfig.extensions.<key>`
- `MessageSenderOverride` SPI: complete replacement of all agent-facing message ops (send, update, blast, OBO) with zero public API change
- `DatafeedEventSource` SPI: replacement of the datafeed read/ack cycle; loop machinery (retry, ACK, dispatch) unchanged
- Fix construction order so extensions can influence service creation
- Keep the entire change additive — no breaking changes to existing public APIs

**Non-Goals:**
- The `messaging-crypto-library` itself (closed-source, separate project)
- Auto-discovery via `ServiceLoader`
- Extension ordering or dependency graph
- Multiple registrations of the same extension class
- Replacing other BDK services (streams, users, presence) via extension — messaging and datafeed only

## Decisions

### D1 — Capability SPIs live in `symphony-bdk-core`, not `symphony-bdk-extension-api`

**Decision**: `MessageSenderOverride`, `DatafeedEventSource`, and their provider interfaces live in `symphony-bdk-core/extension/` alongside existing Aware interfaces, not in the thin `symphony-bdk-extension-api` module.

**Rationale**: Both SPIs reference BDK model types (`V4Message`, `V4Event`, `Message`) that are generated from OpenAPI and live in `symphony-bdk-core`. Placing these interfaces in `symphony-bdk-extension-api` would require that module to depend on `symphony-bdk-core`, creating a circular dependency. Keeping them in `symphony-bdk-core` is consistent with the existing Aware interface pattern.

**Alternative considered**: Extract model types into a shared `symphony-bdk-model` module — rejected as over-engineering for now.

---

### D2 — Construction order inversion: extensions before ServiceFactory

**Decision**: In `SymphonyBdk`'s constructor, `ExtensionService` is created and extensions are instantiated/configured *before* `ServiceFactory` runs. `SymphonyBdkBuilder` gains an `extension(Class)` method to pre-register extensions.

**Rationale**: `ServiceFactory` wires `MessagesApi(agentClient)` into `MessageService` and `DatafeedApi(datafeedAgentClient)` into `DatafeedLoopV2` at construction time. For the override SPIs to take effect, the services must know about them at construction time, not after. A setter-based late-wiring approach was considered but rejected: it makes `MessageService` and `DatafeedLoopV2` mutable post-construction, complicates thread-safety, and obscures the flow.

**Consequence**: Extensions pre-registered via `SymphonyBdkBuilder` participate in service construction. Extensions registered *after* construction via `bdk.extensions().register(...)` can still be used for additive extensions (new services) but cannot provide `MessageSenderOverride` or `DatafeedEventSource`.

---

### D3 — DatafeedEventSource is stateless: no persistent datafeed ID

**Decision**: `DatafeedEventSource` does not carry a datafeed ID. The interface is:
```java
List<V4Event> readEvents(String ackId) throws Exception;
String ackEvents(List<V4Event> events) throws Exception;  // returns next ackId
```

**Rationale**: The agentless backend is session-based rather than ID-based. The existing `DatafeedLoopV2` manages the datafeed ID lifecycle (create, persist, retry on 404). When a `DatafeedEventSource` is registered, the ID management code path in the loop is bypassed entirely — the ackId returned by `ackEvents` is passed directly to the next `readEvents` call.

**Alternative considered**: Keeping `datafeedId` in the signature for symmetry with the existing API — rejected because the agentless source has no concept of a persistent ID and passing a meaningless parameter is misleading.

---

### D4 — MessageSenderOverride covers all message operations including OBO

**Decision**: `MessageSenderOverride` includes both bot-context and OBO-context variants of all agent-facing message operations. The auth context (bot session vs. OBO session) is injected into the override implementation at construction time via `BdkAuthenticationAware` / a new `BdkOboSessionAware` if needed.

**Rationale**: The agentless mode must cover OBO use cases. Separating bot and OBO into two SPIs would double the interfaces without adding clarity — the implementation (`AgentlessBdkExtension`) already holds both sessions internally.

---

### D5 — Typed config via `BdkExtensionConfigAware<C>` + `BdkConfig.extensions` map

**Decision**: `BdkConfig` gains `Map<String, Object> extensions`. `ExtensionService` detects `BdkExtensionConfigAware<C>`, reads `extensions.get(extension.getConfigKey())`, deserializes it to `extension.getConfigClass()` using Jackson (already a BDK dependency), and calls `setExtensionConfig(C)`. `BdkExtensionConfigAware<C>` lives in `symphony-bdk-extension-api` — it uses pure generics and carries no dependency on `symphony-bdk-core`.

**Rationale**: The map approach is the least invasive change to `BdkConfig` (no new strongly-typed fields for each extension). Jackson is already present in the BDK's test and production classpath. The typed config class lives in the extension jar, not in BDK. Giving extensions only their own slice follows the principle of least privilege.

**Future scope (not in this change)**: Injecting the whole `BdkConfig` bag into an extension (e.g. for extensions that need to read `bdk.agent.*`) could be added later via a dedicated `BdkConfigAware`-style interface. Not needed for the agentless use case, which completely replaces the agent concern and does not read BDK's agent config.

---

### D6 — `BdkConfigAware` dropped

**Decision**: `BdkConfigAware` (in `symphony-bdk-config`) is deleted entirely. `TestExtensionConfigAware` (the only place it was used) is updated to use `BdkExtensionConfigAware<C>` instead.

**Rationale**: `BdkExtensionConfigAware<C>` supersedes it. Injecting the whole `BdkConfig` into an extension is a broad, implicit contract — the extension receives every BDK setting including credentials, retry config, and server endpoints. The typed-slice approach is strictly better: extensions declare exactly what config they need, the contract is explicit, and tests are simpler. No production code implements `BdkConfigAware`; removing it now avoids carrying dead API surface through the `EXPERIMENTAL` phase.

---

### D7 — `BdkExtensionLifecycle.onBdkStarted()` is no-arg; `BdkAware` handles full-BDK injection separately

**Decision**: `BdkExtensionLifecycle` (in `symphony-bdk-extension-api`) defines `void onBdkStarted()` and `void onBdkStopped()` with no parameters. Extensions that need access to `SymphonyBdk` after construction implement a separate `BdkAware` interface (in `symphony-bdk-core/extension/`) with `void setBdk(SymphonyBdk bdk)`. `ExtensionService` injects `SymphonyBdk` via `BdkAware` before calling `onBdkStarted()`.

**Rationale**: Placing `onBdkStarted(SymphonyBdk bdk)` in `symphony-bdk-extension-api` would introduce a circular dependency: `extension-api` → `bdk-core` → `extension-api`. The no-arg form keeps `extension-api` dependency-free. Capability-providing extensions (agentless) do not need `SymphonyBdk` at all; service-consuming extensions that do need it declare the dependency explicitly via `BdkAware`, making the coupling visible.

## Risks / Trade-offs

**[Risk] Pre-registered extensions can't be registered again post-construction**
→ *Mitigation*: Document clearly that capability-providing extensions (those implementing `BdkMessageSenderOverrideProvider` or `BdkDatafeedEventSourceProvider`) must be pre-registered via `SymphonyBdkBuilder`. Additive extensions can still use `bdk.extensions().register(...)` post-construction.

**[Risk] Two `MessageSenderOverride` implementations registered simultaneously**
→ *Mitigation*: `ExtensionService` uses `findMessageSenderOverride()` which returns the first registered provider. If more than one is registered, log a warning and use the first. Document that only one active sender override is supported.

**[Risk] `DatafeedLoopV2` ID-management code becomes dead when `DatafeedEventSource` is active**
→ *Mitigation*: The conditional is isolated to the loop's `readEvents` step. Existing ID lifecycle code remains but is skipped cleanly. Add a log line at startup when source mode is active so operators can confirm the override is in effect.

**[Risk] Jackson deserialization of `BdkConfig.extensions` may fail silently on misconfigured YAML**
→ *Mitigation*: `ExtensionService` wraps deserialization in a try-catch and throws `BdkExtensionException` with a descriptive message including the config key and target class name.

**[Trade-off] Construction order inversion increases complexity in `SymphonyBdk` constructor**
→ Extensions must be partially initialized (Aware injection) before full BDK context exists. `onBdkStarted()` fires after full construction; extensions needing `SymphonyBdk` receive it earlier via `BdkAware` setter injection (see D7).

## Migration Plan

All changes are additive. Existing bots using `bdk.extensions().register(...)` continue to work unchanged. No migration required for standard BDK users.

For bots that wish to adopt the agentless extension:
1. Add agentless extension jar to classpath
2. Add `bdk.extensions.agentless.*` config to `bdk-config.yaml`
3. Change instantiation from `new SymphonyBdk(config)` to `SymphonyBdk.builder().config(config).extension(AgentlessBdkExtension.class).build()`
4. No other code changes

## Open Questions

- **OBO session injection into `MessageSenderOverride`**: does the override receive the OBO session at construction time, or is it passed per-call? Per-call is more flexible but complicates the interface. Decision deferred to implementation phase.
- **`DatafeedLoopV1` support**: should `DatafeedEventSource` also be wired into `DatafeedLoopV1`, or is V2 sufficient for agentless? (Assumption: V2 only.)
- **`DatahoseLoop` support**: is agentless datahose in scope for this change? (Assumption: out of scope, datahose stays agent-based for now.)
