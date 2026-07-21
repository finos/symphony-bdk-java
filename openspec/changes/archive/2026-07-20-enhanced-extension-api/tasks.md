## 1. Config and Module Cleanup

- [x] 1.1 Add `Map<String, Object> extensions = new LinkedHashMap<>()` field to `BdkConfig` with getter/setter; verify YAML loading from `bdk.extensions.*`
- [x] 1.2 Delete `BdkConfigAware` from `symphony-bdk-config/src/.../config/extension/`; remove it from `ExtensionService` injection logic
- [x] 1.3 Update `TestExtensionConfigAware` (the only implementation) to use `BdkExtensionConfigAware<C>` instead

## 2. Extension API — New Lifecycle and Config Interfaces

- [x] 2.1 Add `BdkExtensionLifecycle` interface to `symphony-bdk-extension-api` with default no-arg `onBdkStarted()` and `onBdkStopped()` methods
- [x] 2.3 Add `BdkAware` interface to `symphony-bdk-core/extension/` with `void setBdk(SymphonyBdk bdk)` for service-consuming extensions that need full BDK access after construction
- [x] 2.2 Add `BdkExtensionConfigAware<C>` interface to `symphony-bdk-extension-api` with `getConfigKey()`, `getConfigClass()`, and `setExtensionConfig(C)` methods

## 3. Capability SPI Interfaces

- [x] 3.1 Add `MessageSenderOverride` interface to `symphony-bdk-core/extension/` covering: `send`, `update`, `blast`, and OBO variants of all agent-facing message operations
- [x] 3.2 Add `BdkMessageSenderOverrideProvider` interface to `symphony-bdk-core/extension/` with `MessageSenderOverride getMessageSenderOverride()`
- [x] 3.3 Add `DatafeedEventSource` interface to `symphony-bdk-core/extension/` with `List<V4Event> readEvents(String ackId)` and `String ackEvents(List<V4Event> events)`
- [x] 3.4 Add `BdkDatafeedEventSourceProvider` interface to `symphony-bdk-core/extension/` with `DatafeedEventSource getDatafeedEventSource()`

## 4. ExtensionService Enhancements

- [x] 4.1 Detect `BdkExtensionLifecycle` in `ExtensionService.register()` — store references for later lifecycle dispatch
- [x] 4.2 Detect `BdkExtensionConfigAware<C>` in `ExtensionService.register()` — deserialize matching entry from `BdkConfig.extensions` using Jackson and call `setExtensionConfig(C)`; throw `BdkExtensionException` with descriptive message on missing key or deserialization failure
- [x] 4.3 Add `ExtensionService.findMessageSenderOverride()` — returns `Optional<MessageSenderOverride>` from first registered `BdkMessageSenderOverrideProvider`; logs warning if more than one is present
- [x] 4.4 Add `ExtensionService.findDatafeedEventSource()` — returns `Optional<DatafeedEventSource>` from first registered `BdkDatafeedEventSourceProvider`
- [x] 4.5 Add `ExtensionService.onBdkStarted(SymphonyBdk)` — first injects `SymphonyBdk` into all `BdkAware` extensions via `setBdk(bdk)`, then calls no-arg `onBdkStarted()` on all `BdkExtensionLifecycle` extensions
- [x] 4.6 Add `ExtensionService.onBdkStopped()` — calls `onBdkStopped` on all registered `BdkExtensionLifecycle` extensions
- [x] 4.7 Log a warning (not exception) when `BdkMessageSenderOverrideProvider` or `BdkDatafeedEventSourceProvider` is registered post-construction (i.e., after capabilities have already been extracted)

## 5. Construction Order Fix — SymphonyBdk and Builder

- [x] 5.1 Add `extension(Class<? extends BdkExtension>)` method to `SymphonyBdkBuilder` accumulating a list of pre-registered extension classes
- [x] 5.2 Refactor `SymphonyBdk` constructor to create `ExtensionService` (step 2) and instantiate/configure pre-registered extensions (step 3) before `ServiceFactory` is invoked
- [x] 5.3 Extract `MessageSenderOverride` and `DatafeedEventSource` capabilities from `ExtensionService` after extension initialization and before `ServiceFactory` construction
- [x] 5.4 Call `extensionService.onBdkStarted(this)` at the end of `SymphonyBdk` constructor after all services are fully initialized
- [x] 5.5 Wire `extensionService.onBdkStopped()` to a JVM shutdown hook or `close()` method on `SymphonyBdk`

