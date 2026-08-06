## 1. Expose "Agent configured?" signal

- [x] 1.1 Add a way for `AbstractBotAuthenticator` to know whether an Agent is explicitly configured, backed by `BdkConfig.getAgent().overridesParentConfig()` (thread `BdkConfig`, or just the boolean, through `AuthenticatorFactoryImpl` → `BotAuthenticatorRsaImpl` / `BotAuthenticatorCertImpl` → `AbstractBotAuthenticator`).
- [x] 1.2 Expose it via a method on `AbstractBotAuthenticator` (e.g. `isAgentConfigured()`) for `AuthSessionImpl` to consume.

## 2. Fix SKD support check

- [x] 2.1 Update `AuthSessionImpl.isSkdSupported()` to return `true` immediately when no Agent is configured, without calling `AgentVersionService.retrieveAgentVersion()`.
- [x] 2.2 Verify the existing configured-Agent path (version check via `AgentVersionService`) is untouched when an Agent *is* configured.

## 3. Tests

- [x] 3.1 Add/update tests in `AuthSessionImplTest` covering: no Agent configured + `skd` claim true → KM token skipped, no Agent version call made.
- [x] 3.2 Add/update tests in `AuthSessionImplTest` covering: no Agent configured + `skd` claim false → KM token retrieved.
- [x] 3.3 Confirm existing tests for configured-Agent behavior (new-enough Agent, old Agent, unreachable Agent) still pass unchanged.
- [x] 3.4 Add/update tests in `AbstractBotAuthenticatorTest` (and `BotAuthenticatorRsaImplTest` / `BotAuthenticatorCertImplTest` if constructors change) for the new "Agent configured" signal.

## 4. Verification

- [x] 4.1 Run `./gradlew :symphony-bdk-core:test --tests "com.symphony.bdk.core.auth.impl.AuthSessionImplTest"`.
- [x] 4.2 Run full `./gradlew :symphony-bdk-core:test` to catch regressions elsewhere in auth.
