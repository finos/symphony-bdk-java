## 1. Module Scaffolding

- [ ] 1.1 Create `symphony-bdk-http/symphony-bdk-http-jdk/build.gradle` applying `bdk.java-library-conventions` + `bdk.java-publish-conventions`, with `api project(':symphony-bdk-http:symphony-bdk-http-api')` and `jackson-databind`, `jackson-datatype-jsr310`, `jackson-databind-nullable`, `slf4j-api`, `apiguardian-api`, plus test deps mirroring jersey2 (`junit-jupiter`, `logback-classic`, `mockserver-netty`, `mockito-core`, `mockito-junit-jupiter`, `junit-platform-launcher`)
- [ ] 1.2 Add `include(':symphony-bdk-http:symphony-bdk-http-jdk')` to root `settings.gradle`
- [ ] 1.3 Add `api "org.finos.symphony.bdk:symphony-bdk-http-jdk:$project.version"` to `symphony-bdk-bom/build.gradle`
- [ ] 1.4 Confirm `./gradlew :symphony-bdk-http:symphony-bdk-http-jdk:build` succeeds with an empty module before adding implementation code

## 2. JSON Serialization (D5)

- [ ] 2.1 Create `com.symphony.bdk.http.jdk.JSON`, configuring an `ObjectMapper` matching jersey2's: `NON_NULL` inclusion, `FAIL_ON_UNKNOWN_PROPERTIES=false`, `FAIL_ON_INVALID_SUBTYPE=false`, enums via `toString`, `JavaTimeModule` and `JsonNullableModule` registered
- [ ] 2.2 Create an `RFC3339DateFormat`-equivalent date formatter producing the same wire format as jersey2's
- [ ] 2.3 Unit test: date field serializes/deserializes to the same RFC3339 string jersey2 produces for the same value (spec: JSON Serialization Parity)
- [ ] 2.4 Unit test: response containing an unrecognized JSON property deserializes without throwing (spec: JSON Serialization Parity)

## 3. Core `ApiClientJdk` — Request Building

- [ ] 3.1 Create `com.symphony.bdk.http.jdk.ApiClientJdk implements ApiClient`, building `HttpRequest`s from `basePath` + `path`, query params via `parameterToPairs`/`escapeString`, headers, and cookies
- [ ] 3.2 Implement `X-Trace-Id` header injection via `DistributedTracingContext`: generate-if-absent, clear only if generated (spec: Distributed Tracing Header Propagation)
- [ ] 3.3 Unit test: trace ID generated and cleared when absent before the call
- [ ] 3.4 Unit test: existing trace ID preserved and not cleared when already set before the call
- [ ] 3.5 Implement `application/x-www-form-urlencoded` body encoding (spec: ApiClient Contract Conformance)
- [ ] 3.6 Unit test: form params encode as `key=value` pairs joined with `&`, percent-encoded

## 4. Multipart Body Encoding (D6)

- [ ] 4.1 Implement multipart/form-data body construction: boundary generation, part encoding for `File`
- [ ] 4.2 Extend multipart encoding to `Collection<File>` (one part per file), `ApiClientBodyPart`, `ApiClientBodyPart[]`, and plain string fields
- [ ] 4.3 Use a streaming `BodyPublisher` for file parts to avoid buffering entire files into memory
- [ ] 4.4 Unit test: `File` form param produces a multipart part with matching content and filename
- [ ] 4.5 Unit test: `Collection<File>` form param produces one multipart part per file
- [ ] 4.6 Unit test: `ApiClientBodyPart`/`ApiClientBodyPart[]` form params produce parts from their `InputStream` content and filename
- [ ] 4.7 Integration test against MockServer: a real multipart file upload round-trips correctly

## 5. Response Handling

- [ ] 5.1 Implement response deserialization via the `JSON` `ObjectMapper` for non-`File`/`byte[]` return types
- [ ] 5.2 Implement file-download responses via `HttpResponse.BodyHandlers.ofFile(Path)`, writing into `temporaryFolderPath`, honoring `Content-Disposition` filename when present (spec: File Download Responses)
- [ ] 5.3 Implement `byte[]` return type handling
- [ ] 5.4 Implement `204 No Content` → `ApiResponse` with null data
- [ ] 5.5 Implement non-2xx handling: read body as string, throw `ApiException(status, message, headers, body)`
- [ ] 5.6 Unit test: response with `Content-Disposition: attachment; filename="report.pdf"` and return type `File` writes a file named `report.pdf` into `temporaryFolderPath`

## 6. Exception Translation (D2)

