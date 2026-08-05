## Why

The build has accumulated tooling that predates the current codebase by several years: Gradle 8.14.5, `byte-buddy 1.12.19` (2022), `mapstruct 1.4.2.Final` (2021), `archunit 1.2.1`, `mockserver-netty 5.15.0`, and a bare `sourceCompatibility = JavaVersion.VERSION_17` rather than a Java toolchain. None of this is a problem on JDK 17 — which is precisely why it has stayed invisible.

It becomes a hard blocker the moment the BDK targets a newer JDK. Every one of those tools reads, writes, or instruments bytecode, and each carries a class-file-version floor. BDK 4.x will baseline Java 25 (class file version 69), so the entire bytecode-touching layer of the build must move before the JDK does.

Doing that work here, on the 3.x line, has three payoffs:

1. It isolates a large, mechanical, low-risk diff from the genuinely risky framework migration that follows — the two failure modes don't get tangled together in one branch.
2. The 4.x branch (`next`) starts from an already-modern build instead of re-doing this work.
3. It is independently valuable to 3.x consumers: a build that runs on current Gradle, and three dead dependencies removed from the published BOM.

This change deliberately does **not** touch the Java version, the Spring Boot version, or the OpenAPI generator. Those are the three things that alter the consumer contract, and each gets its own change.

## What Changes

- **Gradle 8.14.5 → 9.x.** Includes the associated deprecation cleanup: `project.buildDir` → `layout.buildDirectory` (3 build files), `tasks.create` → `tasks.register` (2 call sites), and replacing the project-level `sourceCompatibility` convention — removed in Gradle 9 — with an explicit `java { toolchain { ... } }` block in `bdk.java-common-conventions`. The toolchain stays pinned to **17** in this change.
- **Bytecode-processing tooling raised to versions that support current class file versions**: `byte-buddy` (explicit pin in `symphony-bdk-core`), `mapstruct` + `mapstruct-processor`, `archunit-junit5`, and an explicit `jacoco { toolVersion }` rather than the Gradle-default JaCoCo.
- **Lombok pinned explicitly in `symphony-bdk-bom`** instead of inheriting from `spring-boot-dependencies`. Lombok is used in 13 modules and is the most JDK-version-sensitive dependency in the build; controlling it independently of the Spring Boot version decouples two otherwise-coupled risks.
- **`mock-server:mockserver-netty 5.15.0` addressed** (bump or replace — see design D3). 4 test files depend on it; it is stale and interacts with the Netty version, which Spring Boot 4 will move.
- **Dead dependencies removed**: `javax.xml.bind:jaxb-api:2.3.1` from `symphony-bdk-core` (zero source usage — verified), `org.projectreactor:reactor-spring:1.0.1.RELEASE` from the BOM (a 2017 artifact), and `javax.annotation:jsr250-api:1.0` → `jakarta.annotation:jakarta.annotation-api` in `bdk.java-codegen-conventions`.
- **No change** to: Java baseline (stays 17), Spring Boot version (stays 3.5.16), OpenAPI generator version (stays 6.6.0 — unless the Gradle 9 interlock in D1 forces it), any `src/main` source file, or any published module coordinate.

## Capabilities

### New Capabilities
- `build-toolchain`: the build's own contract — how the target Java version is declared, and the floor that any bytecode-processing tool in the build must meet. Written now because BDK 4.x will move the target twice (Spring Boot, then JDK) and each move needs to check the same invariants.

### Modified Capabilities
*(none — no existing spec-level requirement changes; this change is build-internal and consumer-visible only through the removed BOM constraints)*

## Impact

**Build**
- `gradle/wrapper/gradle-wrapper.properties`: distribution 8.14.5 → 9.x
- `buildSrc/src/main/groovy/bdk.java-common-conventions.gradle`: `sourceCompatibility` → toolchain block; explicit `jacoco.toolVersion`
- `buildSrc/src/main/groovy/bdk.java-codegen-conventions.gradle`: `buildDir` → `layout.buildDirectory`; jsr250 → jakarta.annotation
- `buildSrc/build.gradle`: plugin dependency alignment for Gradle 9 (already carries a manual override block for `owasp-dependencycheck` vs `openapi-generator` conflicts)
- `symphony-bdk-core/build.gradle`: `buildDir` → `layout.buildDirectory`; `tasks.create` → `tasks.register` (× 2, in the `apisToGenerate` loop); byte-buddy and mapstruct bumps; drop `jaxb-api`
- `symphony-bdk-extensions/symphony-group-extension/build.gradle`: `buildDir` → `layout.buildDirectory`
- `symphony-bdk-bom/build.gradle`: explicit Lombok constraint; archunit / mockserver / assertj bumps; remove `reactor-spring`

**Source**: none expected. `UserDetailMapper` is the only MapStruct mapper in the tree — its *generated* implementation may change with the processor bump and must be diffed.

**APIs**: no public API change.

**Dependencies**: the published BOM loses `reactor-spring` and gains an explicit Lombok constraint. Both are consumer-visible version-resolution changes and are reviewed as their own commit (see design D4).

**Release**: ships as a **3.6.0 minor**, not a patch — the BOM constraint removals are behaviour-visible to consumers who rely on the BOM for versions they don't declare themselves.
