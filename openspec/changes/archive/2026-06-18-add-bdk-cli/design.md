## Context

The BDK is a multi-module Gradle build (Java 17, Spring Boot 3). The user-facing entry point
is the `SymphonyBdk` facade, built from a `BdkConfig` that `BdkConfigLoader` can read from a
file or from the `~/.symphony` directory. Services are exposed as accessors on the facade:
`messages()`, `streams()`, `users()`, `datafeed()`, `health()`, `sessions()`.

The internal `tibot` CLI (a *standalone Maven consumer* of the published BDK) already proves a
clean pattern we want to adopt: a picocli root command with global `-c/--config` and `--json`
options, noun commands as containers, verb commands as `Runnable` leaves reaching the root via
`@ParentCommand`, each building a fresh `SymphonyBdk` and printing a result. This change brings
that pattern **in-tree** as a first-class module so it tracks BDK `HEAD` and ships with the
project.

## Goals / Non-Goals

**Goals:**
- A `symphony-bdk-cli` module producing an installable `bdk` launcher (`installDist`).
- One-shot operational commands covering messaging, streams, users, datafeed and health.
- Output that is trivially consumable by an agent (Claude Code): JSON on `stdout`, clean of
  log noise; meaningful exit codes; JSON errors on `stderr`.
- Reuse the public `SymphonyBdk` facade only — no new core APIs.

**Non-Goals:**
- A `--human` / table output mode (reserved for a later change; v1 is JSON-only to keep the code small).
- A long-running interactive shell, REPL, or daemon.
- Project scaffolding / `bdk init` (overlaps with the existing `generator-symphony`; out of scope).
- Session caching / credential persistence across invocations (see Risks).
- GraalVM native-image packaging (the BDK stack is reflection-heavy; fat-jar/installDist is the v1 target).
- OBO (on-behalf-of) flows — v1 acts strictly as the configured bot.

## Decisions

### 1. picocli, git-style noun→verb tree (mirror tibot)
Root `BdkCli implements Runnable` with `@Command(subcommands = {...}, mixinStandardHelpOptions = true)`.
Noun commands (`message`, `stream`, `user`, `datafeed`, `health`) are containers; verb commands are
`Runnable` leaves. Leaves obtain shared config via the `@ParentCommand` chain. `main()` does
`System.exit(new CommandLine(new BdkCli()).execute(args))`.

### 2. Configuration resolution
Global `-c/--config <path>` with default `${user.home}/.symphony/config.yaml`, loaded via
`BdkConfigLoader.loadFromFile(...)`. The resolved `BdkConfig` is used to build one `SymphonyBdk`
per invocation. The CLI acts **as the bot** — there is no separate human identity in v1.

### 3. JSON-only output on a clean stdout (v1)
There is no `--human` flag in v1; every command serialises its result to JSON via a shared
Jackson `ObjectMapper` and writes it to `stdout`. To keep `stdout` parseable, a CLI-specific
`logback.xml` routes **all** logging to `stderr` (default level `WARN`, raised by `-v/--verbose`).
This is the single most important behaviour for the Claude Code use-case.

### 4. Error envelope + exit codes
A picocli `IExecutionExceptionHandler` serialises any thrown exception to a JSON object
(`{"error": "<message>", "type": "<exception>"}`) on `stderr` and maps it to an exit code:

| Exit | Meaning |
|------|---------|
| 0 | success |
| 1 | generic / unexpected error |
| 2 | authentication failure (`AuthInitializationException`, `AuthUnauthorizedException`) |
| 3 | not found / API 404 (`ApiException`/`ApiRuntimeException` with 404) |
| 64 | usage error (bad arguments) — set explicitly to the conventional `EX_USAGE` value; picocli's own `ExitCode.USAGE` is `2`, which would otherwise collide with authentication failure |

> Note: the service layer wraps `ApiException` into an unchecked `ApiRuntimeException` (retry
> decorator), so the handler walks the exception cause chain and inspects both. The invalid-input
> exit code is set to `64` on every command via `exitCodeOnInvalidInput`, and a `ParameterException`
> thrown during command execution is also mapped to `64`.

### 5. `datafeed read` emits JSON Lines (NDJSON)
A single JSON object does not fit a stream of events. `datafeed read` registers a
`RealTimeEventListener`, starts the loop, and prints **one JSON object per event per line** to
`stdout`. It stops after `--count N` events, after `--timeout <duration>`, or on SIGINT — then
cleanly stops the loop. This is the one command whose `stdout` is a stream, not a single document.

### 6. Distribution via the `application` plugin
Apply the Gradle `application` plugin with `mainClass = com.symphony.bdk.cli.BdkCli`.
`./gradlew :symphony-bdk-cli:installDist` produces `build/install/symphony-bdk-cli/bin/bdk`
(launcher) + `lib/` (classpath jars). Users symlink that `bdk` script onto `PATH`. The
`jersey2` HTTP implementation and a logging backend are added as `runtimeOnly` so the
distribution is self-contained.

## Risks / Trade-offs

- **Re-authentication per invocation.** Each command builds a fresh `SymphonyBdk`, triggering a
  full RSA→JWT login (and KM token) round-trip. Fine for occasional human use; for an agent
  firing many calls in sequence this adds latency. Accepted for v1 (keeps the CLI stateless and
  simple); a session cache or background daemon is a future enhancement.
- **JSON-only.** Humans lose pretty tables in v1. Mitigated by pretty-printing JSON and the
  planned `--human` follow-up.
- **stdout/stderr split for errors.** Errors go to `stderr`, results to `stdout`. A caller that
  captures only `stdout` sees nothing on failure and must rely on the exit code. We document the
  exit-code contract; a unified `{ok, data|error}` envelope on stdout was considered but rejected
  for v1 to keep success-path output a bare result document.
- **Bundled logback config** could clash if a future consumer embeds the CLI as a library; the CLI
  is an application, not a library, so this is acceptable.
- **`datafeed read` lifecycle.** Must guarantee the loop is stopped on `--count`/`--timeout`/SIGINT
  to avoid a hung process; handled with a shutdown hook and a bounded listener.
