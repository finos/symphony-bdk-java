## 1. Retry Pipeline Changes

- [x] 1.1 Implement static `isInterruption(Throwable t)` helper in `RetryWithRecovery.java`
- [x] 1.2 Update `executeOnce()` to check for thread interruption and throw `CancellationException` to bypass recovery
- [x] 1.3 Update static `executeAndRetry()` helper to preserve interrupted status and propagate `CancellationException`
- [x] 1.4 Update `AuthenticationRetry.java` to check for thread interruption, bypass retries, and propagate `CancellationException` in authentication loops

## 2. Client Logging Suppression

- [x] 2.1 Update `getResponse()` in `ApiClientJersey2.java` to detect interruption/cancellation and cleanly propagate `CancellationException` silently

## 3. Override and Service Updates

- [x] 3.1 Update `wrapOverrideException()` in `MessageService.java` to handle interruptions silently and throw `CancellationException`
- [x] 3.2 Update other services in `com.symphony.bdk.core.service.*` (specifically Datafeed loops, Health, AgentVersion, and Paginated query services) to check and propagate thread interruption, avoiding logging false-positives or wrapping cancellations

## 4. Verification and Testing

- [x] 4.1 Write unit tests in `Resilience4jRetryWithRecoveryTest.java` verifying that an interrupted thread execution bypasses retries, preserves interrupted flag, and throws `CancellationException`
- [x] 4.2 Write unit tests in `MessageServiceTest.java` (or similar) verifying that `wrapOverrideException` maps interruptions to `CancellationException` silently
- [x] 4.3 Write unit tests in `ApiClientJersey2Test.java` verifying that `ApiClientJersey2` throws `CancellationException` without logging during interruptions
- [x] 4.4 Write unit tests in `AuthenticationRetryTest.java` verifying that `AuthenticationRetry` bypasses retries, preserves interrupted status, and propagates `CancellationException` on thread interruption
