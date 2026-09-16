## 1. Prerequisites

- [x] 1.1 Confirm `migrate-spring-boot-4` is merged to `next` and `./gradlew build jacocoTestReport jacocoTestCoverageVerification` is green on Java 17 + Spring Boot 4
  - Confirmed: `symphony-bdk-bom/build.gradle` pins `spring-boot-dependencies:4.0.0`; `./gradlew build jacocoTestReport jacocoTestCoverageVerification` on JDK 17 → BUILD SUCCESSFUL.
- [x] 1.2 Retrieve the residual JDK 25 failure list recorded by `modernize-build-toolchain` tasks 7.1–7.2; it is the authoritative starting inventory for sections 2 and 4
  - Retrieved from `openspec/changes/archive/2026-08-05-modernize-build-toolchain/tasks.md` task 7.1–7.2: **zero failures** on a throwaway JDK 25 build with sections 3–6 tooling in place (Gradle 9.6.1, Mockito, ArchUnit, Lombok 1.18.46, `openApiGenerate` all clean). Starting inventory is empty; section 2/4 of this change still re-verify fresh.
- [x] 1.3 Confirm the Gradle version on `next` can provision a JDK 25 toolchain (recorded in `modernize-build-toolchain` task 7.3)
  - Confirmed: `gradle-wrapper.properties` pins Gradle 9.6.1 (same version recorded in task 7.3), and a Temurin 25.0.2 JDK is available locally via SDKMAN for toolchain provisioning.
- [x] 1.4 Rebase `next` on `main` so any 3.x fixes landed since `migrate-spring-boot-4` are present
  - Confirmed: `next` is 0 commits behind `upstream/main` (11 ahead) — already contains everything from `main`, no rebase needed.

## 2. Toolchain and CI Flip

- [x] 2.1 Change `JavaLanguageVersion.of(17)` → `of(25)` in `bdk.java-common-conventions.gradle`
  - Done: `buildSrc/src/main/groovy/bdk.java-common-conventions.gradle` toolchain `languageVersion` now `JavaLanguageVersion.of(25)`.
- [x] 2.2 Confirm no module build file reintroduces its own `sourceCompatibility` / `targetCompatibility`
  - Confirmed: no `.gradle` file in the repo declares `sourceCompatibility` or `targetCompatibility`.
- [x] 2.3 Bump `java-version: '17'` → `'25'` in `.github/workflows/build.yml`
  - Done.
- [x] 2.4 Bump `java-version: '17'` → `'25'` in `.github/workflows/cve-scanning-gradle.yml`
  - Done.
- [x] 2.5 Bump `java-version: '17'` → `'25'` in `.github/workflows/release.yml`, and align its `actions/setup-java@v3` with the version used by the other workflows
  - Done: `release.yml` now uses `actions/setup-java@v4` (matching `cve-scanning-gradle.yml`) with `java-version: '25'`.
- [x] 2.6 Deliberately do **not** add a JDK matrix — Java 25 is a floor, not one option among several (D-note in design Non-Goals)
  - Confirmed: no `matrix` block in `build.yml`; single-JDK CI preserved.
- [x] 2.7 Run `./gradlew build` and record every failure; reconcile against the 1.2 inventory and investigate anything new
  - `./gradlew build` on JDK 25 (Temurin 25.0.2, active `sdk`/system default): **BUILD SUCCESSFUL**, 191 actionable tasks, zero failures. Matches the empty 1.2 inventory — nothing new to investigate.

## 3. OpenAPI Generator 6.6.0 → 7.x (D2)

- [x] 3.1 Generate all 5 API specs with 6.6.0 and with 7.x into two separate trees and `diff -r` them
  - Done: 377 classes snapshotted at 6.6.0/jersey2, regenerated at 7.14.0/jersey3, diffed. See `generated-api-diff-summary.md`.
- [x] 3.2 Write a summary of every consumer-visible change in the 377 generated classes — signature changes, nullable wrapping, fluent-setter shape, `equals`/`hashCode`/`toString`, enum representation, required-vs-optional constructor parameters. This summary is a deliverable and feeds task 7.2, not a checkpoint
  - Done: `openspec/changes/adopt-java-25-baseline/generated-api-diff-summary.md`.
