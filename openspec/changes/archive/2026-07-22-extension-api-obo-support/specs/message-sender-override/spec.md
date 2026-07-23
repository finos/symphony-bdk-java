## MODIFIED Requirements

### Requirement: MessageSenderOverride SPI
`symphony-bdk-core` SHALL expose a `MessageSenderOverride` interface in `com.symphony.bdk.core.extension` that covers all agent-facing message operations: send, update, blast, import, suppress, and get-attachment. Every method SHALL accept the active `AuthSession` as its first parameter. When a registered extension implements `BdkMessageSenderOverrideProvider`, the `MessageSenderOverride` it returns SHALL be used by `MessageService` for all covered operations. On each covered call `MessageService` SHALL pass the `AuthSession` under which it is operating — the bot session for a bot-context `MessageService`, or the OBO session for a `MessageService` obtained via `SymphonyBdk.obo(...)`, `OboServices.messages()`, or `MessageService.obo(...)`. The agent `MessagesApi` client SHALL NOT be called for those operations when an override is active. A single `MessageSenderOverride` instance SHALL be safe to invoke concurrently across bot and multiple OBO sessions; per-call routing SHALL rely on the passed `AuthSession` rather than instance state.

#### Scenario: send delegates to override with active session
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService.send(streamId, message)` delegates entirely to `MessageSenderOverride.send(session, streamId, message)` passing the `MessageService`'s active `AuthSession`, and does not call `messagesApi`

#### Scenario: send uses agent API when no override registered
- **WHEN** no extension implementing `BdkMessageSenderOverrideProvider` is registered
- **THEN** `MessageService.send(streamId, message)` calls `messagesApi.v4StreamSidMessageCreate(...)` as today

#### Scenario: update delegates to override
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService.update(streamId, messageId, message)` delegates to `MessageSenderOverride.update(session, ...)` and does not call `messagesApi`

#### Scenario: blast delegates to override
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered
- **THEN** `MessageService.sendBlast(streamIds, message)` delegates to `MessageSenderOverride.blast(session, ...)` and does not call `messagesApi`

#### Scenario: bot-context call passes the bot session
- **WHEN** an override is registered and `bdk.messages().send(streamId, message)` is called
- **THEN** the `AuthSession` passed to `MessageSenderOverride.send(...)` is the bot `AuthSession`

#### Scenario: OBO send passes the OBO session via SymphonyBdk.obo
- **WHEN** an extension implementing `BdkMessageSenderOverrideProvider` is pre-registered and a send is performed through `bdk.obo(oboSession).messages().send(streamId, message)`
- **THEN** the override is invoked and the `AuthSession` passed to `MessageSenderOverride.send(...)` is the OBO `AuthSession`, not the bot session

#### Scenario: OBO send passes the OBO session via MessageService.obo
- **WHEN** an override is registered and a send is performed through `bdk.messages().obo(oboSession).send(streamId, message)`
- **THEN** the override is invoked and the `AuthSession` passed to `MessageSenderOverride.send(...)` is the OBO `AuthSession`
