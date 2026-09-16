# Platform-Baseline Specification

### Requirement: Supported Spring Boot line
The BDK's Spring modules SHALL support exactly one Spring Boot major line per BDK major line. BDK 4.x SHALL require Spring Boot 4.x. Spring Boot 3.x combined with BDK 4.x SHALL NOT be supported, and the BDK SHALL NOT attempt cross-major compatibility through conditional autoconfiguration or runtime version detection. The requirement SHALL be stated in the migration guide and in the published documentation.

#### Scenario: Consumer on the matching Spring Boot major
- **WHEN** a consumer application uses Spring Boot 4.x and imports `symphony-bdk-bom` 4.x
- **THEN** the BDK starters autoconfigure without the consumer overriding any Spring Boot managed version

#### Scenario: Consumer on the previous Spring Boot major
- **WHEN** a consumer application uses Spring Boot 3.x
- **THEN** the documented answer is to stay on BDK 3.x, and no partial-compatibility path is offered

### Requirement: Minimum Java version is declared and enforced by the build
The BDK SHALL declare a single minimum Java version for all published modules, enforced by the Gradle toolchain and stated in the published documentation. For BDK 4.x that minimum SHALL be **Java 25**. Consumers SHALL NOT need to inspect class files to determine the requirement. The minimum SHALL be stated as a requirement rather than a recommendation, and the BDK SHALL NOT publish artifacts targeting an older Java version for the same major line.

#### Scenario: Published bytecode matches the declared minimum
- **WHEN** any published module's jar is inspected
- **THEN** its class file version corresponds to Java 25, and no module targets a different version

#### Scenario: Declared minimum is documented
- **WHEN** the documentation states the Java requirement for a BDK major line
- **THEN** it names the version that the released artifacts actually require, not the version an intermediate development state built against

#### Scenario: Consumer on an older Java version
- **WHEN** a consumer's runtime is Java 17 or Java 21
- **THEN** BDK 4.x is not usable and the documented answer is the previous major line, not a partial-support path

#### Scenario: Java requirement is stricter than the framework's
- **WHEN** a consumer is on a Spring Boot version whose own Java baseline is lower than the BDK's
- **THEN** the BDK's requirement governs, and the documentation states the BDK's Java requirement independently of Spring Boot's

### Requirement: The previous major line has a stated support window
When a new BDK major line raises the minimum Java version above what a substantial part of the consumer base can run, the previous major line SHALL be maintained as a supported line with a support window stated in writing before the new major is released. The window SHALL be reachable from the migration guide, and the previous line SHALL continue to receive security fixes for its duration.

#### Scenario: Consumer cannot meet the new Java requirement
- **WHEN** a consumer reads the migration guide and cannot run the required Java version
- **THEN** the guide names the previous major line as their supported option and states how long it is maintained

#### Scenario: Security fixes on the maintenance line
- **WHEN** a vulnerability is found in a dependency shared by both lines, within the stated window
- **THEN** the fix is released on the maintenance line as well as the current line

#### Scenario: Support window is decided before the major ships
- **WHEN** the new major version is released
- **THEN** the support window for the previous line is already documented, not deferred to a later decision

### Requirement: Release tag validation matches the branch to the major version
The release pipeline SHALL verify that a release's target branch corresponds to the major version in its tag. Validating the tag's shape alone SHALL NOT be sufficient, because releases published to the artifact repository cannot be withdrawn.

#### Scenario: Maintenance-line release targets the maintenance branch
- **WHEN** a release is drafted with a tag for the previous major line
- **THEN** the workflow fails unless the release targets that line's branch

#### Scenario: Current-line release targets the default branch
- **WHEN** a release is drafted with a tag for the current major line
- **THEN** the workflow fails unless the release targets the default branch

#### Scenario: Mis-targeted release is refused before publication
- **WHEN** a tag's major version and its target branch disagree
- **THEN** the workflow fails before any artifact is signed or uploaded

### Requirement: Exactly one JSON binding implementation on the runtime classpath
The BDK SHALL resolve to exactly one JSON databind implementation across all published modules. Two implementations of the same JSON binding library SHALL NOT be simultaneously resolvable, so that a given BDK operation serializes identically regardless of which HTTP implementation module is on the classpath.

#### Scenario: Serialization does not depend on the HTTP module choice
- **WHEN** the same BDK operation is performed with the Jersey HTTP implementation and with the WebClient HTTP implementation
- **THEN** the wire representation is identical, because both use the same configured JSON mapper

#### Scenario: A second databind implementation is rejected by the build
- **WHEN** a dependency change would put two major versions of the JSON databind library on the runtime classpath
- **THEN** the build fails a dependency-verification check rather than resolving both

### Requirement: Nullability contract expressed in a maintained annotation vocabulary
Public API nullability SHALL be expressed using JSpecify annotations. The BDK SHALL NOT use `javax.annotation.Nullable` or `javax.annotation.Nonnull` (JSR-305) in hand-written source, because JSR-305 is unmaintained and occupies the `javax.annotation` package, which conflicts with strict dependency analysis and module systems.

#### Scenario: JSR-305 nullability annotations are absent from hand-written source
- **WHEN** the architecture tests run
- **THEN** no hand-written class imports `javax.annotation.Nullable` or `javax.annotation.Nonnull`, and the rule fails the build if one is reintroduced

#### Scenario: Nullability defaulting is explicit per module
- **WHEN** a module applies package-level `@NullMarked`
- **THEN** the resulting contract for unannotated types has been verified to match the previously expressed JSR-305 intent, rather than being inherited by accident

### Requirement: Published module coordinates name the technology they actually use
Published artifactIds SHALL NOT name a technology generation the module does not use. When a module's underlying technology generation changes, its coordinate SHALL be corrected at the next major version. Coordinate renames SHALL be listed in the migration guide, and superseded coordinates SHALL NOT be published as relocation or forwarding artifacts.

#### Scenario: Jersey HTTP module coordinate
- **WHEN** a consumer inspects the dependency tree of the Jersey-based HTTP implementation
- **THEN** the artifactId does not claim a Jersey major version other than the one actually resolved

#### Scenario: Renamed coordinate is not aliased
- **WHEN** a consumer depends on a coordinate that a previous major published but this major renamed
- **THEN** resolution fails outright rather than resolving an empty forwarding artifact, and the migration guide names the replacement

### Requirement: Starter autoconfiguration verified by booting a real application
Each published Spring Boot starter SHALL have a test that boots a minimal `@SpringBootApplication` depending only on that starter and asserts the expected beans are present, exercising Spring Boot's autoconfiguration discovery mechanism. Tests that assemble a context directly from autoconfiguration classes SHALL NOT be the only verification, because they bypass the discovery mechanism that a framework upgrade can break.

#### Scenario: Autoconfiguration registration is exercised end to end
- **WHEN** the starter's autoconfiguration is not discovered — because a registration file path, a condition annotation package, or an autoconfiguration class location changed
- **THEN** the starter smoke test fails, rather than passing because the test constructed the context explicitly

#### Scenario: Re-exported test stack is verified from a consumer's perspective
- **WHEN** `symphony-bdk-test-spring-boot` re-exports Spring Boot's test starter to consumers
- **THEN** a test in a dependent module verifies that the re-exported stack works, so breakage is caught in this repository rather than by consumers
