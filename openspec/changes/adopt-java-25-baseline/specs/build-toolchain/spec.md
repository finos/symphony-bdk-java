## MODIFIED Requirements

### Requirement: Target Java version declared via a Gradle toolchain
The build SHALL declare the target Java version through a Gradle Java toolchain in `bdk.java-common-conventions`, not through the project-level `sourceCompatibility` convention. For BDK 4.x the declared `languageVersion` SHALL be **25**. The declared language version SHALL be the single source of truth for every module's target, so that changing the baseline is a one-line change in one file. The JDK running the Gradle daemon SHALL be independent of the declared toolchain version.

#### Scenario: Baseline declared in exactly one place
- **WHEN** the target Java version needs to change
- **THEN** exactly one `languageVersion` declaration in `bdk.java-common-conventions` requires editing, and no module build file declares its own `sourceCompatibility` or `targetCompatibility`

#### Scenario: Compilation target is independent of the daemon JDK
- **WHEN** the build is run with a Gradle daemon JDK newer than the declared toolchain version
- **THEN** all modules still compile to the declared toolchain version, and the produced class files carry that version

#### Scenario: Parameter names and encoding are preserved
- **WHEN** any module is compiled
- **THEN** `-parameters` is passed to `javac` and source encoding is UTF-8, so that Spring constructor binding and Jackson parameter-name resolution continue to work

#### Scenario: Toolchain is provisionable in CI
- **WHEN** CI runs the build on a runner whose preinstalled JDK does not match the declared toolchain
- **THEN** Gradle provisions or locates a matching JDK 25 toolchain rather than silently compiling to a different version

---

### Requirement: Bytecode-processing tooling supports the target class file version
Every tool in the build that reads, writes, or instruments bytecode — the Gradle distribution, Byte Buddy, MapStruct's annotation processor, Lombok, JaCoCo, ArchUnit, and the Mockito mock maker — SHALL be at a version that supports the class file version emitted by the declared toolchain. Versions SHALL be pinned explicitly where the build already pins them directly, rather than inherited implicitly from a third-party platform whose upgrade cadence the BDK does not control. Test-time bytecode instrumentation SHALL NOT depend on JVM self-attach; agents SHALL be configured explicitly via JVM arguments.

#### Scenario: Build succeeds on the declared baseline
- **WHEN** `./gradlew build` is run with the declared toolchain
- **THEN** no task fails with an unsupported-class-file-version, unknown-constant-pool, or unsupported-source-release error

#### Scenario: Lombok version is owned by this repository
- **WHEN** the Spring Boot platform version imported by `symphony-bdk-bom` changes
- **THEN** the resolved Lombok version does not change, because `symphony-bdk-bom` constrains it explicitly

#### Scenario: JaCoCo version is explicit
- **WHEN** the Gradle distribution version changes
- **THEN** the JaCoCo tool version used for coverage does not change, because it is declared via `jacoco { toolVersion }`

#### Scenario: Mock agent is attached explicitly
- **WHEN** any module's tests run
- **THEN** the Mockito agent is supplied as a `-javaagent` JVM argument, and no dynamic-agent-loading suppression flag is used to silence a self-attach warning

#### Scenario: Coverage thresholds are not silently relaxed for instrumentation reasons
- **WHEN** a JDK or JaCoCo change alters how lines are counted and a `jacocoTestCoverageVerification` minimum no longer passes
- **THEN** the threshold change is made in a dedicated commit naming the JDK or JaCoCo version, so it cannot be mistaken for a real coverage regression
