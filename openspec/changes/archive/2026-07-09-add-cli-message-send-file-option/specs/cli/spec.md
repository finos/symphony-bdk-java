## MODIFIED Requirements

### Requirement: Message commands
The CLI SHALL provide `bdk message send`, `bdk message get` and `bdk message list` commands backed
by the BDK `MessageService`. `bdk message send` SHALL accept an optional `-f`/`--file <path>` option
to attach a local file to the outgoing message.

#### Scenario: Send a message
- **WHEN** `bdk message send <streamId> --message "<text>"` is run
- **THEN** the message is sent to the stream and the resulting message id is printed as JSON

#### Scenario: Send a message with a file attachment
- **WHEN** `bdk message send <streamId> --message "<text>" --file <path>` is run and `<path>` is an
  existing, readable regular file
- **THEN** the message is sent with the file attached and the resulting message id is printed as
  JSON

#### Scenario: Invalid file path
- **WHEN** `bdk message send <streamId> --message "<text>" --file <path>` is run and `<path>` does
  not exist or is not a readable regular file
- **THEN** the CLI prints a JSON error to `stderr` and exits with code `64` without attempting to
  send the message

#### Scenario: Get a message by id
- **WHEN** `bdk message get <messageId>` is run
- **THEN** the message is retrieved and printed as JSON, or exit code `3` is returned if it does not exist

#### Scenario: List messages in a stream
- **WHEN** `bdk message list <streamId>` is run, optionally with a `--since` timestamp
- **THEN** the matching messages are printed as a JSON array