- [x] 3.3 Bump `org.openapitools:openapi-generator-gradle-plugin` to 7.x in `buildSrc/build.gradle`, and remove the hand-written transitive override block that exists only because 6.6.0 needed older libraries than `owasp-dependencycheck` 12.2.2
  - Done: bumped to 7.14.0 (latest 7.x the Gradle plugin has published); `resolutionStrategy.force` block removed.
- [x] 3.4 Change `library = 'jersey2'` → `'jersey3'` in both places: `bdk.java-codegen-conventions.gradle` and the `apisToGenerate` loop in `symphony-bdk-core/build.gradle`
  - Done.
- [x] 3.5 Review 7.x `configOptions`: confirm `dateLibrary: "java8"` and `sortParamsByRequiredFlag: "false"` still exist and mean the same thing; decide whether `useJakartaEe` is now the correct way to get Jakarta output; resolve the fate of `org.openapitools:jackson-databind-nullable:0.2.6` against 7.x's nullable strategy and the Jackson decision from `migrate-spring-boot-4`
  - Confirmed both options unchanged in 7.14.0 (verified against `CodegenConstants`/`AbstractJavaCodegen` source). `useJakartaEe` is forced `true` unconditionally by `library=jersey3` (not a config choice). `jackson-databind-nullable:0.2.11` kept as-is — unused by generation either version, retained only for `symphony-bdk-http-jersey`'s `JsonNullableJackson3Module` registration.
- [x] 3.6 Re-justify each of the three custom templates (`api.mustache`, `pojo.mustache`, `modelInnerEnum.mustache`) against 7.x upstream — **delete** any whose customization 7.x now handles natively, rather than porting it forward
  - `api.mustache` and `modelInnerEnum.mustache`: kept unchanged, regenerated output identical to 6.6.0 aside from the `@Generated` annotation — no native 7.x equivalent exists for BDK's builder shape. `pojo.mustache`: kept, with one real fix — `nameInCamelCase` → `nameInPascalCase` for `addXxxItem`/`putXxxItem` (upstream renamed the variable's meaning between versions; unported it would have silently lowercased every collection builder method name).
- [x] 3.7 Add an assertion that the generated sources contain no `javax.*` imports, so the Jakarta patch currently living in `pojo.mustache` cannot be lost silently in a template rebase
  - Done: `doLast` check added to both `openApiGenerate` (codegen-conventions) and the `apisToGenerate` loop (symphony-bdk-core), failing the build on any `import javax.*` line. `@javax.annotation.Nullable` (JSR-305, inline, no import) correctly does not trip it.
- [x] 3.8 Confirm 7.x's `jersey3` library targets the Jersey version Spring Boot 4 pins (established by `migrate-spring-boot-4` task 1.2) — a mismatch means generated code compiles against a different Jakarta REST generation than the runtime provides
  - No coupling exists: generated model/API classes never import `jakarta.ws.rs`/`javax.ws.rs` (confined to `supportingFiles`, which are suppressed). Runtime Jersey version is independently pinned via `symphony-bdk-bom`'s `jersey-bom:4.0.0` import.
- [x] 3.9 Fix the hand-written `symphony-bdk-http-api` invoker seam (`ApiClient`, `ApiResponse`, `Pair`, `TypeReference`) if 7.x changed how generated APIs call into it — `supportingFiles: "false"` means BDK owns these by hand
  - No change needed: `api.mustache` output is byte-identical (aside from cosmetics) between versions, so the calling convention into the invoker seam is unchanged. Confirmed empirically — generated `com/symphony/bdk/http/api` dirs remain empty under `supportingFiles: "false"`.
- [x] 3.10 Fix every hand-written call site affected by a changed generated signature
  - None found: repo-wide grep for `*AllOf` (the only removed symbols) returns no hand-written hits; full `./gradlew build` is green with no source changes outside the two `buildSrc`/module `build.gradle` files and the one template line.
- [x] 3.11 Confirm `skipOverwrite = true` and the `globalProperties` block still behave as intended under 7.x
  - Confirmed: `supportingFiles: "false"` still suppresses all jersey3 supportingFiles; `skipOverwrite` still only affects already-existing output files. Full build green.

