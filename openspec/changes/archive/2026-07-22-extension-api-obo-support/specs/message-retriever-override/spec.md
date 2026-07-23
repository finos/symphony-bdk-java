## MODIFIED Requirements

### Requirement: MessageRetrieverOverride SPI
`symphony-bdk-core` SHALL expose a `MessageRetrieverOverride` interface in `com.symphony.bdk.core.extension` that covers all agent-facing message *read* operations: list messages, search messages, semantic search, and get message by ID. Every method SHALL accept the active `AuthSession` as its first parameter. When a registered extension implements `BdkMessageRetrieverOverrideProvider`, the `MessageRetrieverOverride` it returns SHALL be used by `MessageService` for all covered operations. On each covered call `MessageService` SHALL pass the `AuthSession` under which it is operating — the bot session for a bot-context `MessageService`, or the OBO session for a `MessageService` obtained via `SymphonyBdk.obo(...)`, `OboServices.messages()`, or `MessageService.obo(...)`. The agent `MessagesApi` client SHALL NOT be called for those operations when an override is active. A single `MessageRetrieverOverride` instance SHALL be safe to invoke concurrently across bot and multiple OBO sessions; per-call routing SHALL rely on the passed `AuthSession` rather than instance state. Pod-backed read operations (`getMessageStatus`, `listAttachments`, `listMessageReceipts`, `getMessageRelationships`, `getAttachmentTypes`) are out of scope and are unaffected by this override.

#### Scenario: listMessages delegates to override with active session
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.listMessages(streamId, since, until, pagination)` delegates entirely to `MessageRetrieverOverride.listMessages(session, ...)` passing the `MessageService`'s active `AuthSession`, and does not call `messagesApi`

#### Scenario: listMessages uses agent API when no override registered
- **WHEN** no extension implementing `BdkMessageRetrieverOverrideProvider` is registered
- **THEN** `MessageService.listMessages(streamId, since, until, pagination)` calls `messagesApi.v4StreamSidMessageGet(...)` as today

#### Scenario: searchMessages delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.searchMessages(query, pagination, sortDir)` delegates to `MessageRetrieverOverride.searchMessages(session, ...)` and does not call `messagesApi`

#### Scenario: searchMessagesSemantic delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.searchMessagesSemantic(query, streamId, pagination)` delegates to `MessageRetrieverOverride.searchMessagesSemantic(session, ...)` and does not call `messagesApi`

#### Scenario: getMessage delegates to override
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.getMessage(messageId)` delegates to `MessageRetrieverOverride.getMessage(session, messageId)` and does not call `messagesApi`

#### Scenario: bot-context read passes the bot session
- **WHEN** a retriever override is registered and `bdk.messages().getMessage(messageId)` is called
- **THEN** the `AuthSession` passed to `MessageRetrieverOverride.getMessage(...)` is the bot `AuthSession`

#### Scenario: OBO read passes the OBO session
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered and a read is performed through `bdk.obo(oboSession).messages().getMessage(messageId)`
- **THEN** the override is invoked and the `AuthSession` passed to `MessageRetrieverOverride.getMessage(...)` is the OBO `AuthSession`, not the bot session

#### Scenario: Pod-backed reads are unaffected
- **WHEN** an extension implementing `BdkMessageRetrieverOverrideProvider` is pre-registered
- **THEN** `MessageService.getMessageStatus(...)`, `listAttachments(...)`, `listMessageReceipts(...)`, `getMessageRelationships(...)`, and `getAttachmentTypes()` continue to call the pod APIs directly, unaffected by the override
