## ADDED Requirements

### Requirement: MessageRetrieverOverride SPI
`symphony-bdk-core` SHALL expose a `MessageRetrieverOverride` interface in `com.symphony.bdk.core.extension` that covers all agent-facing message *read* operations: list messages, search messages, semantic search, and get message by ID, and their OBO variants. When a registered extension implements `BdkMessageRetrieverOverrideProvider`, the `MessageRetrieverOverride` it returns SHALL be used by `MessageService` for all covered operations. The agent `MessagesApi` client SHALL NOT be called for those operations when an override is active. Pod-backed read operations (`getMessageStatus`, `listAttachments`, `listMessageReceipts`, `getMessageRelationships`, `getAttachmentTypes`) are out of scope and are unaffected by this override.

#### Scenario: listMessages delegates to override when registered
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.listMessages(streamId, since, until, pagination)` delegates entirely to `MessageRetrieverOverride.listMessages(...)` and does not call `messagesApi`

#### Scenario: listMessages uses agent API when no override registered
- **WHEN** no extension implementing `BdkMessageRetrieverOverrideProvider` is registered
- **THEN** `MessageService.listMessages(streamId, since, until, pagination)` calls `messagesApi.v4StreamSidMessageGet(...)` as today

#### Scenario: searchMessages delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.searchMessages(query, pagination, sortDir)` delegates to `MessageRetrieverOverride.searchMessages(...)` and does not call `messagesApi`

#### Scenario: searchMessagesSemantic delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.searchMessagesSemantic(query, streamId, pagination)` delegates to `MessageRetrieverOverride.searchMessagesSemantic(...)` and does not call `messagesApi`

#### Scenario: getMessage delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.getMessage(messageId)` delegates to `MessageRetrieverOverride.getMessage(messageId)` and does not call `messagesApi`

#### Scenario: OBO read delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered and `MessageService` is used in OBO mode
- **THEN** the OBO read operations delegate to the same `MessageRetrieverOverride` instance, consistent with how `MessageSenderOverride` is threaded into OBO mode

#### Scenario: Pod-backed reads are unaffected
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.getMessageStatus(...)`, `listAttachments(...)`, `listMessageReceipts(...)`, `getMessageRelationships(...)`, and `getAttachmentTypes()` continue to call the pod APIs directly, unaffected by the override

### Requirement: Independence from MessageSenderOverride
`MessageRetrieverOverride` and `MessageSenderOverride` SHALL be independently registerable. An extension MAY implement `BdkMessageRetrieverOverrideProvider`, `BdkMessageSenderOverrideProvider`, both, or neither, and each capability SHALL be wired into `MessageService` independently of whether the other is present.

#### Scenario: Retriever override registered without sender override
- **WHEN** an extension implementing only `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService` uses the retriever override for read operations while send/update/blast continue to call `messagesApi` directly

#### Scenario: Sender override registered without retriever override
- **WHEN** an extension implementing only `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService` uses the sender override for write operations while listMessages/searchMessages/searchMessagesSemantic/getMessage continue to call `messagesApi` directly

### Requirement: Single active MessageRetrieverOverride
At most one `MessageRetrieverOverride` SHALL be active at a time. If more than one extension implementing `BdkMessageRetrieverOverrideProvider` is registered, the BDK SHALL use the first registered one and log a warning.

#### Scenario: First registered override wins
- **WHEN** two extensions both implementing `BdkMessageRetrieverOverrideProvider` are pre-registered
- **THEN** `MessageService` uses the override from the first registered extension, and a warning is logged identifying the ignored second provider

### Requirement: MessageRetrieverOverride exception propagation
Exceptions thrown by `MessageRetrieverOverride` implementations SHALL propagate as `ApiException` (or wrapped `BdkExtensionException`) to the caller of `MessageService`, consistent with the existing error contract for messaging operations.

#### Scenario: Override exception wrapped and propagated
- **WHEN** `MessageRetrieverOverride.getMessage(...)` throws a checked exception
- **THEN** `MessageService.getMessage(...)` wraps it in the standard BDK exception and re-throws, preserving the original cause

### Requirement: BdkMessageRetrieverOverrideProvider detection interface
`symphony-bdk-core` SHALL expose `BdkMessageRetrieverOverrideProvider` in `com.symphony.bdk.core.extension`. Extensions implementing this interface SHALL be detected by `ExtensionService` during registration and their override wired into `MessageService` at construction time (when pre-registered via builder) or logged as a no-op warning (when registered post-construction).

#### Scenario: Provider detected at construction time
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered via `SymphonyBdkBuilder`
- **THEN** `ExtensionService` calls `getMessageRetrieverOverride()` and passes the result to `ServiceFactory` before `MessageService` is created

#### Scenario: Post-construction registration has no effect
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is registered via `bdk.extensions().register(...)` after `SymphonyBdk` construction
- **THEN** a warning is logged and the retriever override has no effect on the already-constructed `MessageService`
