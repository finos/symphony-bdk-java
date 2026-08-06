## 1. Investigation (blocking — resolves the open questions before any code moves)

- [x] 1.1 Determine where Spring Boot 4 places `@ConditionalOnMissingBean`, `@ConditionalOnProperty`, `@ConditionalOnBean`, `@ConditionalOnExpression`, `@SpringBootApplication`, and the `AutoConfigurations` test helper; record the old → new mapping (25 of 28 references are the three `@ConditionalOn*` annotations, so this sizes section 3)
- [x] 1.2 Determine which Jersey version `spring-boot-dependencies:4.x` pins, and whether it implements Jakarta REST 3.1 or 4.0
- [x] 1.3 Verify against the published `jackson-annotations` 3.x artifact that `com.fasterxml.jackson.annotation` is retained — this decides whether the generated-code cost is 0 files or 377
- [x] 1.4 Determine whether a Jackson 3 JSON provider exists for the Jersey version from 1.2 (the D2 gate)
- [x] 1.5 Record the D2 decision — Jackson 3, or ship 4.0 on Jackson 2 — and update this change's scope accordingly. Do not proceed to section 6 until this is written down
- [x] 1.6 Confirm the `AutoConfiguration.imports` file path/name is unchanged in Spring Boot 4 (`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`)

## 2. Starter Smoke Tests — written first, against Spring Boot 3.5 (D5)

- [x] 2.1 Add a smoke test to `symphony-bdk-core-spring-boot-starter` that boots a minimal `@SpringBootApplication` depending only on the starter, and asserts `SymphonyBdk` and the core service beans are present via real autoconfiguration discovery (not `AutoConfigurations`)
- [x] 2.2 Add the equivalent smoke test to `symphony-bdk-app-spring-boot-starter`, asserting the app-layer beans, the health indicator, and the circle-of-trust endpoints are wired
- [x] 2.3 Add a consumer-perspective test for `symphony-bdk-test-spring-boot` that verifies the re-exported test stack works from a dependent module
- [x] 2.4 Confirm all three pass on Spring Boot 3.5.16 before any bump — a smoke test written after the bump proves nothing about whether it would have caught the failure
- [x] 2.5 Merge sections 1–2 to `next` and rebase on `main` before continuing

## 3. Spring Boot 4 Platform Bump

- [x] 3.1 Change `symphony-bdk-bom` to `platform('org.springframework.boot:spring-boot-dependencies:4.x')`
- [x] 3.2 Remove `platform('io.netty:netty-bom:4.1.136.Final')` and its CVE comment (D3)
- [x] 3.3 Remove the three `org.apache.tomcat.embed:tomcat-embed-*:10.1.57` constraints and their CVE comment (D3)
- [x] 3.4 Remove `jakarta.ws.rs:jakarta.ws.rs-api:3.1.0` and `jakarta.validation:jakarta.validation-api:3.0.2` from `symphony-bdk-bom`, and the duplicate `jakarta.validation-api:3.0.2` from `symphony-bdk-app-spring-boot-starter/build.gradle` — do this early so EE 11 incompatibilities surface now, not at the end
- [x] 3.5 Realign or remove the explicit `jersey-bom` import per 1.2
- [x] 3.6 Land 3.2–3.4 as their own commit naming each removed coordinate, matching the BOM-hygiene discipline from `modernize-build-toolchain`
- [x] 3.7 Confirm the toolchain in `bdk.java-common-conventions` is still `JavaLanguageVersion.of(17)` — this change must not flip it (D1)

## 4. Autoconfiguration Retargeting

