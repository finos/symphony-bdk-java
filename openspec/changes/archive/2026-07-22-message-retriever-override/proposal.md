## Why

`MessageSenderOverride` (see `message-sender-override` spec) lets an extension fully replace the agent-facing *write* side of `MessageService` (send, update, blast, import, suppress, get attachment). The agent-facing *read* side — listing, searching, and fetching messages — still always goes through the real `MessagesApi` on the agent client, even when an extension has taken over sending. An extension that reimplements agent behavior (e.g. an agentless, crypto-handling extension) needs both directions covered to be a drop-in replacement; today it can intercept outgoing messages but not incoming ones.

## What Changes

- **`MessageRetrieverOverride` SPI** (new, in `symphony-bdk-core/extension/`): covers all agent-facing message *read* operations — `listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage` — mirroring the existing `MessageSenderOverride` pattern. Pod-facing read operations (`getMessageStatus`, `listAttachments`, `listMessageReceipts`, `getMessageRelationships`, `getAttachmentTypes`) are unaffected — they never touch the agent and stay out of scope.
- **`BdkMessageRetrieverOverrideProvider`** (new, in `symphony-bdk-core/extension/`): detection interface used by `ExtensionService` to wire the capability into `MessageService` at construction time, mirroring `BdkMessageSenderOverrideProvider`.
- **`MessageService`**: conditional delegation to `MessageRetrieverOverride` for the four covered read operations, following the same `senderOverride != null` pattern already used for writes. The agent `MessagesApi` is never called for those operations when an override is active.
- **`ServiceFactory` / `SymphonyBdk` / `ExtensionService`**: accept and wire an optional `MessageRetrieverOverride`, alongside the existing `MessageSenderOverride` and `DatafeedEventSource` wiring. Both overrides can be registered independently by the same or different extensions.
- **Docs** (`docs/extension.md`): new "Message Retriever Override" subsection alongside "Message Sender Override" under "Overriding Core Behavior".

## Capabilities

### New Capabilities
- `message-retriever-override`: SPI allowing an extension to fully replace all agent-facing message *read* operations (list, search, semantic search, get-by-id) without any change to `MessageService`'s public API.

### Modified Capabilities
*(none — no existing spec-level requirements are changing; all changes are additive)*

## Impact

**Code**
- `symphony-bdk-core/extension/`: 2 new interfaces (`MessageRetrieverOverride`, `BdkMessageRetrieverOverrideProvider`)
- `symphony-bdk-core/ExtensionService.java`: new detection branch + capability lookup method for `BdkMessageRetrieverOverrideProvider`
- `symphony-bdk-core/ServiceFactory.java`: accepts an optional `MessageRetrieverOverride` and passes it to `MessageService`
- `symphony-bdk-core/SymphonyBdk.java`: passes the resolved `MessageRetrieverOverride` from `ExtensionService` into `ServiceFactory`
- `symphony-bdk-core/service/message/MessageService.java`: conditional delegation to `MessageRetrieverOverride` for `listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage`

**APIs**: fully additive — no existing public API is changed or removed. `@API(status = EXPERIMENTAL)`, matching `MessageSenderOverride`.

**Dependencies**: none.
