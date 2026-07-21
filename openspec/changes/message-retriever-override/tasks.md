## 1. Capability SPI Interfaces

- [ ] 1.1 Add `MessageRetrieverOverride` interface to `symphony-bdk-core/extension/` with `listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage` (and OBO variants handled via the same shared instance, mirroring `MessageSenderOverride`)
- [ ] 1.2 Add `BdkMessageRetrieverOverrideProvider` interface to `symphony-bdk-core/extension/` with `MessageRetrieverOverride getMessageRetrieverOverride()`
- [ ] 1.3 Add Javadoc to both interfaces documenting pre-registration requirement, first-registered-wins rule, and independence from `MessageSenderOverride`

## 2. ExtensionService Enhancements

- [ ] 2.1 Add `ExtensionService.findMessageRetrieverOverride()` — returns `Optional<MessageRetrieverOverride>` from first registered `BdkMessageRetrieverOverrideProvider`; logs warning if more than one is present
- [ ] 2.2 Log a warning (not exception) when `BdkMessageRetrieverOverrideProvider` is registered post-construction (i.e., after capabilities have already been extracted)

## 3. ServiceFactory / SymphonyBdk — Accept Optional Capability

- [ ] 3.1 Add optional `MessageRetrieverOverride` parameter to `ServiceFactory` constructor (nullable), alongside the existing `MessageSenderOverride`/`DatafeedEventSource` parameters
- [ ] 3.2 Pass `MessageRetrieverOverride` into `MessageService` constructor in `ServiceFactory.getMessageService()`
- [ ] 3.3 Extract `MessageRetrieverOverride` from `ExtensionService` in `SymphonyBdk`'s constructor (same step where `MessageSenderOverride`/`DatafeedEventSource` are extracted) and pass it into `ServiceFactory`

## 4. MessageService — Conditional Override Delegation

- [ ] 4.1 Add nullable `MessageRetrieverOverride retrieverOverride` field to `MessageService`; update constructor(s) to accept it and thread it through the OBO copy constructor (`obo(...)`)
- [ ] 4.2 Update the canonical `listMessages(streamId, since, until, pagination)` call site to delegate to `retrieverOverride.listMessages(...)` when non-null, otherwise use the existing `messagesApi` path; verify all public `listMessages` overloads funnel through this single call site
- [ ] 4.3 Update `searchMessages(query, pagination, sortDir)` with the same conditional delegation pattern
- [ ] 4.4 Update `searchMessagesSemantic(query, streamId, pagination)` with the same conditional delegation pattern
- [ ] 4.5 Update `getMessage(messageId)` with the same conditional delegation pattern
- [ ] 4.6 Reuse the existing `wrapOverrideException(...)` helper so exceptions from `MessageRetrieverOverride` are wrapped consistently with `MessageSenderOverride`'s error contract
- [ ] 4.7 Verify pod-backed reads (`getMessageStatus`, `listAttachments`, `listMessageReceipts`, `getMessageRelationships`, `getAttachmentTypes`) are left untouched

## 5. Tests

- [ ] 5.1 Unit test `ExtensionService`: verify `findMessageRetrieverOverride()` returns first provider and logs warning on multiple
- [ ] 5.2 Unit test `ExtensionService`: verify post-construction registration of `BdkMessageRetrieverOverrideProvider` logs a warning and has no effect
- [ ] 5.3 Unit test `MessageService`: verify delegation to `MessageRetrieverOverride` for `listMessages`, `searchMessages`, `searchMessagesSemantic`, `getMessage`; verify fallback to `messagesApi` when override is null
- [ ] 5.4 Unit test `MessageService`: verify `MessageRetrieverOverride` and `MessageSenderOverride` can be active independently of each other (one without the other)
- [ ] 5.5 Unit test `MessageService`: verify OBO copy constructor threads `retrieverOverride` into the OBO instance
- [ ] 5.6 Unit test `MessageService`: verify exceptions from `MessageRetrieverOverride` are wrapped via `wrapOverrideException(...)`
- [ ] 5.7 Integration test `SymphonyBdkBuilder.extension(Class)`: verify `MessageRetrieverOverride` extraction and wiring before service construction, alongside an already-covered `MessageSenderOverride` extension

## 6. Documentation

- [ ] 6.1 Add "Message Retriever Override" subsection to `docs/extension.md` under "Overriding Core Behavior", alongside "Message Sender Override", with a usage example and a note on composing both providers on one extension class
