## Context

`AuthSessionImpl.refreshAllTokens()` decides whether the bot needs a legacy Key Manager (KM) token in addition to the pod session token:

```java
if (!JwtHelper.isSkdEnabled(this.sessionToken) || !isSkdSupported()) {
  this.keyManagerToken = this.authenticator.retrieveKeyManagerToken();
}
```

`isSkdEnabled` reads the pod-issued JWT's `skd` claim (pod-side feature flag: "Simplified Key Delivery is available"). `isSkdSupported()` additionally calls the Agent's `SignalsApi.v1InfoGet()` via `AgentVersionService` to confirm the Agent binary is new enough (>= 24.12) to operate without a KM token.

This second check assumes an Agent is always reachable. When a BDK is configured without a dedicated Agent (`BdkConfig.getAgent()` simply defaults to the pod host — no Agent process actually listens there), `v1InfoGet()` 404s, `AgentVersionService.retrieveAgentVersion()` returns `Optional.empty()`, and `isSkdSupported()` returns `false` — indistinguishable from "a real Agent that is genuinely too old". This forces a KM token fallback that itself fails, since there's no Agent to serve `/keyauth` either, breaking authentication for otherwise-valid no-Agent setups (e.g. pod-only extension apps, pure OBO usage).

## Goals / Non-Goals

**Goals:**
- Allow BDK instances with no explicitly configured Agent to authenticate successfully, relying only on the pod's `skd` claim.
- Preserve current behavior for BDK instances with an explicitly configured Agent, including the KM token fallback for Agents older than 24.12.
- No new configuration surface for users.

**Non-Goals:**
- Changing how the Agent version is parsed or compared.
- Handling the case where an Agent *is* configured but transiently unreachable (network blip, temporary 5xx) — that remains treated as "unsupported" and falls back to KM token, same as today. This change only addresses the *structurally absent* Agent case.

## Decisions

**Detect "no Agent configured" via `BdkConfig.getAgent().overridesParentConfig()`, not a new flag.**

`BdkClientConfig.overridesParentConfig()` already exists and returns `true` only if the user explicitly set `scheme`, `host`, `port`, or `context` under `agent:` in `bdk-config.yaml`. It is already used for the same "is there really a distinct Agent?" question in `LoadBalancedApiClient.validateLoadBalancingConfiguration()`. Reusing it:
- Requires no new config schema or user-facing flag.
- Is consistent with existing precedent in the codebase rather than introducing a second way to express the same fact.

Alternative considered: a new explicit config flag (e.g. `skipAgentVersionCheck`). Rejected because it pushes a workaround onto users who would need to discover and set it, when the condition is already fully derivable from existing config.

Alternative considered: removing the Agent-version check entirely. Rejected because it would regress BDK setups that have a genuinely old Agent (< 24.12) and still need the KM token fallback.

**Wire the "Agent configured?" signal from `BdkConfig` down to `AuthSessionImpl`.**

`AuthSessionImpl` currently reaches the Agent version through `authenticator.getAgentVersionService()`. `AbstractBotAuthenticator` (and its subclasses `BotAuthenticatorRsaImpl` / `BotAuthenticatorCertImpl`) are constructed with the pieces needed to build an `AgentVersionService` but do not currently retain the `BdkConfig` itself. The implementation needs to thread through either the `BdkConfig` (or the single boolean `agentConfigured`) from `AuthenticatorFactoryImpl`, where `BdkConfig` is already available, down to where `isSkdSupported()` is evaluated.

**Updated logic in `isSkdSupported()`:**

```java
protected boolean isSkdSupported() {
  if (!this.authenticator.isAgentConfigured()) {
    return true; // no Agent to be "unsupported"; trust the pod's skd claim alone
  }
  Optional<AgentVersion> currentVersion = authenticator.getAgentVersionService().retrieveAgentVersion();
  if (currentVersion.isEmpty()) {
    return false;
  }
  return currentVersion.get().isHigher(AgentVersion.AGENT_24_12);
}
```

(Exact method/field naming to be finalized during implementation; the shape of the decision is what matters here.)

## Risks / Trade-offs

- **[Risk]** An Agent is explicitly configured but the pod's `skd` claim is stale/wrong (pod thinks SKD is available, Agent doesn't support it) → mitigated: this path is unchanged, the Agent-version check still runs whenever an Agent is configured.
- **[Risk]** A user configures an Agent explicitly at the exact same host/scheme/port/context as the pod, defeating `overridesParentConfig()`'s "no Agent" inference (it would report `true`/configured even though, in that specific case, it happens to coincide with the pod). This is an edge case already handled correctly today (the version check will run and get a real answer from whatever is listening there), so no new risk is introduced.
- **[Trade-off]** No new config flag means no manual override if a future edge case needs one; accepted, since the auto-detected condition is exactly what caused the bug, and the alternative (a flag) is strictly worse ergonomics for the common case.

## Migration Plan

Not applicable — internal logic fix, no data migration, no config changes, no deprecation. Ships as a normal patch release.

## Open Questions

- None blocking. Naming of the new boolean/getter (`isAgentConfigured()` vs exposing `BdkConfig` directly to `AuthSessionImpl`) is an implementation detail to settle in `tasks.md`/code review.
