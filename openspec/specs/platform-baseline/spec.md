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
The BDK SHALL declare a single minimum Java version for all published modules, enforced by the Gradle toolchain and stated in the published documentation. Consumers SHALL NOT need to inspect class files to determine the requirement.

#### Scenario: Published bytecode matches the declared minimum
- **WHEN** any published module's jar is inspected
- **THEN** its class file version corresponds to the declared minimum Java version, and no module targets a different version

#### Scenario: Declared minimum is documented
- **WHEN** the documentation states the Java requirement for a BDK major line
- **THEN** it names the version that the released artifacts actually require, not the version an intermediate development state built against

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
