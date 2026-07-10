## Why

`bdk message send` can only send text/MessageML content. Symphony messages commonly carry file
attachments, and the underlying `MessageService.send(String, Message)` already supports attachments
via `Message.builder().addAttachment(...)`, but the CLI has no way to reach that capability — users
must fall back to writing custom code against the SDK for this common case.

## What Changes

- Add a `-f`/`--file <path>` option to `bdk message send` that attaches the given local file to the
  outgoing message.
- When `--file` is supplied, the command builds a `Message` via `Message.builder().content(message)
  .addAttachment(...)` and calls `messages().send(streamId, Message)` instead of the plain-string
  overload; without `--file`, behavior is unchanged.
- Validate that the given path exists and is a readable regular file before attempting to send,
  surfacing a clear invalid-arguments error (exit code `64`) otherwise.
- `--message`/`-m` remains required; `--file` is optional and additive.

## Capabilities

### New Capabilities
(none)

### Modified Capabilities
- `cli`: the "Message commands" requirement's "Send a message" scenario gains an alternate scenario
  for sending with a file attachment via `-f`/`--file`.

## Impact

- `symphony-bdk-cli/src/main/java/com/symphony/bdk/cli/command/message/MessageSendCommand.java` —
  add the `--file` option and switch to the `Message`-based send overload when a file is present.
- `symphony-bdk-cli/src/test/java/com/symphony/bdk/cli/CommandTest.java` (and related test base) —
  add coverage for sending with `--file`, including the invalid-path error case.
- No changes to `symphony-bdk-core`'s `MessageService`/`OboMessageService` — the attachment
  capability already exists there and is only being exposed through the CLI.
