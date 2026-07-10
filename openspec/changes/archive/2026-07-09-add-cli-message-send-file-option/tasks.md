## 1. Command implementation

- [x] 1.1 Add `@Option(names = {"-f", "--file"}, paramLabel = "<path>", description = "File to attach to the message.") File file;` to `MessageSendCommand`
- [x] 1.2 Validate that, when `file` is non-null, it exists and is a readable regular file; if not, throw/report an invalid-arguments error resulting in exit code `64` before calling `bdk()`
- [x] 1.3 When `file` is null, keep the existing `bdk().messages().send(streamId, message)` call unchanged
- [x] 1.4 When `file` is present, build `Message.builder().content(message).addAttachment(new FileInputStream(file), file.getName()).build()` and call `bdk().messages().send(streamId, message)` (the `Message`-typed overload)

## 2. Tests

- [x] 2.1 Add a test to `CommandTest.java` sending with `--file` pointing at a temp file, asserting `MessageService.send(streamId, Message)` is invoked with an attachment and the JSON output contains the message id
- [x] 2.2 Add a test for the invalid-path case (nonexistent path), asserting exit code `64` and a JSON error on `stderr`, and that `MessageService.send` is never called
- [x] 2.3 Confirm the existing no-file `messageSendPostsAndPrintsResult()` test still passes unchanged

## 3. Documentation

- [x] 3.1 Update the CLI module's help text / README (if any documents `bdk message send` usage) to mention `-f`/`--file`

## 4. Verification

- [x] 4.1 Run `./gradlew :symphony-bdk-cli:test` and confirm all tests pass
- [x] 4.2 Manually build the launcher (`./gradlew :symphony-bdk-cli:installDist`) and run `bdk message send <streamId> --message "hi" --file <path>` against a test environment to confirm the attachment is delivered
