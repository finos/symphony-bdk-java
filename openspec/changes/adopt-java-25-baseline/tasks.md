## 1. Prerequisites

- [ ] 1.1 Confirm `migrate-spring-boot-4` is merged to `next` and `./gradlew build jacocoTestReport jacocoTestCoverageVerification` is green on Java 17 + Spring Boot 4
- [ ] 1.2 Retrieve the residual JDK 25 failure list recorded by `modernize-build-toolchain` tasks 7.1–7.2; it is the authoritative starting inventory for sections 2 and 4
- [ ] 1.3 Confirm the Gradle version on `next` can provision a JDK 25 toolchain (recorded in `modernize-build-toolchain` task 7.3)
- [ ] 1.4 Rebase `next` on `main` so any 3.x fixes landed since `migrate-spring-boot-4` are present

## 2. Toolchain and CI Flip

- [ ] 2.1 Change `JavaLanguageVersion.of(17)` → `of(25)` in `bdk.java-common-conventions.gradle`
- [ ] 2.2 Confirm no module build file reintroduces its own `sourceCompatibility` / `targetCompatibility`
- [ ] 2.3 Bump `java-version: '17'` → `'25'` in `.github/workflows/build.yml`
- [ ] 2.4 Bump `java-version: '17'` → `'25'` in `.github/workflows/cve-scanning-gradle.yml`
- [ ] 2.5 Bump `java-version: '17'` → `'25'` in `.github/workflows/release.yml`, and align its `actions/setup-java@v3` with the version used by the other workflows
- [ ] 2.6 Deliberately do **not** add a JDK matrix — Java 25 is a floor, not one option among several (D-note in design Non-Goals)
- [ ] 2.7 Run `./gradlew build` and record every failure; reconcile against the 1.2 inventory and investigate anything new

## 3. OpenAPI Generator 6.6.0 → 7.x (D2)

- [ ] 3.1 Generate all 5 API specs with 6.6.0 and with 7.x into two separate trees and `diff -r` them
- [ ] 3.2 Write a summary of every consumer-visible change in the 377 generated classes — signature changes, nullable wrapping, fluent-setter shape, `equals`/`hashCode`/`toString`, enum representation, required-vs-optional constructor parameters. This summary is a deliverable and feeds task 7.2, not a checkpoint
- [ ] 3.3 Bump `org.openapitools:openapi-generator-gradle-plugin` to 7.x in `buildSrc/build.gradle`, and remove the hand-written transitive override block that exists only because 6.6.0 needed older libraries than `owasp-dependencycheck` 12.2.2
- [ ] 3.4 Change `library = 'jersey2'` → `'jersey3'` in both places: `bdk.java-codegen-conventions.gradle` and the `apisToGenerate` loop in `symphony-bdk-core/build.gradle`
- [ ] 3.5 Review 7.x `configOptions`: confirm `dateLibrary: "java8"` and `sortParamsByRequiredFlag: "false"` still exist and mean the same thing; decide whether `useJakartaEe` is now the correct way to get Jakarta output; resolve the fate of `org.openapitools:jackson-databind-nullable:0.2.6` against 7.x's nullable strategy and the Jackson decision from `migrate-spring-boot-4`
- [ ] 3.6 Re-justify each of the three custom templates (`api.mustache`, `pojo.mustache`, `modelInnerEnum.mustache`) against 7.x upstream — **delete** any whose customization 7.x now handles natively, rather than porting it forward
- [ ] 3.7 Add an assertion that the generated sources contain no `javax.*` imports, so the Jakarta patch currently living in `pojo.mustache` cannot be lost silently in a template rebase
- [ ] 3.8 Confirm 7.x's `jersey3` library targets the Jersey version Spring Boot 4 pins (established by `migrate-spring-boot-4` task 1.2) — a mismatch means generated code compiles against a different Jakarta REST generation than the runtime provides
- [ ] 3.9 Fix the hand-written `symphony-bdk-http-api` invoker seam (`ApiClient`, `ApiResponse`, `Pair`, `TypeReference`) if 7.x changed how generated APIs call into it — `supportingFiles: "false"` means BDK owns these by hand
- [ ] 3.10 Fix every hand-written call site affected by a changed generated signature
- [ ] 3.11 Confirm `skipOverwrite = true` and the `globalProperties` block still behave as intended under 7.x

