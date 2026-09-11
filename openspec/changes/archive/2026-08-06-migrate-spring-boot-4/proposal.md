## Why

BDK 3.x is built on Spring Boot 3.5, whose OSS support window has closed. Spring Boot 4 / Spring Framework 7 is the supported line, and moving to it is the substance of the BDK 4.x major: Jakarta EE 11, Jackson 3, Netty 4.2, Tomcat 11, Micrometer 2, and a restructured autoconfiguration module layout.

This change does the framework half of the 4.x migration and **stays on Java 17**. That is deliberate. Spring Boot 4 baselines Java 17, so "Spring Boot 4 on Java 17" is a configuration the framework itself supports and tests — a known-good intermediate state. Combining the framework move and the JDK move into one step would mean debugging two unrelated classes of failure against a build that has never been green, with no way to attribute a failure to either cause. `adopt-java-25-baseline` follows this change and flips the JDK against a build that is already known to work on Spring Boot 4.

The prerequisite `modernize-build-toolchain` has already raised the build's bytecode tooling on the 3.x line, so this change starts from a modern Gradle and does not have to fight the build system while fighting the framework.

## What Changes

- **`symphony-bdk-bom`: `spring-boot-dependencies` 3.5.16 → 4.x.** Because this BOM is the consumer contract, this single line is what actually makes a consumer's application a Spring Boot 4 application.
- **Delete the Netty and Tomcat CVE overrides** from the BOM. `netty-bom:4.1.136.Final` and the three `tomcat-embed-*:10.1.57` pins exist to climb above Spring Boot 3.5's choices. Spring Boot 4 brings Netty 4.2 and Tomcat 11, which supersede them. Left in place they are inert but actively misleading — the comments would send the next person auditing CVEs to the wrong conclusion.
- **Remove the hard-pinned Jakarta EE 10 API versions.** `jakarta.ws.rs:jakarta.ws.rs-api:3.1.0` and `jakarta.validation:jakarta.validation-api:3.0.2` are pinned in the BOM, and `jakarta.validation-api:3.0.2` is pinned *again* directly in `symphony-bdk-app-spring-boot-starter/build.gradle`. Jakarta EE 11 moves both. These pins would hold EE 10 APIs against an EE 11 platform.
- **Autoconfiguration retargeting.** Spring Boot 4 splits `spring-boot-autoconfigure` into per-technology modules and moves classes between packages. Affects the two `AutoConfiguration.imports` files, the ~28 `@ConditionalOn*` usages across the starters, `@SpringBootApplication` references, and the `AutoConfigurations` test helper used in 3 places.
- **Starter dependency retargeting.** `spring-boot-starter-web`, `-actuator`, `-validation`, `-webflux`, and `spring-boot-configuration-processor` are consumed by the two starters, `symphony-bdk-http-webclient`, and `symphony-bdk-test-spring-boot`; each needs to point at whatever Spring Boot 4 renamed or split it into.
- **Jackson: decide 2 vs 3, gated on the Jersey provider question** (see design D2). The blast radius is small either way — 22 hand-written files, and only 3 public method signatures (`BdkConfigParser.parse`, `parseJsonNode`, `interpolateProperties`, all taking or returning `JsonNode`). The 377 generated classes use only `com.fasterxml.jackson.annotation.*` and are expected to be unaffected.
- **Jersey version alignment** with Spring Boot 4's pin, and **rename `symphony-bdk-http-jersey2` → `symphony-bdk-http-jersey`**. The module has been on Jersey 3.x for some time; the name is already wrong, and an artifactId change is only possible in a major.
- **JSR-305 → JSpecify.** 82 usages of `javax.annotation.Nonnull` / `Nullable`. JSR-305 is unmaintained and has split-package problems; Spring Framework 7 standardises on JSpecify. A major is the only place this can move.
- **New starter smoke tests** that boot a minimal application through each published starter and assert the expected beans exist — see design D5. The current tests construct explicit contexts, which cannot catch autoconfiguration that silently stops applying.
- **Migration guide** for 3.x → 4.x consumers, and versioned documentation so the 3.x docs remain reachable.

## Capabilities

### New Capabilities
- `platform-baseline`: the runtime and framework contract the BDK promises consumers — required Java version, supported Spring Boot line, Jackson major version on the public API surface, Jakarta EE generation, and the published module coordinates. This has always been an implicit contract; a major version that changes all of it at once is the point at which it needs to be written down and verified.

### Modified Capabilities
*(none — `build-toolchain` from `modernize-build-toolchain` is unaffected; the toolchain version does not change in this change)*

## Impact

**Code**
- `symphony-bdk-bom/build.gradle`: Spring Boot platform bump; delete Netty BOM, 3 Tomcat pins, 2 Jakarta API pins; Jersey BOM realignment
- `symphony-bdk-spring/symphony-bdk-core-spring-boot-starter`: `SymphonyBdkAutoConfiguration` + `AutoConfiguration.imports`; `@ConditionalOn*` imports; `BdkActivityConfig.SlashAnnotationProcessor`; starter dependency coordinates
- `symphony-bdk-spring/symphony-bdk-app-spring-boot-starter`: `SymphonyBdkAppAutoConfiguration` + `AutoConfiguration.imports`; `SymphonyBdkHealthIndicator` (Actuator/Micrometer 2); `CircleOfTrustController`; remove the duplicated `jakarta.validation-api` pin
- `symphony-bdk-http/symphony-bdk-http-jersey2` → **renamed** `symphony-bdk-http-jersey`: directory, `settings.gradle`, BOM constraint, and all 5 consuming modules; `JSON.java` (`ContextResolver<ObjectMapper>`) and `RFC3339DateFormat.java` if Jackson 3 is adopted
- `symphony-bdk-http/symphony-bdk-http-webclient`: `spring-boot-starter-webflux` coordinate; the stale `guava:31.1-jre` constraint should be re-examined at the same time
- `symphony-bdk-test/symphony-bdk-test-spring-boot`: re-exports `spring-boot-starter-test` and `spring-boot-starter-web` as `api` — consumer test setups break here first
- `symphony-bdk-config/BdkConfigParser`: 3 public `JsonNode` signatures, if Jackson 3 is adopted
- 82 `javax.annotation.Nonnull`/`Nullable` usages across all modules → JSpecify
- `symphony-bdk-examples/*`: `bdk-spring-boot-example`, `bdk-app-spring-boot-example` must build against the new starters

**APIs** — breaking, as expected in a major:
- `symphony-bdk-http-jersey2` artifactId no longer published
- `BdkConfigParser`'s 3 `JsonNode` signatures change if Jackson 3 is adopted
- Nullability annotations change package (source-compatible for consumers, but visible to static analysis tooling)
- Consumers must be on Spring Boot 4; Spring Boot 3 + BDK 4 is explicitly unsupported (design D6)

**Dependencies**: Spring Boot 4 platform; Netty 4.2, Tomcat 11, Micrometer 2, Jakarta EE 11 inherited from it; JSpecify added; Jersey realigned; possibly Jackson 3.

**Docs**: `docs/` needs a 3.x/4.x split and a `docs/migration-4.x.md`. The `symphony-bdk-java.finos.org` site currently serves a single unversioned tree.
