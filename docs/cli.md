# Symphony BDK Command-Line Interface (`bdk`)

The `symphony-bdk-cli` module provides an operational command-line interface that authenticates
**as a configured bot** and performs one-shot Symphony operations (messaging, streams, users,
datafeed, health) from the shell.

It is designed to be consumed by both humans and agents (such as Claude Code): **results are
emitted as JSON on `stdout`**, all logging is routed to `stderr`, and failures map to **stable,
documented exit codes**.

> **v1 scope.** JSON is the only output format. There is no `--human` table mode, no interactive
> shell, no project scaffolding, and no OBO (on-behalf-of) support — the CLI always acts as the
> configured bot. Each invocation performs a fresh authentication.

## Install

The CLI is distributed via the Gradle `application` plugin.

```bash
# build the distribution
./gradlew :symphony-bdk-cli:installDist

# the launcher and its runtime jars are produced under:
#   symphony-bdk-cli/build/install/symphony-bdk-cli/bin/bdk
#   symphony-bdk-cli/build/install/symphony-bdk-cli/lib/

# put `bdk` on your PATH (symlink the launcher)
ln -s "$(pwd)/symphony-bdk-cli/build/install/symphony-bdk-cli/bin/bdk" /usr/local/bin/bdk

# verify
bdk --help
```

On Windows a `bdk.bat` launcher is produced alongside `bdk`.

## Configuration

The CLI loads a standard BDK configuration file (the same `bdk-config.yaml` used by a bot
application) via `BdkConfigLoader`.

| Source | Location |
|--------|----------|
| Default | `~/.symphony/config.yaml` |
| Override | `-c` / `--config <path>` (global option) |

```bash
bdk whoami                              # uses ~/.symphony/config.yaml
bdk -c /etc/mybot/config.yaml whoami    # explicit config
```

If the configuration file is missing or cannot be parsed, the CLI prints a JSON error to `stderr`
and exits non-zero **without contacting Symphony**.

### Global options

| Option | Description |
|--------|-------------|
| `-c`, `--config <path>` | Path to the `bdk-config.yaml` (default: `~/.symphony/config.yaml`). |
| `-v`, `--verbose` | Raise the log level on `stderr` from `WARN` to `DEBUG`. |
| `-h`, `--help` | Show usage. |
| `-V`, `--version` | Print the BDK version. |

## Output & exit-code contract

- **Success** → the command result is serialised as a JSON document on `stdout` (pretty-printed);
  nothing else is written to `stdout`.
- **Logging** → all BDK / dependency log output goes to `stderr`, keeping `stdout` parseable.
- **Failure** → a JSON object `{ "error": "<message>", "type": "<exception>" }` is written to
  `stderr`, and the process exits with a documented code.

| Exit | Meaning |
|------|---------|
| `0` | success |
| `1` | generic / unexpected error |
| `2` | authentication failure |
| `3` | not found (resource does not exist / API `404`) |
| `64` | usage error (bad arguments) |

A caller that captures only `stdout` will see nothing on failure and should branch on the exit
code (and read `stderr` for the error detail).

## Command reference

### `bdk whoami`

Print the authenticated bot's session identity (a connectivity smoke test).

```bash
bdk whoami
```

### `bdk message`

| Command | Description |
|---------|-------------|
| `bdk message send <streamId> --message "<text>"` | Send a message to a stream; prints the resulting message. |
| `bdk message get <messageId>` | Retrieve a single message by id (`3` if it does not exist). |
| `bdk message list <streamId> [--since <ts>] [--limit N] [--skip N]` | List messages in a stream as a JSON array. `--since` accepts epoch millis or an ISO-8601 instant; it defaults to the last 24 hours. |

```bash
bdk message send abc123StreamId --message "<messageML>Hello</messageML>"
bdk message get  abcMessageId
bdk message list abc123StreamId --since 2026-06-01T00:00:00Z --limit 20
```

### `bdk stream`

| Command | Description |
|---------|-------------|
| `bdk stream list [--limit N] [--skip N]` | List the streams the bot is a member of (JSON array). |
| `bdk stream members <streamId>` | List the members of a stream. |
| `bdk stream get <streamId>` | Get a stream's details (`3` if it does not exist). |

### `bdk user`

| Command | Description |
|---------|-------------|
| `bdk user get <emailOrId>` | Look up a user. A numeric argument is treated as a user id; an email-shaped argument is treated as an email address (`3` if no such user, `64` if the argument is neither). |
| `bdk user search <query> [--local] [--limit N] [--skip N]` | Search users by free text (JSON array). |

```bash
bdk user get 71811853189212
bdk user get alice@example.com
bdk user search "alice"
```

### `bdk datafeed read`

Stream real-time events as **JSON Lines (NDJSON)** — one JSON object per event, per line, on
`stdout`. The loop stops cleanly after a bound or on `SIGINT` (Ctrl-C).

| Option | Description |
|--------|-------------|
| `--count <N>` | Stop after `N` events. |
| `--timeout <duration>` | Stop after a duration, e.g. `30s`, `5m`, `1h` (ISO-8601 also accepted). |

```bash
bdk datafeed read --count 10
bdk datafeed read --timeout 30s | jq 'select(.type == "MESSAGESENT")'
```

### `bdk health check`

Report the health of the agent and its connected Symphony components.

```bash
bdk health check
```

## Examples for scripting / agents

```bash
# get the bot user id
bot_id=$(bdk whoami | jq -r '.id')

# fail fast on auth errors
if ! bdk health check >/dev/null 2>err.json; then
  echo "health check failed (exit $?): $(jq -r '.error' err.json)" >&2
fi
```
