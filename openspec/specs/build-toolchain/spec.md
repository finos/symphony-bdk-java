# Build-Toolchain Specification

### Requirement: Target Java version declared via a Gradle toolchain
The build SHALL declare the target Java version through a Gradle Java toolchain in `bdk.java-common-conventions`, not through the project-level `sourceCompatibility` convention. The declared language version SHALL be the single source of truth for every module's target, so that changing the baseline is a one-line change in one file. The JDK running the Gradle daemon SHALL be independent of the declared toolchain version.

#### Scenario: Baseline declared in exactly one place
- **WHEN** the target Java version needs to change
- **THEN** exactly one `languageVersion` declaration in `bdk.java-common-conventions` requires editing, and no module build file declares its own `sourceCompatibility` or `targetCompatibility`

#### Scenario: Compilation target is independent of the daemon JDK
- **WHEN** the build is run with a Gradle daemon JDK newer than the declared toolchain version
- **THEN** all modules still compile to the declared toolchain version, and the produced class files carry that version

#### Scenario: Parameter names and encoding are preserved
- **WHEN** any module is compiled
- **THEN** `-parameters` is passed to `javac` and source encoding is UTF-8, so that Spring constructor binding and Jackson parameter-name resolution continue to work

### Requirement: Bytecode-processing tooling supports the target class file version
Every tool in the build that reads, writes, or instruments bytecode — the Gradle distribution, Byte Buddy, MapStruct's annotation processor, Lombok, JaCoCo, ArchUnit, and the Mockito mock maker — SHALL be at a version that supports the class file version emitted by the declared toolchain. Versions SHALL be pinned explicitly where the build already pins them directly, rather than inherited implicitly from a third-party platform whose upgrade cadence the BDK does not control.

#### Scenario: Build succeeds when the toolchain is raised to a newer JDK
- **WHEN** the declared toolchain `languageVersion` is temporarily raised to the next Java LTS and `./gradlew build` is run
- **THEN** no task fails with an unsupported-class-file-version, unknown-constant-pool, or unsupported-source-release error

#### Scenario: Lombok version is owned by this repository
- **WHEN** the Spring Boot platform version imported by `symphony-bdk-bom` changes
- **THEN** the resolved Lombok version does not change, because `symphony-bdk-bom` constrains it explicitly

#### Scenario: JaCoCo version is explicit
- **WHEN** the Gradle distribution version changes
- **THEN** the JaCoCo tool version used for coverage does not change, because it is declared via `jacoco { toolVersion }`

### Requirement: Build is free of deprecation warnings that block the next Gradle major
`./gradlew build --warning-mode all` SHALL complete without emitting Gradle deprecation warnings. This applies to build scripts in every module and to the convention plugins in `buildSrc`.

#### Scenario: No deprecation warnings on a full build
- **WHEN** `./gradlew build --warning-mode all` is run on a clean checkout
- **THEN** no deprecation warnings are emitted, including from `buildSrc` convention plugins at configuration time

#### Scenario: Build directory and task registration use current APIs
- **WHEN** any build script references the build output directory or registers a task
- **THEN** it uses `layout.buildDirectory` and `tasks.register`, not `project.buildDir` or `tasks.create`

### Requirement: Published BOM constrains only dependencies the project actually uses
`symphony-bdk-bom` SHALL NOT carry version constraints for artifacts that no module in the project resolves. Removing a constraint from the published BOM is a consumer-visible change to dependency resolution and SHALL be released in a minor or major version, never a patch, and SHALL be recorded in release notes.

#### Scenario: Dead constraint is removed
- **WHEN** a constraint in `symphony-bdk-bom` names an artifact that no module resolves
- **THEN** the constraint is removed, and the removal is listed in the release notes for the version that drops it

#### Scenario: Constraint removal is not shipped as a patch
- **WHEN** a release removes one or more constraints from `symphony-bdk-bom`
- **THEN** that release increments at least the minor version

### Requirement: Generated and processor-derived output is diffed across tooling upgrades
When an annotation processor or code generator used by the build is upgraded, the output it produces SHALL be compared before and after the upgrade, and any change to a consumer-visible type or to null/default-value handling SHALL be reviewed before release. A tooling upgrade SHALL NOT be treated as build-internal if it changes generated output.

#### Scenario: MapStruct processor upgrade
- **WHEN** the MapStruct processor version changes
- **THEN** the generated mapper implementations are diffed, and any change in unmapped-property or null handling is reviewed as a behaviour change rather than a build change

#### Scenario: OpenAPI generator upgrade
- **WHEN** the OpenAPI generator version changes
- **THEN** the generated sources under `com.symphony.bdk.gen.api` are diffed in full, because those types are part of the published API surface
