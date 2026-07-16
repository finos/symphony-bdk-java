## ADDED Requirements

### Requirement: SKD support determination without a configured Agent
When refreshing session tokens, the BDK SHALL determine whether Key Manager (KM) token retrieval can be skipped using only the pod-issued JWT's `skd` claim if no Agent is explicitly configured (i.e. `BdkConfig.getAgent()` does not override the parent pod configuration). The BDK SHALL NOT attempt to query Agent version information in this case.

#### Scenario: No Agent configured and pod enables SKD
- **WHEN** the BDK config does not explicitly configure an Agent (host/scheme/port/context all default to the pod)
- **AND** the pod-issued session JWT's `skd` claim is `true`
- **THEN** the BDK SHALL skip Key Manager token retrieval
- **AND** the BDK SHALL NOT call the Agent's version endpoint

#### Scenario: No Agent configured and pod disables SKD
- **WHEN** the BDK config does not explicitly configure an Agent
- **AND** the pod-issued session JWT's `skd` claim is `false` or absent
- **THEN** the BDK SHALL retrieve a Key Manager token as before

### Requirement: SKD support determination with a configured Agent
When an Agent is explicitly configured (`BdkConfig.getAgent()` overrides the parent pod configuration), the BDK SHALL continue to determine SKD support by combining the pod's `skd` JWT claim with the configured Agent's reported version, exactly as before this change.

#### Scenario: Configured Agent is new enough
- **WHEN** the BDK config explicitly configures an Agent
- **AND** the pod-issued session JWT's `skd` claim is `true`
- **AND** the configured Agent reports a version higher than 24.12
- **THEN** the BDK SHALL skip Key Manager token retrieval

#### Scenario: Configured Agent is too old or unreachable
- **WHEN** the BDK config explicitly configures an Agent
- **AND** the configured Agent's version cannot be retrieved, or is 24.12 or lower
- **THEN** the BDK SHALL retrieve a Key Manager token, regardless of the pod's `skd` claim
