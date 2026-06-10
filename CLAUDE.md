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

Snapshots are published to Sonatype OSSRH via the `publishToSonatype` Gradle task (nexus-publish-plugin). This task is automatically triggered in CI on every PR build. Releases use `publishToSonatype closeAndReleaseStagingRepository` and are triggered by a GitHub Release event.

Credentials are passed as Gradle properties: `-PmavenRepoUsername` and `-PmavenRepoPassword`.
