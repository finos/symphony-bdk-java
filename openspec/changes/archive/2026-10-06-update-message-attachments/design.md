## Context

`MessageService.update(streamId, messageId, content)` builds its multipart form with the private `getForm(Message)`, the same helper used by `send` and `blast`. That helper already adds the `attachment` and `preview` parts, so update already forwards attachments. `OboMessageService` declares the same `update` methods and `MessageService` implements both, so OBO shares the code path. When a `MessageSenderOverride` is registered, update is delegated to it and the form is not built by the BDK.

The gap is documentation and test coverage, not behavior: the javadoc says attachments are unsupported, and existing update tests only send text.

## Goals / Non-Goals

**Goals:**
- Make javadoc and docs match actual behavior, including the SBE v24.1 requirement.
- Lock the behavior in with tests that inspect the multipart request body.

**Non-Goals:**
- Changing production logic or method signatures.
- Promoting `@API(status = EXPERIMENTAL)` to stable (maintainer decision).
- Documenting or enforcing replace-vs-add semantics for existing attachments; unverified against a real pod.
- Guarding against pods older than SBE v24.1; the server's response is surfaced as-is.

## Decisions

- **Javadoc only, no code change.** Reuse of `getForm` already satisfies the API. Alternative considered: a dedicated update form builder; rejected as duplication with no benefit.
- **Assert on the multipart body in tests.** Existing tests use `mockApiClient.onPost(...)`, which only matches path and returns a canned response. The new tests need to capture the request (via the mock client's request inspection, or a spy/captor on `ApiClient.invokeAPI`) and check the `attachment`/`preview` parts. The exact capture mechanism is decided during implementation based on what `mockApiClient` exposes.
- **Docs example in `docs/message.md`** under "How to use", mirroring the existing send-with-attachment snippet, with a note about SBE v24.1.
- **Version note, no runtime check.** The BDK has no pod-version detection for messages; a note in javadoc and docs is enough.

## Risks / Trade-offs

- Real-pod behavior (do attachments replace or add to existing ones; is `message` required alongside) is unverified. Mitigation: docs state only what the API reference states, and the proposal flags it as open.
- If the mock client cannot expose the request body, the test may need a small helper or a spy on `ApiClient`, adding a little test infrastructure.