- [x] 4.1 Update imports in `SymphonyBdkAutoConfiguration` and the rest of `symphony-bdk-core-spring-boot-starter` per the 1.1 mapping
- [x] 4.2 Update imports in `SymphonyBdkAppAutoConfiguration` and the rest of `symphony-bdk-app-spring-boot-starter` per the 1.1 mapping
- [x] 4.3 Update the 3 `AutoConfigurations` test-helper usages
- [x] 4.4 Update both `META-INF/spring/...AutoConfiguration.imports` files if 1.6 found the path changed
- [x] 4.5 Retarget starter dependency coordinates for Spring Boot 4's module split: `spring-boot-starter-web`, `-actuator`, `-validation`, `spring-boot-configuration-processor` (app starter); `spring-boot-starter` (core starter); `spring-boot-starter-webflux` (`symphony-bdk-http-webclient`); `spring-boot-starter-web` + `-test` (`symphony-bdk-test-spring-boot`)
- [x] 4.6 Fix `SymphonyBdkHealthIndicator` against Actuator / Micrometer 2
- [x] 4.7 Fix `CircleOfTrustController` and the app-layer exception handling against Spring Framework 7 MVC
- [x] 4.8 Fix `BdkActivityConfig.SlashAnnotationProcessor` if bean-post-processor or `@ConditionalOn*` semantics moved
- [x] 4.9 Run the section 2 smoke tests — they are the acceptance criterion for this section, not the unit tests

## 5. Jakarta EE 11 Fallout

- [x] 5.1 Rebuild and fix any `jakarta.validation` breakage from EE 10 → EE 11 (`@NotBlank` usages in the app starter models)
- [x] 5.2 Rebuild and fix `symphony-bdk-http-jersey2`'s `jakarta.ws.rs` usages against the version from 1.2 (24 imports across the module: `Client`, `WebTarget`, `Entity`, `Invocation`, `ClientRequestFilter`, `ClientResponseFilter`, `ContextResolver`, `Provider`, `MultivaluedHashMap`, `Form`, `GenericType`)
- [x] 5.3 Fix the `jakarta.servlet.http` usages in the app starter against Servlet 6.1
- [x] 5.4 Fix the `jakarta.annotation.PostConstruct` usages
- [x] 5.5 Confirm Netty 4.2 and Tomcat 11 cause no runtime breakage in `symphony-bdk-http-webclient` and the app starter — the smoke tests from section 2 are the check
- [x] 5.6 Re-run `./gradlew dependencyCheck` against the Spring Boot 4 platform; add only overrides that run genuinely justifies, with fresh comments (D3)

## 6. Jackson — conditional on the D2 decision from 1.5

- [x] 6.1 *(Jackson 3 only)* Migrate the 22 hand-written `com.fasterxml.jackson.databind` / `.core` usages to `tools.jackson.*`, module by module: `symphony-bdk-config` (`BdkConfigLoader`, `BdkConfigParser`), `symphony-bdk-core` (7 files incl. `JwtHelper`, `AuthSessionImpl`, `MessageParser`, `Message`, `ExtensionService`, `FormReplyActivity`, `FormReplyContext`, `InputTokenizer`), `symphony-bdk-http-jersey` (`JSON`, `RFC3339DateFormat`), the starters, `symphony-group-extension`, `symphony-bdk-cli` (`internal/Json`)
- [x] 6.2 *(Jackson 3 only)* Change `BdkConfigParser`'s 3 public `JsonNode` signatures and note them in the migration guide as a breaking API change
- [x] 6.3 *(Jackson 3 only)* Rewrite `JSON.java`'s `ContextResolver<ObjectMapper>` against the Jackson 3 provider from 1.4
- [x] 6.4 *(Jackson 3 only)* Migrate the 5 test-side databind usages (`MockApiClient`, `JwtHelperTest`, `CircleOfTrustControllerTest`, `SymphonyBdkMockedConfiguration`, the 3 CLI tests)
- [x] 6.5 *(Jackson 3 only)* Replace `com.fasterxml.jackson.datatype.jsr310.JavaTimeModule`, `YAMLMapper`, `JavaPropsMapper`, and `jackson-databind-nullable` with their Jackson 3 equivalents; if `org.openapitools:jackson-databind-nullable` has no Jackson 3 release, this feeds back into 1.5
- [x] 6.6 *(Jackson 2 only)* Verify Jackson 2 and Spring Boot 4 coexist, and document in the migration guide that BDK 4.0 remains on Jackson 2 with Jackson 3 targeted for 4.1 — N/A, D2 adopted Jackson 3
- [x] 6.7 Assert there is exactly one Jackson databind implementation on the runtime classpath — a dependency-verification test, so the split classpath D2 rejects cannot appear later by accident