## 4. Test Infrastructure on JDK 25

- [x] 4.1 Add an explicit Mockito `-javaagent` to the test JVM arguments in `bdk.java-common-conventions.gradle`, resolved from the Mockito artifact (D3)
  - Done: added a `mockitoAgent` configuration (resolved from `org.mockito:mockito-core`, versioned via the BOM platform, `transitive = false`) and wired `test { doFirst { jvmArgs "-javaagent:${configurations.mockitoAgent.asPath}" } }` in `bdk.java-common-conventions.gradle`. `doFirst` is required — eager `jvmArgs` in the `test {}` block resolved the configuration before the `dependencies {}` block added to it, failing with "Cannot mutate the dependencies of configuration ... after it was resolved".
- [x] 4.2 Confirm no `-XX:+EnableDynamicAgentLoading` suppression is used anywhere (D3)
  - Confirmed: no hits for `EnableDynamicAgentLoading` in the repo.
- [x] 4.3 Confirm the Lombok version pinned in `symphony-bdk-bom` compiles all 13 Lombok-consuming modules on JDK 25
  - Confirmed: `./gradlew build jacocoTestCoverageVerification` on Temurin 25.0.2 (active default JDK) is `BUILD SUCCESSFUL`, Lombok 1.18.46 pinned in the BOM unchanged.
- [x] 4.4 Confirm the 3 ArchUnit architecture tests read class file version 69 — `CoreArchitectureTest`, `SymphonyGroupExtensionArchitectureTest`, `bdk-spring-boot-example/ArchitectureTest`
  - Confirmed: all 3 pass running on JDK 25 (`./gradlew :symphony-bdk-core:test --tests "*CoreArchitectureTest*" :symphony-bdk-extensions:symphony-group-extension:test --tests "*SymphonyGroupExtensionArchitectureTest*" :symphony-bdk-examples:bdk-spring-boot-example:test --tests "*ArchitectureTest*"` → BUILD SUCCESSFUL).
- [x] 4.5 Confirm `jacocoTestCoverageVerification` passes in every module on JDK 25 bytecode; if thresholds shift because of instrumentation counting rather than test changes, adjust them in a dedicated commit naming the JDK
  - Confirmed: every module's `jacocoTestCoverageVerification` passes unchanged on JDK 25 bytecode (JaCoCo 0.8.15 already supports class file 69). No threshold adjustment needed.
- [x] 4.6 Confirm the MapStruct processor and the Spring Boot configuration processor run on JDK 25, and that the generated `UserDetailMapperImpl` is unchanged from the Java 17 build
  - Confirmed: `build/generated/sources/annotationProcessor/java/main/.../UserDetailMapperImpl.java` regenerated (`environment: Java 25.0.2 (Eclipse Adoptium)` in the `@Generated` comment) with identical mapping logic to the Java 17 build. Spring Boot configuration processor ran without error as part of the same green build.

## 5. Release Pipeline (D5)

- [x] 5.1 Add a branch guard to `release.yml`: `v3.*` tags must target the `3.x` branch, `v4.*` tags must target `main` — the current `^v[0-9]+\.[0-9]+\.[0-9]+$` regex validates tag shape only, and a mis-targeted release to Maven Central is immutable
  - Done: `release.yml`'s version-derivation step now checks `github.event.release.target_commitish` — major version `3` must target `3.x`, any other major must target `main`. Also fixed a stale "Set up JDK 17" step name left over from task 2.5's version bump.
- [x] 5.2 Verify the CLI distribution still builds and runs on JDK 25: `:symphony-bdk-cli:installDist`, then execute `bin/bdk`
  - Confirmed: `./gradlew :symphony-bdk-cli:installDist` on Temurin 25.0.2 → BUILD SUCCESSFUL; `bin/bdk --help` runs and exits 0 with the expected usage output.
