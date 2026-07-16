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