## 7. Module Rename: `http-jersey2` → `http-jersey` (D7 — own commit)

- [ ] 7.1 Rename the directory `symphony-bdk-http/symphony-bdk-http-jersey2` → `symphony-bdk-http-jersey` and update `settings.gradle`
- [ ] 7.2 Update the `symphony-bdk-bom` constraint to the new artifactId
- [ ] 7.3 Update all consuming modules: `symphony-bdk-core` (test), `symphony-bdk-core-spring-boot-starter`, `symphony-bdk-cli`, `bdk-ai-agent-example`, and any other example
- [ ] 7.4 Update `docs/` and every code sample referencing the old coordinate
- [ ] 7.5 Confirm no relocation POM or forwarding artifact is published (D7)

## 8. JSR-305 → JSpecify (D4 — own commit)

- [ ] 8.1 Add JSpecify to `symphony-bdk-bom`
- [ ] 8.2 Replace the 52 `javax.annotation.Nonnull` and 30 `javax.annotation.Nullable` usages, module by module — not as a global find-and-replace, because JSpecify's `@NullMarked` defaulting changes what an *unannotated* type means
- [ ] 8.3 Decide per module whether to apply `@NullMarked` at package level, and verify the resulting contract matches the previous JSR-305 intent
- [ ] 8.4 Drop `com.google.code.findbugs:jsr305` from modules that only used it for nullability; keep it where `bdk.java-codegen-conventions` requires it for generated code
- [ ] 8.5 Confirm the 3 ArchUnit architecture tests still pass, and add a rule forbidding `javax.annotation.Nullable`/`Nonnull` so it cannot come back

## 9. Examples and Verification

- [ ] 9.1 Build `bdk-spring-boot-example` and `bdk-app-spring-boot-example` against Spring Boot 4; fix `ApiExceptionHandler` and the example `ArchitectureTest`
- [ ] 9.2 Build the non-Spring examples (`bdk-core-examples`, `bdk-multi-instances-example`, `bdk-group-example`, `bdk-ai-agent-example`) — the last one also pulls `langchain4j`, verify no Jackson or Netty conflict
- [ ] 9.3 Build `symphony-bdk-cli` and verify `installDist` still produces a working `bin/bdk`
- [ ] 9.4 Full green run of `./gradlew build jacocoTestReport jacocoTestCoverageVerification` on **Java 17** + Spring Boot 4 — the definition of done for this change (D1)
- [ ] 9.5 Confirm `./gradlew publishToMavenLocal` publishes the expected coordinates, with `symphony-bdk-http-jersey2` absent

## 10. Documentation

- [ ] 10.1 Write `docs/migration-4.x.md` covering all 7 items from the design's migration plan
- [ ] 10.2 State prominently that BDK 4.x requires Spring Boot 4.x and that Spring Boot 3 + BDK 4 is unsupported (D6)
- [ ] 10.3 Set up versioned documentation so `symphony-bdk-java.finos.org` serves 3.x and 4.x separately, and 3.x consumers are never shown 4.x instructions
- [ ] 10.4 Update `docs/extension.md` and any doc referencing the `jersey2` coordinate or JSR-305 annotations
- [ ] 10.5 Note the Java requirement in the migration guide as **25** (what 4.0.0 actually ships), not the 17 this intermediate state builds against
- [ ] 10.6 Record the 3.x support window in the migration guide once decided — the guide cannot answer "what if I can't move to Java 25" without it
