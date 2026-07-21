# Message-Sender-Override Specification

### Requirement: MessageSenderOverride SPI
`symphony-bdk-core` SHALL expose a `MessageSenderOverride` interface in `com.symphony.bdk.core.extension` that covers all agent-facing message operations: send, update, blast, import, suppress, and their OBO variants. When a registered extension implements `BdkMessageSenderOverrideProvider`, the `MessageSenderOverride` it returns SHALL be used by `MessageService` for all covered operations. The agent `MessagesApi` client SHALL NOT be called for those operations when an override is active.

#### Scenario: send delegates to override when registered
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService.send(streamId, message)` delegates entirely to `MessageSenderOverride.send(streamId, message)` and does not call `messagesApi`

#### Scenario: send uses agent API when no override registered
- **WHEN** no extension implementing `BdkMessageSenderOverrideProvider` is registered
- **THEN** `MessageService.send(streamId, message)` calls `messagesApi.v4StreamSidMessageCreate(...)` as today

#### Scenario: update delegates to override
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService.update(streamId, messageId, message)` delegates to `MessageSenderOverride.update(...)` and does not call `messagesApi`

#### Scenario: blast delegates to override
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService.sendBlast(streamIds, message)` delegates to `MessageSenderOverride.blast(...)` and does not call `messagesApi`

#### Scenario: OBO send delegates to override
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered and `MessageService` is used in OBO mode
- **THEN** the OBO send operation delegates to `MessageSenderOverride` with the OBO auth context available to the override implementation

### Requirement: Single active MessageSenderOverride
At most one `MessageSenderOverride` SHALL be active at a time. If more than one extension implementing `BdkMessageSenderOverrideProvider` is registered, the BDK SHALL use the first registered one and log a warning.

#### Scenario: First registered override wins
- **WHEN** two extensions both implementing `BdkMessageSenderOverrideProvider` are pre-registered
- **THEN** `MessageService` uses the override from the first registered extension, and a warning is logged identifying the ignored second provider

### Requirement: MessageSenderOverride exception propagation
Exceptions thrown by `MessageSenderOverride` implementations SHALL propagate as `ApiException` (or wrapped `BdkExtensionException`) to the caller of `MessageService`, consistent with the existing error contract for messaging operations.

#### Scenario: Override exception wrapped and propagated
- **WHEN** `MessageSenderOverride.send(...)` throws a checked exception
- **THEN** `MessageService.send(...)` wraps it in the standard BDK exception and re-throws, preserving the original cause

### Requirement: BdkMessageSenderOverrideProvider detection interface
`symphony-bdk-core` SHALL expose `BdkMessageSenderOverrideProvider` in `com.symphony.bdk.core.extension`. Extensions implementing this interface SHALL be detected by `ExtensionService` during registration and their override wired into `MessageService` at construction time (when pre-registered via builder) or logged as a no-op warning (when registered post-construction).

#### Scenario: Provider detected at construction time
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered via `SymphonyBdkBuilder`
- **THEN** `ExtensionService` calls `getMessageSenderOverride()` and passes the result to `ServiceFactory` before `MessageService` is created
