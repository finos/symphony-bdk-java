## Why

When a BDK instance is configured without a dedicated Agent (e.g. a pod-only / pure OBO extension app that never calls an Agent), token refresh incorrectly falls back to Key Manager (KM) token retrieval, which fails because there is no Agent to authenticate against. This breaks authentication entirely for a valid, previously-unsupported usage pattern: a BDK that never talks to an Agent.

## What Changes

- `AuthSessionImpl.isSkdSupported()` currently calls the Agent's `/v1/info` endpoint to check the Agent version. When no Agent is configured, this call 404s, is treated as "Agent doesn't support SKD", and forces a fallback to KM token retrieval — which also fails since there is no Agent to serve it.
- Introduce a check for "no Agent configured" using the existing `BdkConfig.getAgent().overridesParentConfig()` signal (already used for a similar purpose in `LoadBalancedApiClient`). When the Agent config does not override the parent (pod) config, there is no dedicated Agent.
- When no Agent is configured, skip the Agent-version check entirely and trust the pod-issued JWT's `skd` claim (`JwtHelper.isSkdEnabled`) alone to decide whether KM token retrieval is needed.
- When an Agent *is* explicitly configured, behavior is unchanged: Agents older than 24.12 still force the KM token fallback.

## Capabilities

### New Capabilities
- `bdk-auth`: Session token refresh behavior, including how SKD (Simplified Key Delivery) support is determined and when KM token retrieval is required. No formal spec exists for this capability yet; this change introduces it, scoped to the no-Agent-configured behavior fixed here.

### Modified Capabilities
(none)

## Impact

- `symphony-bdk-core/src/main/java/com/symphony/bdk/core/auth/impl/AuthSessionImpl.java` — `refreshAllTokens()` / `isSkdSupported()` logic.
- `symphony-bdk-core/src/main/java/com/symphony/bdk/core/auth/impl/AbstractBotAuthenticator.java` — needs to expose whether an Agent is configured (or the `BdkConfig`) to `AuthSessionImpl`.
- No public API or configuration schema changes; purely internal auth logic. Not a breaking change.
- Affects only BDK setups with no explicitly configured Agent; behavior for setups with a configured Agent is unchanged.
