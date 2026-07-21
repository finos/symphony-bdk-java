## ADDED Requirements

### Requirement: DatafeedEventSource SPI
`symphony-bdk-core` SHALL expose a `DatafeedEventSource` interface in `com.symphony.bdk.core.extension` with two methods: `readEvents(String ackId)` returning `List<V4Event>` and `ackEvents(List<V4Event> events)` returning the next ackId as a `String`. When a registered extension implements `BdkDatafeedEventSourceProvider`, `DatafeedLoopV2` SHALL use the provided `DatafeedEventSource` for all read and ack operations. The agent `DatafeedApi` SHALL NOT be called when a source is active.

#### Scenario: readEvents called instead of agent datafeed API
- **WHEN** an extension implementing `BdkDatafeedEventSourceProvider` is pre-registered
- **THEN** `DatafeedLoopV2` calls `DatafeedEventSource.readEvents(ackId)` on each iteration instead of `datafeedApi.readDatafeed(...)`

#### Scenario: Returned events dispatched as normal
- **WHEN** `DatafeedEventSource.readEvents(ackId)` returns a non-empty `List<V4Event>`
- **THEN** `DatafeedLoopV2` dispatches those events to all registered listeners exactly as it would for agent-sourced events

#### Scenario: ackEvents called after successful dispatch
- **WHEN** events are successfully dispatched to all listeners
- **THEN** `DatafeedLoopV2` calls `DatafeedEventSource.ackEvents(events)` and uses the returned ackId for the next `readEvents` call

#### Scenario: Standard loop uses agent API when no source registered
- **WHEN** no extension implementing `BdkDatafeedEventSourceProvider` is registered
- **THEN** `DatafeedLoopV2` uses `datafeedApi.readDatafeed(...)` as today, with no behavioral change

---

### Requirement: DatafeedEventSource is stateless — no persistent datafeed ID
When a `DatafeedEventSource` is active, `DatafeedLoopV2` SHALL NOT perform datafeed ID creation or management. The source is session-based: the loop begins with a null ackId on first iteration and uses the ackId returned by `ackEvents` on all subsequent iterations.

#### Scenario: No datafeed ID creation call when source active
- **WHEN** an extension implementing `BdkDatafeedEventSourceProvider` is pre-registered
- **THEN** `DatafeedLoopV2` does not call `datafeedApi.createDatafeed(...)` and does not persist or look up a datafeed ID

#### Scenario: First iteration uses null ackId
- **WHEN** `DatafeedLoopV2` starts with an active `DatafeedEventSource`
- **THEN** the first call to `readEvents` passes `null` as the ackId

#### Scenario: Subsequent iterations use returned ackId
- **WHEN** `DatafeedEventSource.ackEvents(events)` returns a non-null ackId string
- **THEN** the next call to `readEvents` passes that ackId

---

### Requirement: DatafeedEventSource retry behavior
The existing retry and error-handling logic in `DatafeedLoopV2` SHALL apply to `DatafeedEventSource.readEvents(...)` calls. Transient failures (exceptions thrown by the source) SHALL be retried according to the configured `BdkRetryConfig`, consistent with the existing datafeed retry behavior.

#### Scenario: Transient exception triggers retry
- **WHEN** `DatafeedEventSource.readEvents(ackId)` throws an exception on one call
- **THEN** `DatafeedLoopV2` applies the configured retry policy and retries the call, logging the failure

#### Scenario: Exhausted retries propagate exception
- **WHEN** `DatafeedEventSource.readEvents(ackId)` throws on every retry attempt
- **THEN** `DatafeedLoopV2` stops the loop and propagates the exception, consistent with current agent-based failure behavior

---

### Requirement: DatafeedLoopV2 startup log indicates active source
When a `DatafeedEventSource` is active, `DatafeedLoopV2` SHALL log an INFO message at startup identifying that the agentless event source is in use, so operators can confirm the override is in effect.

#### Scenario: Startup log confirms agentless mode
- **WHEN** `DatafeedLoopV2` starts with an active `DatafeedEventSource`
- **THEN** an INFO log line is emitted at loop startup containing the class name of the active source

---

### Requirement: BdkDatafeedEventSourceProvider detection interface
`symphony-bdk-core` SHALL expose `BdkDatafeedEventSourceProvider` in `com.symphony.bdk.core.extension`. Extensions implementing this interface SHALL be detected by `ExtensionService` during registration and their source wired into `DatafeedLoopV2` at construction time when pre-registered via builder.

#### Scenario: Provider detected at construction time
- **WHEN** an extension implementing `BdkDatafeedEventSourceProvider` is pre-registered via `SymphonyBdkBuilder`
- **THEN** `ExtensionService` calls `getDatafeedEventSource()` and passes the result to `ServiceFactory` before `DatafeedLoopV2` is created
