## Purpose

Defines the Symphony BDK's standard behavior and contract when executing blocking HTTP requests or retry operations on an interrupted thread, or when a request is cancelled.

## ADDED Requirements

### Requirement: Interruption-Aware Request Execution

The BDK request execution pipeline and retry recovery handler SHALL immediately abort when an interruption is detected, bypass all retry logic, suppress noisy logs, and propagate a standard `CancellationException`.

#### Scenario: Immediate Abort on Thread Interrupted Flag
- **WHEN** a BDK blocking request or retry operation is executed on a thread whose interrupted status is already set
- **THEN** the operation SHALL immediately bypass all retry logic, restore the thread's interrupted status, and throw a `java.util.concurrent.CancellationException`

#### Scenario: Immediate Abort on Interrupted Exception Cause
- **WHEN** a BDK blocking request throws an exception caused by `java.lang.InterruptedException` or `java.util.concurrent.CancellationException`
- **THEN** the operation SHALL bypass all retry logic and throw a `java.util.concurrent.CancellationException`. The thread's interrupted status SHALL only be restored if the cause chain contains an actual `java.lang.InterruptedException` or the thread's interrupted flag was already set, avoiding thread-pool poisoning from logical task cancellations.

#### Scenario: Silenced Request Cancellation Logging
- **WHEN** a BDK blocking request is cancelled or interrupted
- **THEN** the BDK client and retry handlers SHALL NOT output warning or error log statements related to the request failure or connection refusal

#### Scenario: Silent Interruption in Message Overrides
- **WHEN** a registered message override is executing and is interrupted or cancelled
- **THEN** the override wrapper SHALL restore the thread's interrupted status and propagate a `java.util.concurrent.CancellationException` without logging it as an unexpected override exception
