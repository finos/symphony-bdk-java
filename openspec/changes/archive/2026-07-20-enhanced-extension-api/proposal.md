## Why

The BDK extension mechanism (`symphony-bdk-extension-api`, `ExtensionService`) has been `EXPERIMENTAL` since its introduction and is effectively unused in production — it lacks lifecycle hooks, typed configuration, and any way for extensions to influence core service behavior. A concrete, high-value use case has emerged that requires all of these gaps to be filled: an **agentless messaging mode** where the BDK bypasses the Symphony Agent entirely and handles encryption/decryption locally via a proprietary `messaging-crypto-library`, with zero impact on the developer-facing API.

## What Changes

- **`BdkExtensionLifecycle` interface** (new, in `symphony-bdk-extension-api`): `onBdkStarted(SymphonyBdk)` and `onBdkStopped()` hooks so extensions can start workers or release resources at the right time.
- **`BdkExtensionConfigAware<C>` interface** (new, in `symphony-bdk-extension-api`): typed per-extension configuration injected from a new `extensions` map in `BdkConfig`.
- **`BdkConfig.extensions`** (new field): `Map<String, Object>` allowing any extension to declare its own config block in `bdk-config.yaml` under `bdk.extensions.<key>`.
- **`MessageSenderOverride` SPI** (new, in `symphony-bdk-core/extension/`): replaces ALL agent-facing message operations (send, update, blast, attachments, OBO variants) when registered. The agent client is never called for messaging when this provider is active.
- **`DatafeedEventSource` SPI** (new, in `symphony-bdk-core/extension/`): replaces the entire datafeed read/ack cycle (stateless — no persistent datafeed ID). Returns already-decrypted `List<V4Event>`; the loop dispatch and retry machinery are unchanged.
- **`BdkMessageSenderOverrideProvider` / `BdkDatafeedEventSourceProvider`** (new, in `symphony-bdk-core/extension/`): detection interfaces used by `ExtensionService` to wire capabilities into services.
- **Construction order fix** in `SymphonyBdk`: extensions are now initialized before `ServiceFactory` so their capabilities can influence how `MessageService` and `DatafeedLoopV2` are built.
- **`SymphonyBdkBuilder.extension(Class)`** (new): pre-registers extensions before construction, replacing the post-construction `bdk.extensions().register(...)` pattern for capability-providing extensions.
- **`BdkConfigAware` relocation**: moved from `symphony-bdk-config` to `symphony-bdk-core/extension/` alongside all other Aware interfaces.
- **`ExtensionService` improvements**: detects and injects `BdkExtensionLifecycle` and `BdkExtensionConfigAware<C>` in addition to existing Aware interfaces.

## Capabilities

### New Capabilities

- `extension-lifecycle`: Lifecycle callbacks (`onBdkStarted`, `onBdkStopped`) and typed config injection (`BdkExtensionConfigAware<C>`) for all BDK extensions.
- `message-sender-override`: SPI allowing an extension to fully replace all agent-facing message operations (send, update, blast, OBO) without any change to `MessageService`'s public API.
- `datafeed-event-source`: SPI allowing an extension to replace the datafeed read/ack cycle with a stateless alternative that returns decrypted events; loop dispatch and retry logic are unaffected.

### Modified Capabilities

*(none — no existing spec-level requirements are changing; all changes are additive)*

## Impact

**Code**
- `symphony-bdk-extension-api`: 2 new interfaces (`BdkExtensionLifecycle`, `BdkExtensionConfigAware<C>`)
- `symphony-bdk-core/extension/`: 4 new interfaces (`MessageSenderOverride`, `DatafeedEventSource`, `BdkMessageSenderOverrideProvider`, `BdkDatafeedEventSourceProvider`); `BdkConfigAware` moved here
- `symphony-bdk-core/ExtensionService.java`: new injection branches + capability lookup methods
- `symphony-bdk-core/SymphonyBdk.java`: construction order change (extensions before `ServiceFactory`)
- `symphony-bdk-core/SymphonyBdkBuilder.java`: new `extension(Class)` method
- `symphony-bdk-core/ServiceFactory.java`: accepts optional `MessageSenderOverride` and `DatafeedEventSource`
- `symphony-bdk-core/service/message/MessageService.java`: conditional delegation to `MessageSenderOverride`
- `symphony-bdk-core/service/datafeed/DatafeedLoopV2.java`: conditional delegation to `DatafeedEventSource`
- `symphony-bdk-config/BdkConfig.java`: new `extensions` map field

**APIs**: fully additive — no existing public API is changed or removed. `@API(status = EXPERIMENTAL)` remains until the agentless extension is validated in production.

**Dependencies**: no new external dependencies. The `messaging-crypto-library` is a dependency of the agentless extension jar (closed-source, not in this repo), not of the BDK itself.
