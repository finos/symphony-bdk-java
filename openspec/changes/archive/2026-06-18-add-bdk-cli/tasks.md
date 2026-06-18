## 1. Module bootstrap & build

- [x] 1.1 Add `info.picocli:picocli` version constraint to `symphony-bdk-bom`
- [x] 1.2 Create `symphony-bdk-cli` module and register it in `settings.gradle`
- [x] 1.3 Apply `bdk.java-common-conventions`, the `application` plugin (`mainClass = com.symphony.bdk.cli.BdkCli`, `applicationName = 'bdk'` so `installDist` emits `bin/bdk`), and depend on `project(':symphony-bdk-core')` + runtimeOnly `project(':symphony-bdk-http:symphony-bdk-http-jersey2')` + picocli
- [x] 1.4 Add a CLI-specific `logback.xml` that routes all logging to `stderr` (default `WARN`)
- [x] 1.5 Verify `./gradlew :symphony-bdk-cli:installDist` produces `bin/bdk` + `lib/`

## 2. CLI framework & cross-cutting behaviour

- [x] 2.1 Implement root `BdkCli` command: global `-c/--config` (default `~/.symphony/config.yaml`), `-v/--verbose`, `mixinStandardHelpOptions`, `main()` with `System.exit(...execute(args))`
- [x] 2.2 Implement shared config loading (`BdkConfigLoader.loadFromFile`) and `SymphonyBdk` construction helper reachable by leaf commands
- [x] 2.3 Implement a shared Jackson `ObjectMapper` JSON writer for results on `stdout`
- [x] 2.4 Implement `IExecutionExceptionHandler` mapping exceptions to JSON-on-`stderr` + exit codes (0/1/2/3/64)

## 3. Commands

- [x] 3.1 `whoami` — print bot session identity via `sessions()`
- [x] 3.2 `message` container + `send`, `get`, `list` via `messages()`
- [x] 3.3 `stream` container + `list`, `members`, `get` via `streams()`
- [x] 3.4 `user` container + `get` (pattern-detect email vs numeric id), `search` via `users()`
- [x] 3.5 `datafeed read` — JSON Lines event stream with `--count`/`--timeout`/SIGINT bounds via `datafeed()`
- [x] 3.6 `health check` via `health()`

## 4. Tests

- [x] 4.1 picocli wiring tests: command/subcommand registration, option parsing, default config path
- [x] 4.2 Exit-code / error-envelope tests (auth failure → 2, not-found → 3, bad args → 64)
- [x] 4.3 JSON output tests asserting `stdout` is valid JSON and logs go to `stderr`
- [x] 4.4 Per-command tests against a mocked `SymphonyBdk` / services

## 5. Distribution & docs

- [x] 5.1 Provide a `bin/bdk` install note (symlink `installDist` launcher onto `PATH`)
- [x] 5.2 Write `docs/cli.md`: install, configuration, command reference, JSON/exit-code contract
- [x] 5.3 Update `README.md` to mention the CLI
