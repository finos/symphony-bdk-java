## 1. Module Scaffolding

- [x] 1.1 Create `symphony-bdk-http/symphony-bdk-http-jdk/build.gradle` applying `bdk.java-library-conventions` + `bdk.java-publish-conventions`, with `api project(':symphony-bdk-http:symphony-bdk-http-api')` and `jackson-databind`, `jackson-datatype-jsr310`, `jackson-databind-nullable`, `slf4j-api`, `apiguardian-api`, plus test deps mirroring jersey2 (`junit-jupiter`, `logback-classic`, `mockserver-netty`, `mockito-core`, `mockito-junit-jupiter`, `junit-platform-launcher`)
- [x] 1.2 Add `include(':symphony-bdk-http:symphony-bdk-http-jdk')` to root `settings.gradle`
- [x] 1.3 Add `api "org.finos.symphony.bdk:symphony-bdk-http-jdk:$project.version"` to `symphony-bdk-bom/build.gradle`
- [x] 1.4 Confirm `./gradlew :symphony-bdk-http:symphony-bdk-http-jdk:build` succeeds with an empty module before adding implementation code

## 2. JSON Serialization (D5)

- [x] 2.1 Create `com.symphony.bdk.http.jdk.JSON`, configuring an `ObjectMapper` matching jersey2's: `NON_NULL` inclusion, `FAIL_ON_UNKNOWN_PROPERTIES=false`, `FAIL_ON_INVALID_SUBTYPE=false`, enums via `toString`, `JavaTimeModule` and `JsonNullableModule` registered
- [x] 2.2 Create an `RFC3339DateFormat`-equivalent date formatter producing the same wire format as jersey2's
- [x] 2.3 Unit test: date field serializes/deserializes to the same RFC3339 string jersey2 produces for the same value (spec: JSON Serialization Parity)
- [x] 2.4 Unit test: response containing an unrecognized JSON property deserializes without throwing (spec: JSON Serialization Parity)

## 3. Core `ApiClientJdk` — Request Building

- [x] 3.1 Create `com.symphony.bdk.http.jdk.ApiClientJdk implements ApiClient`, building `HttpRequest`s from `basePath` + `path`, query params via `parameterToPairs`/`escapeString`, headers, and cookies
- [x] 3.2 Implement `X-Trace-Id` header injection via `DistributedTracingContext`: generate-if-absent, clear only if generated (spec: Distributed Tracing Header Propagation)
- [x] 3.3 Unit test: trace ID generated and cleared when absent before the call
- [x] 3.4 Unit test: existing trace ID preserved and not cleared when already set before the call
- [x] 3.5 Implement `application/x-www-form-urlencoded` body encoding (spec: ApiClient Contract Conformance)
- [x] 3.6 Unit test: form params encode as `key=value` pairs joined with `&`, percent-encoded

## 4. Multipart Body Encoding (D6)

- [x] 4.1 Implement multipart/form-data body construction: boundary generation, part encoding for `File`
- [x] 4.2 Extend multipart encoding to `Collection<File>` (one part per file), `ApiClientBodyPart`, `ApiClientBodyPart[]`, and plain string fields
- [x] 4.3 Use a streaming `BodyPublisher` for file parts to avoid buffering entire files into memory
- [x] 4.4 Unit test: `File` form param produces a multipart part with matching content and filename
- [x] 4.5 Unit test: `Collection<File>` form param produces one multipart part per file
- [x] 4.6 Unit test: `ApiClientBodyPart`/`ApiClientBodyPart[]` form params produce parts from their `InputStream` content and filename
- [x] 4.7 Integration test against MockServer: a real multipart file upload round-trips correctly

## 5. Response Handling

- [x] 5.1 Implement response deserialization via the `JSON` `ObjectMapper` for non-`File`/`byte[]` return types
- [x] 5.2 Implement file-download responses via `HttpResponse.BodyHandlers.ofFile(Path)`, writing into `temporaryFolderPath`, honoring `Content-Disposition` filename when present (spec: File Download Responses)
- [x] 5.3 Implement `byte[]` return type handling
- [x] 5.4 Implement `204 No Content` → `ApiResponse` with null data
- [x] 5.5 Implement non-2xx handling: read body as string, throw `ApiException(status, message, headers, body)`
- [x] 5.6 Unit test: response with `Content-Disposition: attachment; filename="report.pdf"` and return type `File` writes a file named `report.pdf` into `temporaryFolderPath`

## 6. Exception Translation (D2)

- [x] 6.1 Catch `HttpTimeoutException` around `send`/unwrapped `sendAsync().join()` calls and rethrow as `java.net.SocketTimeoutException` with the original as cause
- [x] 6.2 Confirm `HttpConnectTimeoutException`, `java.net.ConnectException`, and `java.net.UnknownHostException` propagate unmodified (no translation needed — already the right root-cause types)
- [x] 6.3 Unit test: a connect-timeout scenario surfaces `java.net.ConnectException` as the root cause (spec: Exception Translation for Retry Compatibility)
- [x] 6.4 Unit test: a request-timeout scenario surfaces `java.net.SocketTimeoutException` as the root cause (spec: Exception Translation for Retry Compatibility)
- [x] 6.5 Integration test: `RetryWithRecoveryBuilder`'s retry predicate treats both translated exceptions as retryable, confirming interop with `symphony-bdk-core`'s existing retry logic unmodified