## 6. ServiceFactory — Accept Optional Capabilities

- [x] 6.1 Add optional `MessageSenderOverride` parameter to `ServiceFactory` constructor (nullable)
- [x] 6.2 Pass `MessageSenderOverride` into `MessageService` constructor in `ServiceFactory.getMessageService()`
- [x] 6.3 Add optional `DatafeedEventSource` parameter to `ServiceFactory` constructor (nullable)
- [x] 6.4 Pass `DatafeedEventSource` into `DatafeedLoopV2` constructor in `ServiceFactory.getDatafeedLoop()`

## 7. MessageService — Conditional Override Delegation

- [x] 7.1 Add nullable `MessageSenderOverride senderOverride` field to `MessageService`; update constructor(s) to accept it
- [x] 7.2 Update `send(streamId, message)` to delegate to `senderOverride.send(...)` when non-null, otherwise use existing `messagesApi` path
- [x] 7.3 Update `update(streamId, messageId, message)` with same conditional delegation pattern
- [x] 7.4 Update `sendBlast(streamIds, message)` with same conditional delegation pattern
- [x] 7.5 Update OBO message send/update/blast variants with same conditional delegation pattern
- [x] 7.6 Update attachment, import, and suppress operations with same conditional delegation pattern
- [x] 7.7 Ensure exceptions from `MessageSenderOverride` are wrapped consistently with existing BDK error contract

## 8. DatafeedLoopV2 — Conditional Source Delegation

- [x] 8.1 Add nullable `DatafeedEventSource eventSource` field to `DatafeedLoopV2`; update constructor to accept it
- [x] 8.2 Add startup INFO log in `DatafeedLoopV2` when `eventSource` is non-null, identifying the active source class name
- [x] 8.3 Update the datafeed read step: when `eventSource != null`, call `eventSource.readEvents(ackId)` instead of `datafeedApi.readDatafeed(...)`; skip datafeed ID creation/management entirely
- [x] 8.4 Update the ACK step: when `eventSource != null`, call `eventSource.ackEvents(events)` and store the returned ackId for the next iteration
- [x] 8.5 Verify existing retry logic in `DatafeedLoopV2` applies to `eventSource.readEvents(...)` calls without additional changes

## 9. Tests

- [x] 9.1 Unit test `ExtensionService`: verify `BdkExtensionLifecycle` callbacks are invoked in correct order
- [x] 9.2 Unit test `ExtensionService`: verify `BdkExtensionConfigAware<C>` injection from `BdkConfig.extensions` — happy path, missing key, and deserialization failure
- [x] 9.3 Unit test `ExtensionService`: verify `findMessageSenderOverride()` returns first provider and logs warning on multiple
- [x] 9.4 Unit test `ExtensionService`: verify `findDatafeedEventSource()` returns first provider
- [x] 9.5 Unit test `MessageService`: verify delegation to `MessageSenderOverride` for all covered operations; verify fallback to `messagesApi` when override is null
- [x] 9.6 Unit test `DatafeedLoopV2`: verify `eventSource.readEvents(ackId)` is called instead of `datafeedApi`; verify ackId is threaded correctly; verify no datafeed ID creation
- [x] 9.7 Unit test `DatafeedLoopV2`: verify retry logic applies to `eventSource.readEvents(...)` failures
- [x] 9.8 Integration test `SymphonyBdkBuilder.extension(Class)`: verify capability extraction and wiring before service construction
- [x] 9.9 Update `BdkConfig` tests to cover YAML loading of `bdk.extensions.*` map

## 10. Documentation

- [x] 10.1 Update `symphony-bdk-extension-api` README / Javadoc with new `BdkExtensionLifecycle` and `BdkExtensionConfigAware<C>` interfaces
- [x] 10.2 Add Javadoc to `MessageSenderOverride`, `BdkMessageSenderOverrideProvider`, `DatafeedEventSource`, `BdkDatafeedEventSourceProvider`
- [x] 10.3 Update `SymphonyBdkBuilder` Javadoc to document the `extension(Class)` method and its relationship to post-construction `bdk.extensions().register(...)`
- [x] 10.4 Update CLAUDE.md architecture section to document the enhanced extension API
