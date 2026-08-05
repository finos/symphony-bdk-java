## 1. Gradle 9 Compatibility Spike (blocking — resolves D1)

- [x] 1.1 Bump `gradle/wrapper/gradle-wrapper.properties` to the chosen Gradle 9.x on a throwaway branch and run `./gradlew :symphony-bdk-core:openApiGenerate` — record whether `openapi-generator-gradle-plugin:6.6.0` works
- [x] 1.2 Run `./gradlew build --warning-mode all` and capture the full deprecation list; this is the authoritative task list for section 2, superseding the statically-found call sites
- [x] 1.3 Verify `owasp-dependencycheck:12.2.2` and `com.github.ben-manes.versions` still resolve under Gradle 9 (`buildSrc/build.gradle` already carries a manual override block for a 6.6.0-vs-dependencycheck conflict)
- [x] 1.4 Per D1, choose and record the branch: proceed as scoped / (a) pull generator bump forward / (b) defer Gradle 9 to `adopt-java-25-baseline`. Update this change's proposal scope if (a) or (b)

  **D1 spike result — proceed as scoped.** `openapi-generator-gradle-plugin:6.6.0`'s `GenerateTask` runs cleanly under Gradle 9.6.1 (verified via `:symphony-bdk-core:generateAgent`); the default no-arg `openApiGenerate` task fails with "generator name must be specified" but that is pre-existing, unconfigured-task behaviour unrelated to Gradle 9. `com.github.ben-manes.versions` needed a bump from 0.42.0 → 0.54.0 (0.42.0 calls a `LenientConfiguration` API removed in Gradle 9); `org.owasp.dependencycheck:12.2.2` resolves and applies with no changes needed. Two additional Gradle-9 interlocks were found beyond D1's scope and fixed as part of section 2: cross-project `project(':x').sourceSets` access in two Spring Boot starter modules (dynamic property forwarding on `ProjectDependency` was removed — fixed via a top-level `project(':symphony-bdk-core')` accessor + `evaluationDependsOn`), and the `org.springframework.boot` Gradle plugin versions pinned in the two example modules (3.2.2, 3.5.4) predated Gradle 9 support and were bumped to 3.5.16 to match the BOM's existing `spring-boot-dependencies` constraint — no Spring Boot runtime version change.

## 2. Gradle 9 Migration

- [x] 2.1 Bump the wrapper to Gradle 9.x (`gradle/wrapper/gradle-wrapper.properties`, and commit the regenerated wrapper scripts)
- [x] 2.2 Replace `sourceCompatibility = JavaVersion.VERSION_17` in `bdk.java-common-conventions.gradle` with a `java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }` block (D2)
- [x] 2.3 Confirm `options.compilerArgs << '-parameters'` and `options.encoding = 'UTF-8'` survive the conventions rewrite — both are load-bearing (Spring constructor binding, Jackson parameter names)
- [x] 2.4 Replace `project.buildDir` with `layout.buildDirectory` in `bdk.java-codegen-conventions.gradle`, `symphony-bdk-core/build.gradle`, and `symphony-bdk-extensions/symphony-group-extension/build.gradle`
- [x] 2.5 Convert the two `tasks.create(...)` calls in `symphony-bdk-core/build.gradle`'s `apisToGenerate` loop (`download$api`, `generate$api`) to `tasks.register(...)`, preserving the `compileJava.dependsOn` / `sourcesJar.dependsOn` wiring
- [x] 2.6 Work through the remaining deprecations from 1.2 until `./gradlew build --warning-mode all` is clean

  Additional deprecations found beyond the statically-known 5: `Project.getProperties()` (root `build.gradle`, 4 call sites → `findProperty`), implicit parent-project property lookup of `projectVersion` in the `allprojects` block (→ `rootProject.ext.projectVersion`), and Groovy space-assignment syntax for `username`/`password`/`url`/`required` in the two publishing blocks (`bdk.java-publish-conventions.gradle` and `symphony-bdk-bom/build.gradle`). `./gradlew clean build --warning-mode all` is now warning-free.
- [x] 2.7 Add an explicit `jacoco { toolVersion = '...' }` to `bdk.java-common-conventions.gradle` rather than relying on the Gradle-bundled default (pinned to `0.8.15`, latest stable)

## 3. Bytecode-Processing Tooling Bumps