## 7. `ApiClientBuilderJdk` — TLS, Proxy, Timeouts (D1, D8, D9)

- [x] 7.1 Create `com.symphony.bdk.http.jdk.ApiClientBuilderJdk implements ApiClientBuilder`
- [x] 7.2 Implement `withKeyStore`/`withTrustStore`: build an `SSLContext` via `KeyManagerFactory`/`TrustManagerFactory`, merging default JVM root CAs via `ApiUtils.addDefaultRootCaCertificates`, applied via `HttpClient.Builder#sslContext`
- [x] 7.3 Implement `withProxy`: `ProxySelector.of(InetSocketAddress)` on `HttpClient.Builder#proxy`
- [x] 7.4 Implement `withProxyCredentials`: `HttpClient.Builder#authenticator(Authenticator)` responding only to `RequestorType.PROXY`
- [x] 7.5 Implement `withConnectionTimeout` → `HttpClient.Builder#connectTimeout`; `withReadTimeout` → per-request `HttpRequest.Builder#timeout`, with javadoc documenting the approximation (D1)
- [x] 7.6 Leave `withConnectionPoolMax`/`withConnectionPoolPerRoute` as the inherited no-op default (D4) — no override needed
- [x] 7.7 Integration test against MockServer with mutual TLS enforced: keystore/truststore wiring succeeds end-to-end (mirrors `ApiClientBuilderJersey2Test#sslContextIsUsed`)
- [x] 7.8 Integration test: request routed through a configured proxy
- [x] 7.9 Integration test: proxy credentials answer a `407` challenge successfully

## 8. Filters and Request Logging (D3)

- [x] 8.1 Define `addFilter` to accept `Function<HttpRequest.Builder, HttpRequest.Builder>` and throw `IllegalArgumentException` for any other type
- [x] 8.2 Implement outgoing request logging as a manual wrap around `send`, logging status code, URL, and elapsed time at DEBUG to `com.symphony.bdk.requests.outgoing`
- [x] 8.3 Unit test: a custom filter function is applied to the outgoing request
- [x] 8.4 Unit test: passing a non-`Function` filter throws `IllegalArgumentException`
- [x] 8.5 Unit test: a completed request produces the expected DEBUG log entry (spec: Outgoing Request Logging)

## 9. SPI Registration

- [x] 9.1 Create `com.symphony.bdk.http.jdk.ApiClientBuilderProviderJdk implements ApiClientBuilderProvider`
- [x] 9.2 Create `src/main/resources/META-INF/services/com.symphony.bdk.http.api.ApiClientBuilderProvider` containing `com.symphony.bdk.http.jdk.ApiClientBuilderProviderJdk`
- [x] 9.3 Integration test: with only `symphony-bdk-http-jdk` on the test classpath, `ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class)` resolves it
- [x] 9.4 Integration test: with `symphony-bdk-http-jdk` and `symphony-bdk-http-jersey2` both on the test classpath, `ServiceLookup.lookupSingleService` throws `IllegalStateException`

## 10. Coverage and Build Verification

- [x] 10.1 Confirm `jacocoTestCoverageVerification` passes at the same per-class line-coverage bar `symphony-bdk-http-webclient` enforces
- [x] 10.2 Run `./gradlew :symphony-bdk-http:symphony-bdk-http-jdk:build` clean and confirm no Jersey/Apache-HC/Reactor-Netty transitive dependency appears in `dependencies` output
- [x] 10.3 Run the full `./gradlew build` to confirm no regression in `symphony-bdk-core` or other modules

## 11. Documentation and Default Flip (D12)

- [x] 11.1 Update `docs/tech/architecture.md` line 38 so `symphony-bdk-http-jdk` is listed as the default implementation for `symphony-bdk-core`, with `symphony-bdk-http-jersey2` reworded as deprecated-but-supported; leave line 39 (webclient as Spring Boot's default) unchanged
- [x] 11.2 Update `docs/getting-started.md`'s dependency snippets (Maven line ~62, Gradle line ~104) so `symphony-bdk-http-jdk` is the leading example, with jersey2/webclient in the "or" comment; call out the module's `EXPERIMENTAL` status next to the default snippet (per Open Questions/Risks)
- [x] 11.3 Update `docs/migration.md`'s equivalent dependency snippets (lines ~119, ~222) to match the new default, and add a short, explicitly optional "migrating off jersey2" section documenting the two semantic gaps (D1 timeout mapping, D3 filter support) so a consumer can decide before switching implementations
- [x] 11.4 Add `@API(status = API.Status.DEPRECATED)` and javadoc pointing at `symphony-bdk-http-jdk` to `ApiClientJersey2`, `ApiClientBuilderJersey2`, and `ApiClientBuilderProviderJersey2` in `symphony-bdk-http-jersey2` (replacing their current `@API(STABLE)`/`@API(INTERNAL)` status)
- [x] 11.5 Confirm `./gradlew :symphony-bdk-http:symphony-bdk-http-jersey2:build` still passes after the annotation-only change — no behavioral or test changes expected in that module
