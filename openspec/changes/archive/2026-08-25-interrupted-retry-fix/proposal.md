## Why

The Symphony BDK is frequently executed inside asynchronous thread pools or reactive pipelines (such as Spring WebFlux or Project Reactor). In these environments, when a task is cancelled, the executing thread is typically interrupted. Currently, the BDK's built-in retry wrapper (`RetryWithRecovery`) mistakenly treats the resulting socket exceptions as transient network errors and repeatedly attempts to retry the HTTP call. Because the thread remains interrupted, these retries fail instantly, clogging the console logs with false-positive error stack traces and dropping the final exception in reactive pipelines. The BDK must become interruption-aware to abort calls cleanly and silently upon thread interruption.

## What Changes

- **Implement Interruption Checks in `RetryWithRecovery` and `AuthenticationRetry`**: The core retry handlers will check `Thread.currentThread().isInterrupted()` and `isInterruption(e)`. If an interruption is detected, they will bypass all retry logic, restore the interrupted status (`Thread.currentThread().interrupt()`), and propagate a clean standard `java.util.concurrent.CancellationException`. This ensures that all BDK services, including client API services and authentication services, fail cleanly on interruption without retrying.
- **Suppress Cancellation Logging in HTTP Client**: The HTTP client implementations (e.g., `ApiClientJersey2`) will inspect the cause chain of caught exceptions (like `ProcessingException`). If caused by `CancellationException`, `InterruptedException`, or thread interruption, it will not print error logs and will propagate the exception cleanly.
- **Update BDK Connector Overrides**: The `MessageService.wrapOverrideException()` method will be updated to handle interruptions silently rather than throwing a `BdkExtensionException` with "Message override threw an unexpected exception".
- **Use Standard Exception**: A standard `java.util.concurrent.CancellationException` will be used as the unchecked exception representing thread/task cancellation. This avoids adding a custom BDK exception class, keeping API boundaries clean.

## Capabilities

### New Capabilities
- `request-interruption`: The BDK handles thread interruption gracefully by aborting requests without retries or noisy logging, and propagating a clean standard `CancellationException`.

### Modified Capabilities

## Impact

- **Code**:
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/retry/RetryWithRecovery.java`: check and bypass retries on thread interruption.
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/retry/resilience4j/Resilience4jRetryWithRecovery.java`: wrap Resilience4j's retry predicate to fail-fast and immediately abort retries on thread interruptions or cancellations.
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/auth/impl/AuthenticationRetry.java`: check and bypass retries on thread interruption in authentication loops.
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/service/message/MessageService.java`: `wrapOverrideException` maps interruptions to `CancellationException`.
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/service/datafeed/impl/AbstractAckIdEventLoop.java` / `AbstractDatafeedLoop.java` / `DatafeedLoopV1.java` / `DatafeedLoopV2.java`: detect thread cancellation or interruptions inside real-time datafeed loops to shut down cleanly and silently without false-positive error logs.
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/service/health/HealthService.java` / `AgentVersionService.java`: ensure exceptions wrapping interruptions are translated and propagated as standard `CancellationException`.
  - `symphony-bdk-core/src/main/java/com/symphony/bdk/core/service/pagination/CursorBasedPaginatedService.java` / `OffsetBasedPaginatedService.java`: propagate thread interruption gracefully during bulk pagination queries.
  - `symphony-bdk-http/symphony-bdk-http-jersey2/src/main/java/com/symphony/bdk/http/jersey2/ApiClientJersey2.java`: detect and propagate interruptions cleanly without throwing generic `ProcessingException`.
- **APIs**: No breaking API changes, since standard `CancellationException` is a runtime exception.