- [ ] 3.1 Bump the explicit `net.bytebuddy:byte-buddy` pin in `symphony-bdk-core/build.gradle` (currently 1.12.19) to a release supporting class file version 69
- [ ] 3.2 Bump `org.mapstruct:mapstruct` and `org.mapstruct:mapstruct-processor` in `symphony-bdk-core/build.gradle` (currently 1.4.2.Final) to 1.6.x
- [ ] 3.3 Diff the generated `UserDetailMapperImpl` before and after the MapStruct bump; investigate any change in null-handling or unmapped-property behaviour (D5)
- [ ] 3.4 Bump `com.tngtech.archunit:archunit-junit5` in the BOM (currently 1.2.1); confirm the 3 architecture tests still pass — `CoreArchitectureTest`, `SymphonyGroupExtensionArchitectureTest`, `bdk-spring-boot-example/ArchitectureTest`
- [ ] 3.5 Bump `org.assertj:assertj-core` in the BOM if a newer release is available for the JaCoCo/JDK combination
- [ ] 3.6 Verify `jacocoTestCoverageVerification` still passes in every module after the JaCoCo toolVersion bump; if thresholds shift because of JaCoCo counting changes rather than test changes, adjust them in a dedicated commit naming the JaCoCo version

## 4. Lombok Version Ownership

- [ ] 4.1 Add an explicit `org.projectlombok:lombok` constraint to `symphony-bdk-bom/build.gradle`, at the version currently resolved from `spring-boot-dependencies:3.5.16` or newer
- [ ] 4.2 Verify all 13 Lombok-consuming modules still compile with the pinned version
- [ ] 4.3 Confirm the pinned version has published JDK 25 support (needed later by `adopt-java-25-baseline`, recorded here so the check isn't repeated)

## 5. MockServer Replacement (D3)

- [ ] 5.1 Check whether a current MockServer release tracks Netty 4.2; if so, reduce this section to a version bump and skip 5.2–5.4
- [ ] 5.2 Choose the replacement (WireMock or OkHttp MockWebServer) and add it to the BOM
- [ ] 5.3 Migrate the 4 test files using `mockserver` off it, preserving assertion coverage
- [ ] 5.4 Remove `org.mock-server:mockserver-netty` from `symphony-bdk-bom` and from `symphony-bdk-core` / `symphony-bdk-http-webclient` test dependencies

## 6. Dead Dependency Removal (D4 — own commit)

- [ ] 6.1 Remove `javax.xml.bind:jaxb-api:2.3.1` from `symphony-bdk-core/build.gradle` (verified: zero source usage anywhere in the tree)
- [ ] 6.2 Replace `javax.annotation:jsr250-api:1.0` with `jakarta.annotation:jakarta.annotation-api` in `bdk.java-codegen-conventions.gradle`, and confirm the generated sources still compile
- [ ] 6.3 Remove `org.projectreactor:reactor-spring:1.0.1.RELEASE` from `symphony-bdk-bom` after confirming no module resolves it
- [ ] 6.4 Land 6.1–6.3 plus the Lombok constraint from 4.1 as a single commit whose message names each removed coordinate and why

## 7. Forward-Compatibility Verification (does not change committed config)

- [ ] 7.1 On a throwaway branch, flip the toolchain to `JavaLanguageVersion.of(25)` and run `./gradlew build` — record every failure as a task in `adopt-java-25-baseline`, then discard the branch
- [ ] 7.2 Specifically record: whether Mockito's dynamic agent attach warns or fails, whether ArchUnit reads class file 69, whether Lombok compiles, and whether `openApiGenerate` runs
- [ ] 7.3 Confirm the chosen Gradle 9.x can provision a JDK 25 toolchain (needed by `adopt-java-25-baseline`)

## 8. Release

- [ ] 8.1 Confirm `./gradlew build jacocoTestReport jacocoTestCoverageVerification` is green on JDK 17 (the CI command)
- [ ] 8.2 Confirm `./gradlew publishToMavenLocal` produces the same module coordinates as 3.5.x
- [ ] 8.3 Draft 3.6.0 release notes covering the two consumer-visible BOM changes from D4's migration plan
- [ ] 8.4 Update `CLAUDE.md` with the new Gradle version and the minimum daemon JDK for contributors
