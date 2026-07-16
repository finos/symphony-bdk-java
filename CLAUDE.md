# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Full build (default task)
./gradlew build

# Build with coverage reports (used in CI)
./gradlew build jacocoTestReport jacocoTestCoverageVerification

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :symphony-bdk-core:test

# Run a single test class or method
./gradlew :symphony-bdk-core:test --tests "com.symphony.bdk.core.service.MessageServiceTest"
./gradlew :symphony-bdk-core:test --tests "com.symphony.bdk.core.service.MessageServiceTest.shouldSendMessage"

# Publish to local Maven repository
./gradlew publishToMavenLocal

# OWASP dependency vulnerability check (requires NVD_API_KEY env var for reasonable speed)
./gradlew dependencyCheck

# Check for dependency updates
./gradlew dependencyUpdates
```

## Module Architecture

The project is a multi-module Gradle build. All modules import the `symphony-bdk-bom` platform for dependency version alignment.

### Core modules

| Module | Role |
|--------|------|
| `symphony-bdk-bom` | Bill of Materials — version constraints for all dependencies |
| `symphony-bdk-config` | Loads and parses `bdk-config.yaml` into `BdkConfig` |
| `symphony-bdk-core` | Entry point, services, auth, datafeed loop, activity framework |
| `symphony-bdk-extension-api` | SPI for third-party BDK extensions |

### Abstraction layers with multiple implementations

| API module | Implementations |
|------------|-----------------|
| `symphony-bdk-http:symphony-bdk-http-api` | `symphony-bdk-http-jersey2`, `symphony-bdk-http-webclient` |
| `symphony-bdk-template:symphony-bdk-template-api` | `symphony-bdk-template-freemarker`, `symphony-bdk-template-handlebars` |

### Spring Boot integration

`symphony-bdk-spring:symphony-bdk-core-spring-boot-starter` and `symphony-bdk-app-spring-boot-starter` wrap the core in auto-configured Spring beans. These do not change core behaviour — they only wire configuration and lifecycle.

### Test utilities

`symphony-bdk-test:symphony-bdk-test-jupiter` provides JUnit 5 extensions; `symphony-bdk-test-spring-boot` wraps them for Spring context tests.

## symphony-bdk-core Internals

`SymphonyBdk` is the user-facing entry point. It is built via `SymphonyBdkBuilder` and owns a `ServiceFactory` that instantiates all services lazily.

Key sub-packages:

- **`auth`** — session authentication and JWT token management (`AuthSession`, `BotAuthenticator`, `OboAuthenticator`)
- **`service`** — one service class per Symphony API domain (`MessageService`, `StreamService`, `UserService`, `DatafeedService`, etc.)
- **`activity`** — activity framework: `ActivityRegistry` dispatches `DatafeedEvent`s to registered `AbstractActivity` handlers (command, form reply, room events)
- **`retry`** — Resilience4j-backed retry decorators applied to all HTTP calls
- **`client`** — HTTP client load-balancing and exception translation

OBO (On-Behalf-Of) flows are surfaced through `OboServices` / `OboService`, which mirror the main services but authenticate with a delegated session.

## Build Conventions (`buildSrc/`)

Four Groovy convention plugins used by sub-modules:

- `bdk.java-common-conventions` — Java 17, UTF-8, JaCoCo, JUnit Platform, sources+javadoc jars, BOM platform import
- `bdk.java-library-conventions` — extends common + `java-library` plugin (used by all published libs)
- `bdk.java-publish-conventions` — `maven-publish` + `signing`; signing is **only required for release versions** (`isReleaseVersion = !version.endsWith('SNAPSHOT')`)
- `bdk.java-codegen-conventions` — OpenAPI Generator (Jersey2, Java 8 date library) reading `src/main/resources/api.yaml`; generated sources land in `build/generated/openapi`

## Publishing

Snapshots are published to Sonatype OSSRH via the `publishToSonatype` Gradle task (nexus-publish-plugin). This task is automatically triggered in CI on every PR build (versioned `PR-<number>-SNAPSHOT`). Releases use `publishToSonatype closeAndReleaseStagingRepository`.

Releasing is fully driven by publishing a GitHub Release (`.github/workflows/release.yml`):

1. Draft a new release on the `main` branch with a `vMAJOR.MINOR.PATCH` tag (e.g. `v3.3.16`).
2. On publish, the workflow derives the version from the tag (stripping the `v`), builds and signs the artifacts with `-PprojectVersion=<version>`, publishes them to Maven Central, and uploads the CLI binaries to the release.
3. It then bumps the `-SNAPSHOT` default in the root `build.gradle` to the next patch on `main` automatically — no manual branch or version edit is needed.

Credentials are passed as Gradle properties: `-PmavenRepoUsername` and `-PmavenRepoPassword`.

# context-mode — MANDATORY routing rules

You have context-mode MCP tools available. These rules are NOT optional — they protect your context window from flooding. A single unrouted command can dump 56 KB into context and waste the entire session.

## BLOCKED commands — do NOT attempt these

### curl / wget — BLOCKED
Any Bash command containing `curl` or `wget` is intercepted and replaced with an error message. Do NOT retry.
Instead use:
- `ctx_fetch_and_index(url, source)` to fetch and index web pages
- `ctx_execute(language: "javascript", code: "const r = await fetch(...)")` to run HTTP calls in sandbox

### Inline HTTP — BLOCKED
Any Bash command containing `fetch('http`, `requests.get(`, `requests.post(`, `http.get(`, or `http.request(` is intercepted and replaced with an error message. Do NOT retry with Bash.
Instead use:
- `ctx_execute(language, code)` to run HTTP calls in sandbox — only stdout enters context

### WebFetch — BLOCKED
WebFetch calls are denied entirely. The URL is extracted and you are told to use `ctx_fetch_and_index` instead.
Instead use:
- `ctx_fetch_and_index(url, source)` then `ctx_search(queries)` to query the indexed content

## REDIRECTED tools — use sandbox equivalents

### Bash (>20 lines output)
Bash is ONLY for: `git`, `mkdir`, `rm`, `mv`, `cd`, `ls`, `npm install`, `pip install`, and other short-output commands.
For everything else, use:
- `ctx_batch_execute(commands, queries)` — run multiple commands + search in ONE call
- `ctx_execute(language: "shell", code: "...")` — run in sandbox, only stdout enters context

### Read (for analysis)
If you are reading a file to **Edit** it → Read is correct (Edit needs content in context).
If you are reading to **analyze, explore, or summarize** → use `ctx_execute_file(path, language, code)` instead. Only your printed summary enters context. The raw file content stays in the sandbox.

### Grep (large results)
Grep results can flood context. Use `ctx_execute(language: "shell", code: "grep ...")` to run searches in sandbox. Only your printed summary enters context.

## Tool selection hierarchy

1. **GATHER**: `ctx_batch_execute(commands, queries)` — Primary tool. Runs all commands, auto-indexes output, returns search results. ONE call replaces 30+ individual calls.
2. **FOLLOW-UP**: `ctx_search(queries: ["q1", "q2", ...])` — Query indexed content. Pass ALL questions as array in ONE call.
3. **PROCESSING**: `ctx_execute(language, code)` | `ctx_execute_file(path, language, code)` — Sandbox execution. Only stdout enters context.
4. **WEB**: `ctx_fetch_and_index(url, source)` then `ctx_search(queries)` — Fetch, chunk, index, query. Raw HTML never enters context.
5. **INDEX**: `ctx_index(content, source)` — Store content in FTS5 knowledge base for later search.

## Subagent routing

When spawning subagents (Agent/Task tool), the routing block is automatically injected into their prompt. Bash-type subagents are upgraded to general-purpose so they have access to MCP tools. You do NOT need to manually instruct subagents about context-mode.

## Output constraints

- Keep responses under 500 words.
- Write artifacts (code, configs, PRDs) to FILES — never return them as inline text. Return only: file path + 1-line description.
- When indexing content, use descriptive source labels so others can `ctx_search(source: "label")` later.

## ctx commands

| Command | Action |
|---------|--------|
| `ctx stats` | Call the `ctx_stats` MCP tool and display the full output verbatim |
| `ctx doctor` | Call the `ctx_doctor` MCP tool, run the returned shell command, display as checklist |
| `ctx upgrade` | Call the `ctx_upgrade` MCP tool, run the returned shell command, display as checklist |
