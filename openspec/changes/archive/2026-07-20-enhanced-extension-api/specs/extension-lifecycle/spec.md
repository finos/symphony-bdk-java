## ADDED Requirements

### Requirement: Extension lifecycle callbacks
The BDK extension system SHALL invoke `onBdkStarted(SymphonyBdk bdk)` on any registered extension that implements `BdkExtensionLifecycle` after the `SymphonyBdk` instance is fully constructed. It SHALL invoke `onBdkStopped()` when the BDK is closed or the JVM shutdown hook fires. Both methods SHALL have default no-op implementations so extensions are not required to implement them.

#### Scenario: onBdkStarted called after full construction
- **WHEN** an extension implementing `BdkExtensionLifecycle` is pre-registered via `SymphonyBdkBuilder`
- **THEN** `onBdkStarted(bdk)` is called exactly once after `SymphonyBdk` construction completes, with a fully initialized `SymphonyBdk` instance

#### Scenario: onBdkStopped called on close
- **WHEN** the `SymphonyBdk` instance is closed (or JVM shutdown hook fires)
- **THEN** `onBdkStopped()` is called on all registered extensions that implement `BdkExtensionLifecycle`

#### Scenario: Non-lifecycle extension unaffected
- **WHEN** an extension does not implement `BdkExtensionLifecycle`
- **THEN** no lifecycle methods are called on it and extension registration proceeds normally

---

### Requirement: Typed per-extension configuration injection
The BDK extension system SHALL support typed, per-extension configuration. Any extension implementing `BdkExtensionConfigAware<C>` SHALL receive a deserialized instance of its declared config class `C`, populated from `BdkConfig.extensions.<key>` where `key` is the value returned by `getConfigKey()`.

#### Scenario: Config injected from BdkConfig.extensions map
- **WHEN** `BdkConfig.extensions` contains an entry matching the extension's `getConfigKey()`
- **THEN** `ExtensionService` deserializes that entry to `getConfigClass()` and calls `setExtensionConfig(C)` before `onBdkStarted` is invoked

#### Scenario: Missing config key throws descriptive exception
- **WHEN** `BdkConfig.extensions` does not contain the key declared by the extension
- **THEN** `ExtensionService` throws `BdkExtensionException` with a message identifying the missing key and the extension class

#### Scenario: Malformed config throws descriptive exception
- **WHEN** `BdkConfig.extensions.<key>` cannot be deserialized into the declared config class
- **THEN** `ExtensionService` throws `BdkExtensionException` with a message identifying the config key and target class

---

### Requirement: BdkConfig extensions map
`BdkConfig` SHALL expose a `Map<String, Object> extensions` field, populated from the `bdk.extensions` YAML block. The field SHALL default to an empty map when not present in the configuration file.

#### Scenario: Extension config loaded from YAML
- **WHEN** `bdk-config.yaml` contains `bdk.extensions.<key>: { ... }`
- **THEN** `BdkConfig.getExtensions()` returns a map containing that key with the corresponding raw values

#### Scenario: Missing extensions block uses empty map
- **WHEN** `bdk-config.yaml` does not contain a `bdk.extensions` block
- **THEN** `BdkConfig.getExtensions()` returns an empty map and no exception is thrown

---

### Requirement: BdkConfigAware consolidation
`BdkConfigAware` SHALL be located in `symphony-bdk-core/extension/` alongside all other Aware interfaces. The previous location in `symphony-bdk-config` SHALL expose a `@Deprecated` forwarding interface for one release cycle.

#### Scenario: Extension using BdkConfigAware from core compiles
- **WHEN** an extension imports `com.symphony.bdk.core.extension.BdkConfigAware`
- **THEN** the extension compiles and receives the `BdkConfig` instance at registration time

#### Scenario: Deprecated import produces deprecation warning
- **WHEN** an extension imports `BdkConfigAware` from `com.symphony.bdk.core.config.extension`
- **THEN** the compiler emits a deprecation warning but compilation succeeds

---

### Requirement: Capability-providing extensions pre-registered via builder
Extensions implementing `BdkMessageSenderOverrideProvider` or `BdkDatafeedEventSourceProvider` SHALL be pre-registered via `SymphonyBdkBuilder.extension(Class)` before `SymphonyBdk` is constructed, so their capabilities can be wired into `ServiceFactory`.

#### Scenario: Builder pre-registers capability extension
- **WHEN** `SymphonyBdk.builder().config(cfg).extension(MyExt.class).build()` is called
- **THEN** `MyExt` is instantiated, configured (Aware injection), and its capabilities extracted before `ServiceFactory` creates any services

#### Scenario: Post-construction registration of capability extension logs warning
- **WHEN** `bdk.extensions().register(MyCapabilityExt.class)` is called after `SymphonyBdk` is fully constructed and `MyCapabilityExt` implements `BdkMessageSenderOverrideProvider` or `BdkDatafeedEventSourceProvider`
- **THEN** the extension is registered, a warning is logged stating the capability override has no effect (services already constructed), and no exception is thrown
