## ADDED Requirements

### Requirement: CLI module and installable launcher
The system SHALL provide a `symphony-bdk-cli` Gradle module that builds an installable `bdk`
launcher via the Gradle `application` plugin's `installDist` task, with main class
`com.symphony.bdk.cli.BdkCli`.

#### Scenario: Building the distribution
- **WHEN** a developer runs `./gradlew :symphony-bdk-cli:installDist`
- **THEN** a runnable launcher is produced at `build/install/symphony-bdk-cli/bin/bdk` together
  with all required runtime jars under `lib/`

#### Scenario: Invoking on PATH
- **WHEN** the `bdk` launcher is symlinked onto `PATH` and invoked as `bdk <noun> <verb> [args]`
- **THEN** the corresponding command executes and the process exits with a documented exit code

#### Scenario: No subcommand prints usage
- **WHEN** `bdk` is invoked with no arguments
- **THEN** the root usage/help is printed and the process exits without performing any Symphony call

### Requirement: Configuration resolution
The CLI SHALL load a `BdkConfig` from `~/.symphony/config.yaml` by default and SHALL accept a
`-c`/`--config <path>` global option to override the location, using `BdkConfigLoader`.

#### Scenario: Default configuration location
- **WHEN** a command is run with no `--config` option and `~/.symphony/config.yaml` exists
- **THEN** the CLI loads that file and authenticates the configured bot

#### Scenario: Explicit configuration override
- **WHEN** a command is run with `--config /path/to/other.yaml`
- **THEN** the CLI loads the specified file instead of the default

#### Scenario: Missing configuration
- **WHEN** the resolved configuration file does not exist or cannot be parsed
- **THEN** the CLI prints a JSON error to `stderr` and exits with a non-zero code without contacting Symphony

### Requirement: Authentication as the configured bot
The CLI SHALL authenticate as the bot described by the loaded configuration and SHALL perform all
operations using that bot's session.

#### Scenario: Successful authentication
- **WHEN** a command is run with a configuration containing valid bot credentials
- **THEN** the CLI authenticates the bot and performs the requested operation as that bot

#### Scenario: Authentication failure
- **WHEN** the bot credentials are invalid or authentication is rejected
- **THEN** the CLI prints a JSON error to `stderr` and exits with code `2`

### Requirement: JSON output on standard output
In v1 the CLI SHALL emit command results as JSON on `stdout` and SHALL NOT provide any other
output format.

#### Scenario: Result serialised as JSON
- **WHEN** a command completes successfully
- **THEN** its result is written to `stdout` as a JSON document and nothing else is written to `stdout`

#### Scenario: Logging does not pollute stdout
- **WHEN** the BDK or its dependencies emit log output during a command
- **THEN** that log output is written to `stderr`, leaving `stdout` as parseable JSON

### Requirement: Error reporting and exit codes
The CLI SHALL report errors as JSON on `stderr` and SHALL use stable, documented exit codes so
that callers can branch on outcome without parsing text.

#### Scenario: Error emitted as JSON
- **WHEN** a command fails
- **THEN** a JSON object containing at least an `error` message is written to `stderr`

#### Scenario: Documented exit codes
- **WHEN** a command fails due to authentication, the exit code is `2`; due to a not-found resource, `3`;
  due to invalid arguments, `64`; for any other error, `1`
- **THEN** the process terminates with the corresponding code, while success terminates with `0`

### Requirement: `whoami` command
The CLI SHALL provide a `bdk whoami` command that prints the authenticated bot's session identity.

#### Scenario: Print bot identity
- **WHEN** `bdk whoami` is run with a valid configuration
- **THEN** the bot's session details (such as user id and display name from `sessions()`) are printed as JSON on `stdout`

### Requirement: Message commands
The CLI SHALL provide `bdk message send`, `bdk message get` and `bdk message list` commands backed
by the BDK `MessageService`.

#### Scenario: Send a message
- **WHEN** `bdk message send <streamId> --message "<text>"` is run
- **THEN** the message is sent to the stream and the resulting message id is printed as JSON

#### Scenario: Get a message by id
- **WHEN** `bdk message get <messageId>` is run
- **THEN** the message is retrieved and printed as JSON, or exit code `3` is returned if it does not exist

#### Scenario: List messages in a stream
- **WHEN** `bdk message list <streamId>` is run, optionally with a `--since` timestamp
- **THEN** the matching messages are printed as a JSON array

### Requirement: Stream commands
The CLI SHALL provide `bdk stream list`, `bdk stream members` and `bdk stream get` commands backed
by the BDK `StreamService`.

#### Scenario: List the bot's streams
- **WHEN** `bdk stream list` is run
- **THEN** the streams the bot is a member of are printed as a JSON array

#### Scenario: List stream members
- **WHEN** `bdk stream members <streamId>` is run
- **THEN** the members of the stream are printed as a JSON array

#### Scenario: Get stream details
- **WHEN** `bdk stream get <streamId>` is run
- **THEN** the stream's details are printed as JSON, or exit code `3` is returned if it does not exist

### Requirement: User commands
The CLI SHALL provide `bdk user get` and `bdk user search` commands backed by the BDK `UserService`.
`bdk user get` SHALL accept a single positional argument and detect by pattern whether it is an
email address or a numeric user id, looking the user up accordingly.

#### Scenario: Get a user by id
- **WHEN** `bdk user get <argument>` is run and the argument matches a numeric user id pattern
- **THEN** the user is looked up by id and printed as JSON, or exit code `3` is returned if no such user exists

#### Scenario: Get a user by email
- **WHEN** `bdk user get <argument>` is run and the argument matches an email-address pattern
- **THEN** the user is looked up by email and printed as JSON, or exit code `3` is returned if no such user exists

#### Scenario: Search users
- **WHEN** `bdk user search <query>` is run
- **THEN** the matching users are printed as a JSON array

### Requirement: Datafeed read command
The CLI SHALL provide a `bdk datafeed read` command that streams real-time events as JSON Lines and
terminates cleanly on a bound or interruption.

#### Scenario: Stream events as JSON Lines
- **WHEN** `bdk datafeed read` is run
- **THEN** each received real-time event is printed as a single JSON object on its own line on `stdout`

#### Scenario: Bounded read
- **WHEN** `bdk datafeed read --count <N>` or `--timeout <duration>` is run
- **THEN** the command stops the datafeed loop and exits with code `0` after the bound is reached

#### Scenario: Interrupted read
- **WHEN** the process receives SIGINT while reading
- **THEN** the datafeed loop is stopped cleanly before the process exits

### Requirement: Health check command
The CLI SHALL provide a `bdk health check` command backed by the BDK `HealthService`.

#### Scenario: Report health
- **WHEN** `bdk health check` is run
- **THEN** the health status of the bot's connected Symphony components is printed as JSON