## 4. Test Infrastructure on JDK 25

- [ ] 4.1 Add an explicit Mockito `-javaagent` to the test JVM arguments in `bdk.java-common-conventions.gradle`, resolved from the Mockito artifact (D3)
- [ ] 4.2 Confirm no `-XX:+EnableDynamicAgentLoading` suppression is used anywhere (D3)
- [ ] 4.3 Confirm the Lombok version pinned in `symphony-bdk-bom` compiles all 13 Lombok-consuming modules on JDK 25
- [ ] 4.4 Confirm the 3 ArchUnit architecture tests read class file version 69 — `CoreArchitectureTest`, `SymphonyGroupExtensionArchitectureTest`, `bdk-spring-boot-example/ArchitectureTest`
- [ ] 4.5 Confirm `jacocoTestCoverageVerification` passes in every module on JDK 25 bytecode; if thresholds shift because of instrumentation counting rather than test changes, adjust them in a dedicated commit naming the JDK
- [ ] 4.6 Confirm the MapStruct processor and the Spring Boot configuration processor run on JDK 25, and that the generated `UserDetailMapperImpl` is unchanged from the Java 17 build

## 5. Release Pipeline (D5)

- [ ] 5.1 Add a branch guard to `release.yml`: `v3.*` tags must target the `3.x` branch, `v4.*` tags must target `main` — the current `^v[0-9]+\.[0-9]+\.[0-9]+$` regex validates tag shape only, and a mis-targeted release to Maven Central is immutable
- [ ] 5.2 Verify the CLI distribution still builds and runs on JDK 25: `:symphony-bdk-cli:installDist`, then execute `bin/bdk`
- [ ] 5.3 Verify `publishToSonatype` and the signing configuration work under the new JDK and Gradle combination
- [ ] 5.4 Verify `./gradlew dependencyCheck` runs on JDK 25 and review `allow-list.xml` against the Spring Boot 4 dependency set

## 6. Full Verification

- [ ] 6.1 `./gradlew build jacocoTestReport jacocoTestCoverageVerification` green on Java 25 + Spring Boot 4
- [ ] 6.2 Run the starter smoke tests from `migrate-spring-boot-4` section 2 — they are the check that autoconfiguration still works on the new JDK
- [ ] 6.3 Build every example module, including `bdk-ai-agent-example` (langchain4j) and `bdk-multi-instances-example`
- [ ] 6.4 Confirm every published jar's class file version corresponds to Java 25 and that no module targets a different version
- [ ] 6.5 `./gradlew publishToMavenLocal` and confirm the coordinate set matches what `migrate-spring-boot-4` established

## 7. Documentation and the 3.x Commitment

- [ ] 7.1 State Java 25 as a hard requirement in `docs/migration-4.x.md` and in the README — required, not recommended
- [ ] 7.2 Add the generated-API change summary from 3.2 to the migration guide, with before/after signatures for anything consumers construct or read directly
- [ ] 7.3 Document that consumers using `symphony-bdk-test-jupiter` / `symphony-bdk-test-spring-boot` inherit the explicit Mockito agent configuration and must run tests on JDK 25
- [ ] 7.4 **Record the 3.x support window** in the migration guide and release notes (D1). The guide cannot answer "what if I cannot move to Java 25" without it, and 4.0.0 must not ship without an answer
- [ ] 7.5 Update `CLAUDE.md` with the Java 25 baseline and the JDK requirement for contributors
- [ ] 7.6 Confirm the versioned documentation set up by `migrate-spring-boot-4` task 10.3 keeps 3.x docs reachable for consumers who stay on the maintenance line
