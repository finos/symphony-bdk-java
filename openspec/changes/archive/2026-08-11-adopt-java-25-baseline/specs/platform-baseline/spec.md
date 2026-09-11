## MODIFIED Requirements

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

## ADDED Requirements

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

---

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
