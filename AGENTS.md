# AGENTS.md

<<<<<<< HEAD
This file provides guidance to AI Coding Assistants when working with code in this repository.

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
=======
Guidance for AI coding agents working in this repository.

---

## Coding Principles

**Bias toward caution over speed. For trivial tasks, use judgment.**

### Think Before Coding

- State assumptions explicitly. If uncertain, ask.
- If multiple interpretations exist, present them — don't pick silently.
- If a simpler approach exists, say so. Push back when warranted.
- If something is unclear, stop. Name what's confusing. Ask.

### Simplicity First

- No features beyond what was asked.
- No abstractions for single-use code.
- No "flexibility" or "configurability" that wasn't requested.
- No error handling for impossible scenarios.
- If you write 200 lines and it could be 50, rewrite it.

### Surgical Changes

When editing existing code:
- Don't "improve" adjacent code, comments, or formatting.
- Don't refactor things that aren't broken.
- Match existing style, even if you'd do it differently.
- If you notice unrelated dead code, mention it — don't delete it.

When your changes create orphans: remove imports/variables/functions that **your** changes made unused. Don't remove pre-existing dead code unless asked.

### Goal-Driven Execution

Transform tasks into verifiable goals:
- "Add validation" → write tests for invalid inputs, then make them pass
- "Fix the bug" → write a test that reproduces it, then make it pass
- "Refactor X" → ensure tests pass before and after

For multi-step tasks, state a brief plan:
```
1. [Step] → verify: [check]
2. [Step] → verify: [check]
```

---

## Build Commands

```bash
# Full build (compile + test + package all modules)
./gradlew build

# Build + coverage report
./gradlew build jacocoTestReport jacocoTestCoverageVerification

# Install all jars to local Maven repository
./gradlew publishToMavenLocal

# Build a single module
./gradlew :symphony-bdk-core:build

# Run a specific test class
./gradlew :symphony-bdk-core:test --tests "com.symphony.bdk.core.service.message.MessageServiceTest"

# Run a single test method
./gradlew :symphony-bdk-core:test --tests "com.symphony.bdk.core.service.message.MessageServiceTest.testSendMessage"

# Run OWASP dependency vulnerability check
./gradlew dependencyCheckAnalyze
```

Default Gradle task is `build`. Java 17 is required (Spring Boot 3).

---

## Architecture

### Module layout

| Module | Purpose |
|--------|---------|
| `symphony-bdk-core` | Main BDK entry point (`SymphonyBdk`), all services, auth, datafeed, activities |
| `symphony-bdk-config` | Configuration model (`BdkConfig`) and YAML loading (`BdkConfigLoader`) |
| `symphony-bdk-http/symphony-bdk-http-api` | HTTP client abstraction interfaces |
| `symphony-bdk-http/symphony-bdk-http-jersey2` | Jersey 2 HTTP client implementation |
| `symphony-bdk-http/symphony-bdk-http-webclient` | Spring WebClient HTTP implementation |
| `symphony-bdk-template/symphony-bdk-template-api` | Template engine abstraction |
| `symphony-bdk-template/symphony-bdk-template-freemarker` | FreeMarker implementation |
| `symphony-bdk-template/symphony-bdk-template-handlebars` | Handlebars implementation |
| `symphony-bdk-spring/symphony-bdk-core-spring-boot-starter` | Spring Boot autoconfiguration for bots |
| `symphony-bdk-spring/symphony-bdk-app-spring-boot-starter` | Spring Boot autoconfiguration for extension apps |
| `symphony-bdk-extension-api` | SPI for pluggable BDK extensions |
| `symphony-bdk-extensions/symphony-group-extension` | Built-in group management extension |
| `symphony-bdk-test/symphony-bdk-test-jupiter` | JUnit 5 test utilities (`@SymphonyBdkTest`, `SymphonyBdkTestMock`) |
| `symphony-bdk-test/symphony-bdk-test-spring-boot` | Spring Boot test helpers |
| `symphony-bdk-bom` | Bill of Materials for dependency management |
| `symphony-bdk-examples` | Runnable example bots |

### Core entry point

`SymphonyBdk` (in `symphony-bdk-core`) is the single facade users instantiate. It owns:
- **Authentication**: `AuthSession` (bot), `OboAuthenticator` (on-behalf-of), `ExtensionAppAuthenticator` — implemented for both RSA and certificate modes.
- **Services**: `MessageService`, `StreamService`, `UserService`, `ConnectionService`, `PresenceService`, `SignalService`, `ApplicationService`, `SessionService`, `HealthService`, `DisclaimerService`.
- **Datafeed/Datahose**: `DatafeedLoop` and `DatahoseLoop` for real-time event consumption.
- **Activity framework**: `ActivityRegistry` — register slash commands and form replies via `bdk.activities().register(...)`.
- **Extensions**: `ExtensionService` — register pluggable extensions via `bdk.extensions().register(...)` (post-construction, additive only) or `SymphonyBdk.builder().extension(MyExt.class).build()` (pre-construction, for capability-providing extensions).

### Enhanced extension API (EXPERIMENTAL)

The extension system supports the following capabilities (all `@API(status = EXPERIMENTAL)`):

