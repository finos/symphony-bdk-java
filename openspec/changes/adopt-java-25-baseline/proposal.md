## Why

BDK 4.x baselines **Java 25**, the current LTS. This is the second and final platform move of the major: `migrate-spring-boot-4` established a green build on Java 17 + Spring Boot 4, and this change flips the JDK against that known-good state.

Java 25 emits class file version 69, which is what makes this a change rather than a one-line edit. Every tool in the build that reads, writes, or instruments bytecode has to understand it. `modernize-build-toolchain` already raised most of that layer on the 3.x line — Gradle, Byte Buddy, MapStruct, ArchUnit, JaCoCo, Lombok — and recorded the residual failures from a throwaway JDK 25 build. This change works through that recorded list and makes 25 the committed baseline.

The one substantial piece of work not already covered is the **OpenAPI generator**. Version 6.6.0 is a 2023 release that cannot be expected to run on JDK 25, and it generates 377 classes that are part of the published API surface. That bump is the largest single risk in this change and is deliberately quarantined here rather than smuggled into an earlier one.

Baselining 25 rather than 17 or 21 is a deliberate choice with a known cost: BDK 4.x becomes unavailable to consumers who cannot run Java 25, which makes the 3.x line a long-lived maintenance branch rather than a short-term safety net. That cost is accepted, and this change carries the obligation to make it explicit — see design D1 and task 7.4.

## What Changes

- **Toolchain 17 → 25.** One `languageVersion` line in `bdk.java-common-conventions`, which is the entire point of having introduced the toolchain in `modernize-build-toolchain`.
- **CI on JDK 25** across all three workflows: `build.yml`, `cve-scanning-gradle.yml`, `release.yml`. Single JDK, no matrix — Java 25 is a hard requirement, not one option among several, so there is nothing to matrix over.
- **`openapi-generator` 6.6.0 → 7.x**, with `library = 'jersey2'` → `jersey3` in both generator configurations (`bdk.java-codegen-conventions` and the `apisToGenerate` loop in `symphony-bdk-core/build.gradle`). Includes rebasing the three custom Mustache templates (`api.mustache`, `pojo.mustache`, `modelInnerEnum.mustache`) onto 7.x's upstream templates, and a full review of the resulting diff across the 377 generated classes.
- **Mockito's dynamic agent attach** made explicit via a `-javaagent` test JVM argument rather than relying on self-attach, which the JDK warns about and is scheduled to deny.
- **Lombok** confirmed at a JDK 25-capable version — already pinned explicitly in the BOM by `modernize-build-toolchain`, so this is a verification, not a bump hunt.
- **Release workflow JDK bump**, plus a branch guard on the release tag: the current `^v[0-9]+\.[0-9]+\.[0-9]+$` regex matches `v3.x` and `v4.x` identically with no check on which branch the release targets.
- **Documentation**: Java 25 stated as a hard requirement, and the 3.x support window recorded so the migration guide can answer "what if I cannot move to Java 25".
- **No source-level modernization.** No records, pattern matching, sealed types, or virtual threads. This change flips a baseline; using what the baseline unlocks is separate work.

## Capabilities

### Modified Capabilities
- `platform-baseline`: the declared minimum Java version moves from 17 to 25, and the requirement's consequence — that the previous major becomes a supported maintenance line with a stated window — is added.
- `build-toolchain`: the declared toolchain language version moves from 17 to 25, and the generated-output-diff requirement is exercised for the OpenAPI generator rather than only for annotation processors.

### New Capabilities
*(none)*

## Impact

**Build**
- `buildSrc/src/main/groovy/bdk.java-common-conventions.gradle`: `JavaLanguageVersion.of(17)` → `of(25)`
- `buildSrc/build.gradle`: `openapi-generator-gradle-plugin` 6.6.0 → 7.x, and removal of the hand-written override block that exists only because 6.6.0 needs older transitive libraries than `owasp-dependencycheck` 12.2.2
- `buildSrc/src/main/groovy/bdk.java-codegen-conventions.gradle`: `library` → `jersey3`; 7.x `configOptions` review (`dateLibrary`, `useJakartaEe`, nullable handling)
- `symphony-bdk-core/build.gradle`: same generator changes in the 5-API `apisToGenerate` loop
- `templates/api.mustache`, `templates/pojo.mustache`, `templates/modelInnerEnum.mustache`: rebased onto 7.x upstream templates
- Test JVM args for the explicit Mockito agent, in `bdk.java-common-conventions`

**CI**: `build.yml`, `cve-scanning-gradle.yml`, `release.yml` — `java-version: '17'` → `'25'`; `actions/setup-java@v3` in `release.yml` is also behind the other workflows and should be aligned

**Code**: 377 generated classes under `com.symphony.bdk.gen.api` regenerate. Any hand-written call site that touches a changed generated signature follows. `symphony-bdk-http-api`'s invoker abstraction (`ApiClient`, `ApiResponse`, `Pair`, `TypeReference`) is generated against `invokerPackage = 'com.symphony.bdk.http.api'` and may shift under 7.x.

**APIs** — breaking:
- Java 25 required at runtime; consumers on 17 or 21 cannot use 4.x at all
- Generated model and API classes may change signature under the generator bump. These are public types (`com.symphony.bdk.gen.api.model.*`), so every change is a consumer-visible change and must be enumerated in the migration guide, not discovered by consumers

**Dependencies**: `openapi-generator` 7.x; `jackson-databind-nullable` may be replaced or dropped depending on 7.x's nullable strategy; explicit Mockito agent wiring.

**Docs**: Java 25 requirement; the generated-API diff summary; the 3.x support window.
