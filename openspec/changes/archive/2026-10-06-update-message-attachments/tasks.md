## 1. Javadoc

- [x] 1.1 In `MessageService.java`, replace "(attachments are not supported yet)" on both `update(...)` overloads with a statement that attachments and previews are supported and require SBE v24.1 or later
- [x] 1.2 Apply the same javadoc fix to both `update(...)` declarations in `OboMessageService.java`

## 2. Tests

- [x] 2.1 In `MessageServiceTest`, find how to capture the multipart request body from `mockApiClient` (or spy `ApiClient.invokeAPI`) and add a small helper if needed
- [x] 2.2 Add a test: update with one attachment and its preview sends one `attachment` and one `preview` part with the expected filename and content type
- [x] 2.3 Add a test: update with two attachments sends two `attachment` parts
- [x] 2.4 Add a test: update with text only sends no `attachment` or `preview` parts
- [x] 2.5 Add a test: update with an attachment through `messageService.obo(authSession)` sends the `attachment` part
- [x] 2.6 Run `./gradlew :symphony-bdk-core:test --tests "com.symphony.bdk.core.service.message.MessageServiceTest"`

## 3. Docs

- [x] 3.1 Add an "update a message with an attachment" snippet to `docs/message.md` under "How to use", with a note that attachments on update require SBE v24.1 or later