**Lifecycle and configuration** (`symphony-bdk-extension-api`):
- `BdkExtensionLifecycle` — `onBdkStarted()` / `onBdkStopped()` callbacks.
- `BdkExtensionConfigAware<C>` — typed per-extension config injected from `bdk.extensions.<key>` in YAML.
- `BdkConfig.extensions` — `Map<String, Object>` populated from the `bdk.extensions` YAML block.

**Capability SPIs** (`symphony-bdk-core/extension/`):
- `BdkAware` — `setBdk(SymphonyBdk)` injection before lifecycle start; for extensions that need full BDK access.
- `MessageSenderOverride` + `BdkMessageSenderOverrideProvider` — replaces all agent-facing message operations (send, update, blast, import, suppress, attachments). When active, `MessagesApi` is never called.
- `DatafeedEventSource` + `BdkDatafeedEventSourceProvider` — replaces the datafeed read/ack cycle (stateless, no persistent datafeed ID). Loop dispatch and retry machinery are unchanged.

**Construction order**: extensions pre-registered via `SymphonyBdkBuilder.extension(Class)` are instantiated, configured (Aware injection), and their capabilities extracted *before* `ServiceFactory` creates `MessageService` and `DatafeedLoopV2`. Extensions registered after construction (via `bdk.extensions().register(...)`) are additive only — capability providers registered post-construction log a warning and have no effect.

### OpenAPI code generation

Modules that talk directly to Symphony REST APIs use `bdk.java-codegen-conventions.gradle` from `buildSrc/`. The convention applies the `org.openapi.generator` Gradle plugin, reads `src/main/resources/api.yaml`, and generates Jersey 2 client code into `build/generated/openapi/`. Generated sources are on the compile classpath but not checked in.

### HTTP client abstraction

`symphony-bdk-http-api` defines provider interfaces. At runtime, either `jersey2` or `webclient` is on the classpath — `ServiceLookup` picks the implementation via `java.util.ServiceLoader`. This allows the same `symphony-bdk-core` to work in both plain-Java and Spring/Reactor environments.

### Spring Boot integration

Both starters (`bdk-core-spring-boot-starter`, `bdk-app-spring-boot-starter`) use Spring Boot 3 autoconfiguration. The core starter wires `SymphonyBdk` and all services as beans; the app starter adds Circle-of-Trust auth, tracing filter, and health indicators for extension apps.

### Testing

Use `@SymphonyBdkTest` (JUnit 5 extension from `symphony-bdk-test-jupiter`) to get an injected `SymphonyBdkTestMock` with pre-stubbed Mockito mocks for all services. `MessageMatchers` provides custom Mockito matchers for MessageML assertions.

### Security

`allow-list.xml` holds OWASP suppression entries for known false-positives. Build fails on CVSS ≥ 5. Add suppressions with a `<notes>` explaining why the CVE is not exploitable before merging dependency updates.

### Build conventions (buildSrc)

- `bdk.java-common-conventions.gradle` — applied to every subproject (Java version, checkstyle, etc.)
- `bdk.java-library-conventions.gradle` — library-specific settings (javadoc jar, sources jar)
- `bdk.java-publish-conventions.gradle` — Maven Central publishing via Sonatype OSSRH
- `bdk.java-codegen-conventions.gradle` — OpenAPI generation (applied only to HTTP client modules)

---

## Tool Routing (context-mode)

Context-mode MCP tools are available. These routing rules protect the context window from flooding — a single unrouted command can dump 56 KB into context.

### Blocked commands

| Command | Use instead |
|---------|-------------|
| `curl` / `wget` | `ctx_fetch_and_index(url, source)` or `ctx_execute(language: "javascript", ...)` |
| Inline HTTP (`fetch('http...`, `requests.get(`, etc.) | `ctx_execute(language, code)` |
| `WebFetch` | `ctx_fetch_and_index(url, source)` then `ctx_search(queries)` |

### Tool hierarchy

1. **GATHER**: `ctx_batch_execute(commands, queries)` — primary; runs all commands, auto-indexes, searches in one call
2. **FOLLOW-UP**: `ctx_search(queries: [...])` — query indexed content; batch all questions in one call
3. **PROCESSING**: `ctx_execute(language, code)` / `ctx_execute_file(path, language, code)` — sandbox execution, only stdout enters context
4. **WEB**: `ctx_fetch_and_index(url, source)` → `ctx_search(queries)` — raw HTML never enters context
5. **INDEX**: `ctx_index(content, source)` — store content in FTS5 knowledge base

### Bash scope

Bash is only for: `git`, `mkdir`, `rm`, `mv`, `ls`, `npm install`, `pip install`, and other short-output commands. For anything producing >20 lines, use `ctx_execute` or `ctx_batch_execute`.

### Read scope

- Reading to **edit** a file → `Read` is correct (Edit needs content in context)
- Reading to **analyze or summarize** → use `ctx_execute_file` instead

### ctx commands

| Command | Action |
|---------|--------|
| `ctx stats` | Call `ctx_stats` MCP tool and display output verbatim |
| `ctx doctor` | Call `ctx_doctor` MCP tool, run returned shell command, display as checklist |
| `ctx upgrade` | Call `ctx_upgrade` MCP tool, run returned shell command, display as checklist |
>>>>>>> e3443309 (Add enhanced extension API with lifecycle, config, and capability overrides)