- [ ] 6.1 Catch `HttpTimeoutException` around `send`/unwrapped `sendAsync().join()` calls and rethrow as `java.net.SocketTimeoutException` with the original as cause
- [ ] 6.2 Confirm `HttpConnectTimeoutException`, `java.net.ConnectException`, and `java.net.UnknownHostException` propagate unmodified (no translation needed — already the right root-cause types)
- [ ] 6.3 Unit test: a connect-timeout scenario surfaces `java.net.ConnectException` as the root cause (spec: Exception Translation for Retry Compatibility)
- [ ] 6.4 Unit test: a request-timeout scenario surfaces `java.net.SocketTimeoutException` as the root cause (spec: Exception Translation for Retry Compatibility)
- [ ] 6.5 Integration test: `RetryWithRecoveryBuilder`'s retry predicate treats both translated exceptions as retryable, confirming interop with `symphony-bdk-core`'s existing retry logic unmodified

## 7. `ApiClientBuilderJdk` — TLS, Proxy, Timeouts (D1, D8, D9)

- [ ] 7.1 Create `com.symphony.bdk.http.jdk.ApiClientBuilderJdk implements ApiClientBuilder`
- [ ] 7.2 Implement `withKeyStore`/`withTrustStore`: build an `SSLContext` via `KeyManagerFactory`/`TrustManagerFactory`, merging default JVM root CAs via `ApiUtils.addDefaultRootCaCertificates`, applied via `HttpClient.Builder#sslContext`
- [ ] 7.3 Implement `withProxy`: `ProxySelector.of(InetSocketAddress)` on `HttpClient.Builder#proxy`
- [ ] 7.4 Implement `withProxyCredentials`: `HttpClient.Builder#authenticator(Authenticator)` responding only to `RequestorType.PROXY`
- [ ] 7.5 Implement `withConnectionTimeout` → `HttpClient.Builder#connectTimeout`; `withReadTimeout` → per-request `HttpRequest.Builder#timeout`, with javadoc documenting the approximation (D1)
- [ ] 7.6 Leave `withConnectionPoolMax`/`withConnectionPoolPerRoute` as the inherited no-op default (D4) — no override needed
- [ ] 7.7 Integration test against MockServer with mutual TLS enforced: keystore/truststore wiring succeeds end-to-end (mirrors `ApiClientBuilderJersey2Test#sslContextIsUsed`)
- [ ] 7.8 Integration test: request routed through a configured proxy
- [ ] 7.9 Integration test: proxy credentials answer a `407` challenge successfully

## 8. Filters and Request Logging (D3)

- [ ] 8.1 Define `addFilter` to accept `Function<HttpRequest.Builder, HttpRequest.Builder>` and throw `IllegalArgumentException` for any other type
- [ ] 8.2 Implement outgoing request logging as a manual wrap around `send`, logging status code, URL, and elapsed time at DEBUG to `com.symphony.bdk.requests.outgoing`
- [ ] 8.3 Unit test: a custom filter function is applied to the outgoing request
- [ ] 8.4 Unit test: passing a non-`Function` filter throws `IllegalArgumentException`
- [ ] 8.5 Unit test: a completed request produces the expected DEBUG log entry (spec: Outgoing Request Logging)

## 9. SPI Registration

- [ ] 9.1 Create `com.symphony.bdk.http.jdk.ApiClientBuilderProviderJdk implements ApiClientBuilderProvider`
- [ ] 9.2 Create `src/main/resources/META-INF/services/com.symphony.bdk.http.api.ApiClientBuilderProvider` containing `com.symphony.bdk.http.jdk.ApiClientBuilderProviderJdk`
- [ ] 9.3 Integration test: with only `symphony-bdk-http-jdk` on the test classpath, `ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class)` resolves it
- [ ] 9.4 Integration test: with `symphony-bdk-http-jdk` and `symphony-bdk-http-jersey2` both on the test classpath, `ServiceLookup.lookupSingleService` throws `IllegalStateException`

## 10. Coverage and Build Verification

- [ ] 10.1 Confirm `jacocoTestCoverageVerification` passes at the same per-class line-coverage bar `symphony-bdk-http-webclient` enforces
- [ ] 10.2 Run `./gradlew :symphony-bdk-http:symphony-bdk-http-jdk:build` clean and confirm no Jersey/Apache-HC/Reactor-Netty transitive dependency appears in `dependencies` output
- [ ] 10.3 Run the full `./gradlew build` to confirm no regression in `symphony-bdk-core` or other modules

## 11. Documentation

- [ ] 11.1 Update `docs/tech/architecture.md` to list `symphony-bdk-http-jdk` as a third `ApiClient` implementation
- [ ] 11.2 Update `docs/getting-started.md`'s dependency snippet comment to include `symphony-bdk-http-jdk` as a runtime HTTP dependency option
- [ ] 11.3 Update `docs/migration.md` with a module-selection section documenting the two semantic gaps (D1 timeout mapping, D3 filter support) so a consumer can decide before switching implementations
