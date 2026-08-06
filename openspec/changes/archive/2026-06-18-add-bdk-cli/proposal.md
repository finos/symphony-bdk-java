## Why

The Symphony BDK for Java has no command-line interface. Every interaction with
Symphony today requires writing, compiling and running Java code (`new SymphonyBdk(...)`,
register activities, `datafeed().start()`). There is no way to quickly send a message,
inspect a stream, look up a user, or smoke-test a bot's credentials from a terminal —
let alone to drive those operations from an agent such as Claude Code.

An operational CLI closes that gap: a human (or an agent on their behalf) authenticates
**as the bot** using an existing `bdk-config.yaml` and performs one-shot Symphony
operations from the shell. Because a primary caller will be Claude Code, machine-readable
output is the default rather than an afterthought.

## What Changes

- Add a new Gradle module **`symphony-bdk-cli`** containing a [picocli](https://picocli.info/)
  "git-style" noun→verb command tree, mirroring the proven structure of the internal
  `tibot` CLI.
- The CLI loads a `BdkConfig` from `~/.symphony/config.yaml` by default, overridable with
  `-c/--config <path>`, and instantiates a `SymphonyBdk` to act **as the configured bot**.
- **JSON is the only output format in v1** — emitted on `stdout`. All BDK/logback logging is
  redirected to `stderr` so `stdout` stays parseable. (A `--human` table mode is reserved as a
  future enhancement.)
- Errors are emitted as JSON on `stderr` with **stable, documented exit codes** so callers can
  branch without parsing text.
- Distribution via the Gradle `application` plugin: `installDist` produces a `bin/bdk`
  launcher that can be symlinked onto `PATH`, enabling `bdk <noun> <verb> …`.
- v1 command surface:
  - `bdk whoami` — print the authenticated bot's session/identity (connectivity smoke test)
  - `bdk message send|get|list`
  - `bdk stream list|members|get`
  - `bdk user get|search`
  - `bdk datafeed read` — stream real-time events as JSON Lines
  - `bdk health check`

## Capabilities

### New Capabilities
- `cli`: An operational command-line interface that authenticates as a configured bot and
  performs one-shot Symphony operations (messaging, streams, users, datafeed, health) with
  JSON output suitable for scripting and agent consumption.

### Modified Capabilities
<!-- None. This change is purely additive and does not alter the behaviour of any existing core capability. -->

## Impact

- **New module**: `symphony-bdk-cli` (added to `settings.gradle`).
- **Build**: applies `bdk.java-common-conventions` (Java 17) + the Gradle `application` and
  `shadow`/distribution plumbing; depends on `project(':symphony-bdk-core')` and, at runtime,
  `project(':symphony-bdk-http:symphony-bdk-http-jersey2')`.
- **New runtime dependency**: `info.picocli:picocli` (must be added to `symphony-bdk-bom`).
- **No changes to existing modules' behaviour** — the CLI is a consumer of the public
  `SymphonyBdk` facade only.
- **Docs**: a new `docs/cli.md` and a `bin/bdk` install note.
