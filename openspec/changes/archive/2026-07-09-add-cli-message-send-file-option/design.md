## Context

`MessageSendCommand` (symphony-bdk-cli) currently only calls the plain-string
`MessageService.send(String streamId, String message)` overload. The BDK core already exposes
attachment support through `MessageService.send(String streamId, Message message)`, where `Message`
is built via `Message.builder().content(...).addAttachment(InputStream, String filename)...build()`.
There is no `File`-based attachment helper on `MessageBuilder`, only `InputStream`.

## Goals / Non-Goals

**Goals:**
- Let `bdk message send` attach a single local file via `-f`/`--file <path>`.
- Keep the existing no-file behavior byte-for-byte unchanged.
- Fail fast with exit code `64` (invalid arguments) when the path doesn't exist or isn't a readable
  regular file, before any network call.

**Non-Goals:**
- Multiple attachments per message.
- Remote/URL attachments or streaming from stdin.
- Changes to `symphony-bdk-core`'s `MessageService`/`Message`/`Attachment` model.

## Decisions

- **Conditional overload selection**: keep `messages().send(streamId, message)` for the no-file
  case, and only build a `Message` object (`Message.builder().content(message)
  .addAttachment(new FileInputStream(file), file.getName()).build()`) when `--file` is present.
  Rationale: minimizes behavior change/risk for the common no-attachment path and matches how the
  SDK itself distinguishes the two use cases.
- **`File` as the picocli option type**: picocli converts `@Option` of type `File` natively, so
  `File file;` with `@Option(names = {"-f", "--file"})` requires no custom converter, consistent
  with how other options in the module rely on picocli's built-in converters.
- **Validation location**: validate existence/readability in the command itself (before calling
  `bdk()`/`messages()`), not by catching `FileNotFoundException` from opening the stream, so the
  error is reported as an invalid-argument (`64`) rather than a generic failure (`1`), matching the
  CLI's documented exit-code contract.
- **Filename**: pass `file.getName()` (not the full path) as the attachment filename, since
  `Attachment` only uses it to derive the MIME type/extension and for display in Symphony.

## Risks / Trade-offs

- [Large files read fully into memory via `Message.builder()`'s `InputStream` attachment] →
  Accepted for v1; no size cap is enforced by the CLI itself, consistent with the rest of the CLI
  not imposing payload limits. Document this as a known limitation if it becomes an issue.
- [Filename without extension causes `Attachment` to throw `MessageCreationException`] → Let it
  propagate as a generic error (exit code `1`) rather than special-casing it, since it's an existing
  core-level constraint the CLI doesn't need to duplicate.

## Open Questions

None — the change is additive and scoped entirely to the CLI's `MessageSendCommand`.
