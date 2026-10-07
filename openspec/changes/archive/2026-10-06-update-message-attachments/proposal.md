## Why

The Update Message v4 endpoint accepts `attachment` and `preview` parts since SBE v24.1, and `MessageService.update(...)` already forwards them (it builds its multipart form with the same `getForm(message)` used by `send`). However, the javadoc on `MessageService` and `OboMessageService` still says "attachments are not supported yet", no test covers update-with-attachment, and the docs show no example. Users reasonably conclude the feature is missing.

## What Changes

- Correct the javadoc of both `update(...)` overloads in `MessageService` and `OboMessageService`: attachments and previews are supported, and require a pod on SBE v24.1 or later.
- Add tests asserting that `update(...)` sends `attachment` and `preview` multipart parts (regular and OBO).
- Add an update-with-attachment example to `docs/message.md`.
- No production logic change, no signature change, no change to the `@API(status = EXPERIMENTAL)` annotation (promotion to stable is a maintainer decision, out of scope).

## Capabilities

### New Capabilities
- `message-update-attachments`: contract that `MessageService.update(...)` (and its OBO variant) forwards `Message` attachments and previews to the Update Message v4 endpoint.

### Modified Capabilities

## Impact

- `symphony-bdk-core`: `MessageService.java`, `OboMessageService.java` (javadoc only); `MessageServiceTest.java` (new tests).
- `docs/message.md` (example).
- Open question, not resolved by this change: server-side semantics of attachments on update (replace vs add) have not been verified against a real pod; the docs will describe only what the API reference states.