- [x] 5.3 Verify `publishToSonatype` and the signing configuration work under the new JDK and Gradle combination
  - Confirmed on JDK 25 without touching the real Sonatype endpoint (no credentials/GPG key available locally, and it's a remote/immutable target): `./gradlew tasks --all` resolves the full `nexus-publish-plugin` 1.3.0 task graph (`publishAllPublicationsToSonatypeRepository` etc.) with no configuration errors; `-PprojectVersion=99.0.0` (a release-shaped version, flips `isReleaseVersion` true) shows `signMavenPublication` correctly enters the task graph ahead of publish, confirming the `signing` plugin still wires up under Gradle 9 / JDK 25. Actual credentialed publish is exercised by the real release workflow in CI, not locally.
- [x] 5.4 Verify `./gradlew dependencyCheck` runs on JDK 25 and review `allow-list.xml` against the Spring Boot 4 dependency set
  - `./gradlew dependencyCheck` is now ambiguous under Gradle 9 (candidates: `dependencyCheckAggregate`/`Analyze`/`Purge`/`Update`); the correct multi-module task is `dependencyCheckAggregate` (task 7.5/CLAUDE.md should record this).
  - First run on JDK 25 surfaced ~130 CVEs across 80 dependencies, none covered by the existing `allow-list.xml` (written for the Spring Boot 3.5.x / Netty 4.1.x dependency set). Nearly all were against major versions `migrate-spring-boot-4` already adopted — Spring Boot 4.0.0/Spring Framework 7.0.1, Netty 4.2.7.Final, Tomcat 11.0.14, Apache HttpComponents 5.x — that this was the first `dependencyCheck` run since. Root cause, not a JDK-25 problem.
  - Resolved via same-minor patch bumps in `symphony-bdk-bom/build.gradle`: `spring-boot-dependencies` 4.0.0 → 4.0.7 (pulls Spring Framework 7.0.8, cleared all Spring-side CVEs), explicit `netty-bom` 4.2.16.Final (matches the version `mockserver-netty:7.5.0` itself was built against — 4.2.17.Final broke `ApiClientWebClientTest` via `ClientException`, 4.2.16.Final is green), `tomcat-embed-core`/`tomcat-embed-websocket` 11.0.14 → 11.0.24.
  - Three findings needed a decision beyond same-minor scope (user confirmed): bumped `httpclient5` 5.5.2 → 5.6.4 and `httpcore5`/`httpcore5-h2` 5.3.6 → 5.4.3 (minor bump, real DoS/connection-leak fixes per Apache advisories, ahead of what `spring-boot-dependencies:4.0.7` pins); bumped `bdk-ai-agent-example`'s transitive `opennlp-tools` 2.5.9 → 2.5.11 via `implementation` (CVE-2026-63317 fix — first attempt used `runtimeOnly`, which missed the compileClasspath copy of the transitive dependency); added a documented `allow-list.xml` suppression for CVE-2026-66299 (Tomcat's bundled WebSocket *examples* webapp DoS — inapplicable, Spring Boot's embedded Tomcat never ships that webapp).
  - Final state: `./gradlew dependencyCheckAggregate` → "Found 0 vulnerabilities"; `./gradlew build` green on JDK 25 with all version changes in place.

## 6. Full Verification

- [x] 6.1 `./gradlew build jacocoTestReport jacocoTestCoverageVerification` green on Java 25 + Spring Boot 4
  - Confirmed: `./gradlew build jacocoTestReport jacocoTestCoverageVerification` on Temurin 25.0.2 → BUILD SUCCESSFUL, 221 actionable tasks.
- [x] 6.2 Run the starter smoke tests from `migrate-spring-boot-4` section 2 — they are the check that autoconfiguration still works on the new JDK
  - Confirmed: `SymphonyBdkCoreSmokeTest`, `SymphonyBdkAppSmokeTest`, `SymphonyBdkSpringBootTestSmokeTest` all pass with `--rerun` on Temurin 25.0.2.
- [x] 6.3 Build every example module, including `bdk-ai-agent-example` (langchain4j) and `bdk-multi-instances-example`
  - Confirmed: all 6 modules under `symphony-bdk-examples` build successfully on Temurin 25.0.2.
- [x] 6.4 Confirm every published jar's class file version corresponds to Java 25 and that no module targets a different version
  - Confirmed: every BDK-compiled class across all module jars (and BOOT-INF/classes in the two Boot example fat jars) reads class file major version 69 (Java 25). The only version-61 (Java 17) classes found are Spring Boot's own bundled `org/springframework/boot/loader/*` classes inside the fat jars — compiled by the Spring Boot Gradle plugin itself, not BDK code, and expected.
- [x] 6.5 `./gradlew publishToMavenLocal` and confirm the coordinate set matches what `migrate-spring-boot-4` established
  - Confirmed: `./gradlew publishToMavenLocal` on Temurin 25.0.2 → BUILD SUCCESSFUL. Published coordinate set under `org.finos.symphony.bdk` matches `migrate-spring-boot-4` task 9.5's expectation, notably with `symphony-bdk-http-jersey2` absent (module is `symphony-bdk-http-jersey`).

## 7. Documentation and the 3.x Commitment

- [x] 7.1 State Java 25 as a hard requirement in `docs/migration-4.x.md` and in the README — required, not recommended
  - `docs/migration-4.x.md` already stated this (written during `migrate-spring-boot-4`'s archival with the full 4.x plan in view). Fixed the one place that had gone stale now that this change actually flipped the toolchain: `README.md`'s "Before you start" section said JDK 17 and claimed "`main` has not yet moved its build toolchain to 25" — updated to JDK 25, required not recommended, with a pointer to the `3.x` branch for JDK 17 builds.
- [x] 7.2 Add the generated-API change summary from 3.2 to the migration guide, with before/after signatures for anything consumers construct or read directly
  - Added `## 8. Generated API changes` to `docs/migration-4.x.md`, condensed from the "Consumer-facing summary" section of `generated-api-diff-summary.md`: the 21 unreferenced `*AllOf` classes removed, and an explicit statement that no other generated class changes shape, signatures, `equals`/`hashCode`/`toString`, or annotations.
- [x] 7.3 Document that consumers using `symphony-bdk-test-jupiter` / `symphony-bdk-test-spring-boot` inherit the explicit Mockito agent configuration and must run tests on JDK 25
  - Added to `docs/migration-4.x.md` section 7 ("Test dependencies"): the self-attach restriction consumers hit and the Gradle `-javaagent` configuration to work around it (mirroring D3's fix in `bdk.java-common-conventions`), plus the same warning against `-XX:+EnableDynamicAgentLoading`. Cross-linked from `docs/test.md`'s intro since that's where a consumer building on these test modules is more likely to land first.
- [x] 7.4 **Record the 3.x support window** in the migration guide and release notes (D1). The guide cannot answer "what if I cannot move to Java 25" without it, and 4.0.0 must not ship without an answer
  - Already recorded: `docs/migration-4.x.md`'s "Support window" section and `README.md`'s top banner both state the 6-month critical-security-fix window on `3.x` following 4.0.0's release, with the caveat that Spring itself gives no guarantee for superseded packages. There is no static release-notes file in this repo — GitHub release notes are auto-categorized from PR labels via `.github/release.yml` at publish time — so the migration guide and README are the durable, linkable statement of the window that a release description can point to.
- [x] 7.5 Update `CLAUDE.md` with the Java 25 baseline and the JDK requirement for contributors
  - Updated the Requirements section (toolchain target 17 → 25, clarified contributors need a JDK 25 available for toolchain provisioning), the `bdk.java-codegen-conventions` description (Jersey2 → Jersey3), the `bdk.java-common-conventions` description (notes the explicit Mockito agent), and the dependency-check command (`dependencyCheck` → `dependencyCheckAggregate`, per task 5.4's finding that the bare task name is now ambiguous under Gradle 9).
- [x] 7.6 Confirm the versioned documentation set up by `migrate-spring-boot-4` task 10.3 keeps 3.x docs reachable for consumers who stay on the maintenance line
  - Confirmed, no change needed: `.github/workflows/docs.yml` (from `migrate-spring-boot-4`'s archival commit `baa374a2`) already builds `main`'s docs at the site root and the `3.x` branch's own `docs/` into a `/3.x/` subtree, and `docs/_config.yml` carries a "BDK 3.x docs" aux link to `/3.x/`. This change added no new documentation surface that needs a 3.x equivalent.
