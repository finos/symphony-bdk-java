## Context

The Symphony Java BDK provides a blocking HTTP client (`ApiClientJersey2` using Glassfish Jersey and Apache HttpClient 5) and Resilience4j-based retry wrappers (`RetryWithRecovery` and `AuthenticationRetry`). In reactive or asynchronous environments, BDK calls are executed within thread pools where tasks may be cancelled/interrupted (e.g., `Thread.interrupt()`).

When a thread is interrupted during a socket operation:
1. Jersey/Apache HC throws a `ProcessingException` wrapping a `CancellationException` or `InterruptedException`.
2. `RetryWithRecovery` or `AuthenticationRetry` catches this exception, matches it against its transient network issue predicate (`isNetworkIssueOrMinorError` / `canAuthenticationBeRetried`), and attempts retries.
3. Because the thread's interrupted status is still set, all subsequent socket calls fail instantly and synchronously, printing highly verbose, false-positive connection errors (`log.error(...)` in `RetryWithRecovery.handleRecovery` or warning logs).
4. After all attempts are exhausted, the exception is propagated but often dropped downstream in reactive pipelines, yielding noisy `onErrorDropped` logs.

See `proposal.md` for the core problem statement.

## Goals / Non-Goals

**Goals:**
- Detect thread interruption in both the retry loop and the underlying Jersey client.
- Bypass all retry attempts immediately upon detecting an interruption.
- Suppress warning and error logging for cancelled/interrupted requests.
- Restore the thread's interrupted status to ensure downstream reactive frameworks function correctly.
- Propagate a standard, clean `java.util.concurrent.CancellationException`.

**Non-Goals:**
- Introducing custom unchecked exceptions (we will use `CancellationException` to avoid API expansion).
- Rewriting the entire HTTP exception classification system.

## Decisions

### D1 — Standard Exception Choice (`CancellationException`)
**Decision**: Use `java.util.concurrent.CancellationException` (which extends `IllegalStateException` / `RuntimeException`) as the propagated exception indicating request cancellation or interruption.
- **Rationale**: Since `InterruptedException` is a checked exception, throwing it directly would require changing the signature of many public BDK service and API methods, breaking binary and source compatibility for downstream consumers. `CancellationException` is a standard Java runtime exception, is semantically perfect for async task/call cancellations, and can be thrown transparently.
- **Alternative considered**: Create a custom `BdkInterruptedException` extending `ApiRuntimeException`. Rejected because it introduces new public API surface area for an open-source library that is unnecessary when a standard Java exception fits perfectly.

### D2 — Traversal Exception Interruption Detection Helper
**Decision**: Add a static utility method `isInterruption(Throwable t)` inside `RetryWithRecovery` to inspect the cause chain of any exception for `InterruptedException` or `CancellationException`.
- **Rationale**: Downstream HTTP libraries (such as Jersey) often wrap low-level interruption or socket exceptions into higher-level runtime wrappers (like `ProcessingException` or `RuntimeException`). A recursive cause-chain inspection ensures robust and implementation-agnostic detection of interruptions.
- **Alternative considered**: Matching only direct types. Rejected because it would fail to detect low-level interruptions wrapped by Jersey's client connector.

### D3 — Bypassing Client Logging & Silent Abort
**Decision**: In `ApiClientJersey2`, if a caught `ProcessingException` has an interruption in its cause chain, or if `Thread.currentThread().isInterrupted()` is true, propagate a `CancellationException` directly.
- **Rationale**: Avoids logging network issues in `ApiClientJersey2` or `RetryWithRecovery` for requests that were canceled deliberately. This ensures console logs remain clean during reactive cancellation.

### D4 — Interruption-Aware Services (`com.symphony.bdk.core.service.*`)
**Decision**: In all services exposed to interruptions (specifically real-time datafeed loops like V1/V2, health check service, agent version query, and pagination/bulk retrieval APIs), we check and propagate `CancellationException` or restored interrupted flags immediately when caught, rather than wrapping them in `BdkExtensionException`, swallowing them, or logging them as warnings/errors.
- **Rationale**: Real-time services (like Datafeed loops) are intended to exit cleanly and silently upon task cancellation. Throwing `CancellationException` and logging a simple info shutdown message ensures system resource teardown is clean, responsive, and logs remain silent under high concurrency.

## Risks / Trade-offs

- **[Risk]** Downstream callers might not expect `CancellationException`.
  - **Mitigation**: `CancellationException` is standard in Java concurrency (e.g., `Future.get()`, reactive pipelines). Reactive streams and `CompletableFuture` already handle `CancellationException` as standard cancel signals, making this the most robust and standard choice.
