## ADDED Requirements

### Requirement: SPI Discoverability
The module SHALL register its `ApiClientBuilderProvider` implementation via `META-INF/services/com.symphony.bdk.http.api.ApiClientBuilderProvider` so it is discoverable by `java.util.ServiceLoader` using the same mechanism as `symphony-bdk-http-jersey2` and `symphony-bdk-http-webclient`.

#### Scenario: Sole HTTP implementation on the runtime classpath
- **WHEN** `symphony-bdk-http-jdk` is the only `ApiClientBuilderProvider` implementation on the runtime classpath
- **THEN** `SymphonyBdkBuilder.build()` resolves it via `ServiceLookup.lookupSingleService` without any explicit configuration

#### Scenario: Coexisting with another HTTP implementation
- **WHEN** `symphony-bdk-http-jdk` and another `ApiClientBuilderProvider` implementation (e.g. `symphony-bdk-http-jersey2`) are both present on the runtime classpath
- **THEN** `ServiceLookup.lookupSingleService` throws `IllegalStateException`, the same behavior already enforced for any two coexisting implementations

### Requirement: ApiClient Contract Conformance
`ApiClientJdk` SHALL implement `ApiClient#invokeAPI` fully, encoding query params, form params, and multipart form data equivalently to `ApiClientJersey2` for every value type the shared contract supports.

#### Scenario: Multipart file upload
- **WHEN** a request is built with `contentType = multipart/form-data` and a form param of type `File`
- **THEN** the request body contains a multipart part with the file's content and filename, matching the encoding `ApiClientJersey2` produces for the same input

#### Scenario: Multipart collection of files
- **WHEN** a request is built with `contentType = multipart/form-data` and a form param of type `Collection<File>`
- **THEN** the request body contains one multipart part per file in the collection

#### Scenario: URL-encoded form body
- **WHEN** a request is built with `contentType = application/x-www-form-urlencoded` and one or more form params
- **THEN** the request body is encoded as `key=value` pairs joined with `&`, with each key and value percent-encoded

### Requirement: Distributed Tracing Header Propagation
`ApiClientJdk` SHALL set the `X-Trace-Id` header on every outgoing request via `DistributedTracingContext`, generating a trace ID when none is set and clearing only the IDs it generated itself.

#### Scenario: No trace ID set before the call
- **WHEN** `invokeAPI` is called and `DistributedTracingContext.hasTraceId()` is `false` beforehand
- **THEN** a trace ID is generated and set as the `X-Trace-Id` header, and `DistributedTracingContext` is cleared after the call completes

#### Scenario: Trace ID already set before the call
- **WHEN** `invokeAPI` is called and `DistributedTracingContext.hasTraceId()` is `true` beforehand
- **THEN** the existing trace ID is reused as the `X-Trace-Id` header and is NOT cleared after the call completes

### Requirement: Exception Translation for Retry Compatibility
`ApiClientJdk` SHALL translate transport-level exceptions from `java.net.http.HttpClient` into root causes that `symphony-bdk-core`'s `RetryWithRecoveryBuilder.isNetworkIssueOrMinorError` predicate already recognizes: `java.net.SocketException`, `java.net.ConnectException`, `java.net.SocketTimeoutException`, or `java.net.UnknownHostException`.

#### Scenario: Connect timeout
- **WHEN** the underlying `HttpClient` fails to establish a connection within the configured connect timeout
- **THEN** `invokeAPI` throws an exception whose root cause is `java.net.ConnectException` (via `HttpConnectTimeoutException`, which already extends it)

#### Scenario: Request timeout during send
- **WHEN** a request exceeds its configured per-request timeout while in flight, causing `HttpTimeoutException`
- **THEN** `invokeAPI` throws an exception whose root cause is `java.net.SocketTimeoutException`

### Requirement: TLS Client Certificate Support
`ApiClientBuilderJdk` SHALL build an `SSLContext` from supplied keystore and truststore bytes and passwords, merging in the JVM's default root CA certificates so a custom truststore does not shadow public CAs, and apply it to the built `HttpClient` via `HttpClient.Builder#sslContext`.

#### Scenario: Mutual TLS against a server requiring client certificates
- **WHEN** `ApiClientBuilderJdk` is configured with a keystore and truststore via `withKeyStore`/`withTrustStore` and a request is sent to a server enforcing mutual TLS authentication
- **THEN** the TLS handshake succeeds and the request completes without a certificate error

### Requirement: HTTP Proxy Support
`ApiClientBuilderJdk` SHALL route requests through a configured HTTP proxy host and port, and SHALL answer proxy Basic-Auth challenges when proxy credentials are configured.

#### Scenario: Proxy host and port configured
- **WHEN** `withProxy` is configured with a host and port
- **THEN** outgoing requests are routed through that proxy

#### Scenario: Proxy credentials configured
- **WHEN** `withProxyCredentials` is configured and the proxy responds with a `407 Proxy Authentication Required` challenge
- **THEN** the request is retried with proxy credentials supplied via an `Authenticator` responding to `RequestorType.PROXY`, and completes successfully

### Requirement: JSON Serialization Parity
`ApiClientJdk` SHALL serialize and deserialize request and response bodies using an `ObjectMapper` configuration equivalent to `ApiClientJersey2`'s: dates as RFC3339 strings, enums via `toString`, `NON_NULL` inclusion, unknown properties ignored, and `JsonNullable`-wrapped fields supported.

#### Scenario: Date field round-trip
- **WHEN** a model containing a date field is serialized and then deserialized
- **THEN** the date is represented on the wire as an RFC3339 string identical in format to what `ApiClientJersey2` produces for the same value

#### Scenario: Unknown JSON property in a response
- **WHEN** a response body contains a JSON property not present on the target model class
- **THEN** deserialization succeeds and ignores the unknown property, rather than throwing

### Requirement: File Download Responses
When the expected return type is `File`, `ApiClientJdk` SHALL stream the response body directly to `temporaryFolderPath`, using the filename from the `Content-Disposition` header when present.

#### Scenario: Downloading a file with a Content-Disposition header
- **WHEN** a request whose declared return type is `File` receives a response with a `Content-Disposition: attachment; filename="report.pdf"` header
- **THEN** the response body is written to a file named `report.pdf` inside `temporaryFolderPath`

### Requirement: Timeout Configuration Mapping
`ApiClientBuilderJdk` SHALL map `withConnectionTimeout` to the underlying `HttpClient`'s connect timeout, and SHALL map `withReadTimeout` to the per-request total timeout, since JDK `HttpClient` exposes no distinct socket/read timeout.

#### Scenario: Read timeout configured
- **WHEN** `withReadTimeout` is configured with a duration
- **THEN** every request built by the resulting `ApiClient` has that duration applied via `HttpRequest.Builder#timeout`

### Requirement: Outgoing Request Logging
`ApiClientJdk` SHALL log each outgoing request's status code, URL, and elapsed time at DEBUG level under the `com.symphony.bdk.requests.outgoing` logger, matching the existing logging contract from `ApiClientJersey2RequestLogFilter`.

#### Scenario: A request completes
- **WHEN** a request sent through `ApiClientJdk` receives a response
- **THEN** a DEBUG-level log entry is written to `com.symphony.bdk.requests.outgoing` containing the response status code, the request URL, and the elapsed time
