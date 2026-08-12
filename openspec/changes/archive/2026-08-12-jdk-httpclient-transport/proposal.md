## Why

`symphony-bdk-core` talks to the Symphony REST APIs through a pluggable `ApiClient` contract (`symphony-bdk-http-api`), and today exactly two runtime implementations exist: `symphony-bdk-http-jersey2` (Jersey 2 + Apache HttpClient) and `symphony-bdk-http-webclient` (Spring WebClient + Reactor Netty). Both pull in a third-party HTTP stack as a mandatory runtime dependency, and both require a consumer to accept that stack's own transitive dependency and CVE surface.

Since JDK 11, `java.net.http.HttpClient` ships inside the JDK itself, with mutual-TLS, HTTP/2, proxying, and both synchronous and asynchronous sends built in. BDK's toolchain has been on Java 17 since `modernize-build-toolchain`, with `adopt-java-25-baseline` proposing to move further, so this capability has been available and unused for the entire lifetime of BDK 3.x/4.x. A third implementation that adds **zero** new external HTTP dependencies is now straightforward to offer, and gives consumers who care about minimal footprint, dependency hygiene, or avoiding Jersey/Apache-HC or Reactor Netty specifically, a first-class alternative rather than a reason to fork or hand-roll their own `ApiClient`.

Once that alternative exists, keeping `symphony-bdk-http-jersey2` as the module every "getting started" doc and dependency snippet points to by default stops making sense: it's the option with the largest dependency and CVE surface (Jersey 2, Apache HttpClient, `jjwt`, BouncyCastle), for a benefit — Jersey-specific request/response filters — that nothing in the current codebase actually uses. This proposal makes `symphony-bdk-http-jdk` the new documented default for `symphony-bdk-core` and formally deprecates jersey2 in that role. Deprecation here means a soft, non-removing signal (`@API(status = API.Status.DEPRECATED)` plus javadoc pointing at the replacement, matching the annotation-based status system this codebase already uses for `STABLE`/`EXPERIMENTAL`/`INTERNAL`) — existing consumers who already depend on `symphony-bdk-http-jersey2` see no functional change and are not forced to migrate.

## What Changes

- **New module `symphony-bdk-http/symphony-bdk-http-jdk`**, published as `org.finos.symphony.bdk:symphony-bdk-http-jdk`, implementing `ApiClient`, `ApiClientBuilder`, and `ApiClientBuilderProvider` on top of `java.net.http.HttpClient`. Registered in `settings.gradle` and added as a BOM constraint in `symphony-bdk-bom/build.gradle`, following the exact pattern of the two existing modules.
- **Full behavioral parity with `symphony-bdk-http-jersey2`** wherever the shared contract requires it, so a consumer can swap implementations without observing a difference on the wire or in application code:
  - Query/form/multipart param encoding, including `File`, `Collection<File>`, and `ApiClientBodyPart`/`ApiClientBodyPart[]` form values (JDK `HttpClient` has no built-in multipart body support, so this is hand-rolled — see design D-Multipart).
  - JSON serialization matching jersey2's `ObjectMapper` configuration: RFC3339 date strings, enums via `toString`, `NON_NULL` inclusion, `FAIL_ON_UNKNOWN_PROPERTIES=false`, `JsonNullableModule`/`JavaTimeModule` registered.
  - `X-Trace-Id` header injection/cleanup via `DistributedTracingContext`, matching `ApiClientJersey2`'s generate-if-absent-then-clear behavior.
  - File-download responses streamed to `temporaryFolderPath`, honoring `Content-Disposition` filename.
  - TLS client certificate configuration (keystore/truststore bytes + password) via `HttpClient.Builder#sslContext`.
  - HTTP proxy host/port and credentials.
  - Outgoing request logging under the existing `com.symphony.bdk.requests.outgoing` logger.
- **Exception translation into the same root-cause types `symphony-bdk-core`'s retry predicate already inspects** (`RetryWithRecoveryBuilder.isNetworkIssueOrMinorError` checks for `java.net.SocketException`, `java.net.ConnectException`, `java.net.SocketTimeoutException`, `java.net.UnknownHostException`). This keeps retry behavior working unmodified for consumers who switch implementations — no changes to `symphony-bdk-core`'s retry code are in scope.
- **SPI registration** via `META-INF/services/com.symphony.bdk.http.api.ApiClientBuilderProvider`, the same `ServiceLoader` mechanism the two existing modules use. `ServiceLookup.lookupSingleService` already enforces exactly one implementation on the runtime classpath at a time — unchanged, and applies to the new module the same as the existing two.
- **Two documented, deliberate gaps versus jersey2**, not silently absorbed:
  - JDK `HttpClient` has no distinct read/socket timeout, only a connect timeout and a per-request total timeout. `withReadTimeout` is mapped to the per-request timeout as the closest available approximation (design D1).
  - JDK `HttpClient` has no request/response filter chain. `addFilter` accepts a narrower request-mutation-only functional type instead of an arbitrary Jersey filter (design D3).
  - `withConnectionPoolMax`/`withConnectionPoolPerRoute` remain no-ops, the same precedent already set by `symphony-bdk-http-webclient`, since JDK `HttpClient` has no per-instance pool-sizing knob.
- **`symphony-bdk-http-jdk` becomes the documented default for `symphony-bdk-core`**, replacing `symphony-bdk-http-jersey2` in that role: `docs/tech/architecture.md`, `docs/getting-started.md`, and `docs/migration.md` are updated so the default dependency snippet and prose lead with `symphony-bdk-http-jdk`, with jersey2 and webclient presented as alternatives (webclient keeps its existing status as the Spring Boot starter's default, which this change does not touch).
- **`symphony-bdk-http-jersey2` is formally deprecated, not removed**: `ApiClientJersey2`, `ApiClientBuilderJersey2`, and `ApiClientBuilderProviderJersey2` move from `@API(STABLE)` to `@API(status = API.Status.DEPRECATED)`, with javadoc pointing consumers at `symphony-bdk-http-jdk`. The module keeps shipping, keeps its `ServiceLoader` registration, and gets no functional changes — a consumer with an existing `symphony-bdk-http-jersey2` runtime dependency observes nothing beyond a `@Deprecated`-style IDE warning.
- **Documentation**: `docs/tech/architecture.md`, `docs/getting-started.md`, and `docs/migration.md` updated to present the new module as the default `symphony-bdk-core` HTTP implementation, with a short "migrating off jersey2" note in `docs/migration.md` for consumers who want to switch.
- **Explicitly out of scope**: the Spring Boot starter's hardcoded `ApiClientBuilderProviderJersey2` wiring in `BdkCoreConfig` is untouched — webclient remains the Spring Boot starter's default, and making the Spring starter's HTTP implementation pluggable is separate work. Removing the jersey2 module or forcing migration is also out of scope; this change only changes the *recommendation*.
- **Not breaking.** This is an additive module plus a soft, non-removing deprecation of jersey2's default status. No existing `ApiClient`/`ApiClientBuilder` contract, generated API code, or published artifact changes; `symphony-bdk-http-jersey2` keeps working exactly as before for consumers who keep it as a runtime dependency.

## Capabilities

### New Capabilities
- `jdk-http-transport`: a `java.net.http.HttpClient`-based implementation of the BDK's `ApiClient`/`ApiClientBuilder` contract — its behavioral parity with the existing implementations, its documented semantic gaps, and its SPI discoverability.

### Modified Capabilities
*(none tracked in `openspec/specs` — `symphony-bdk-http-jersey2` has no existing capability spec to modify. Its deprecation is described in this proposal's "What Changes" and in design.md D12, not as a spec delta)*

## Impact

**Code**
- New module tree `symphony-bdk-http/symphony-bdk-http-jdk/` (`build.gradle`, `src/main/java/com/symphony/bdk/http/jdk/*`, `src/main/resources/META-INF/services/com.symphony.bdk.http.api.ApiClientBuilderProvider`, `src/test/java/com/symphony/bdk/http/jdk/*`)
- `settings.gradle`: add `include(':symphony-bdk-http:symphony-bdk-http-jdk')`
- `symphony-bdk-bom/build.gradle`: add `api "org.finos.symphony.bdk:symphony-bdk-http-jdk:$project.version"`
- `symphony-bdk-http/symphony-bdk-http-jersey2/src/main/java/com/symphony/bdk/http/jersey2/{ApiClientJersey2,ApiClientBuilderJersey2,ApiClientBuilderProviderJersey2}.java`: annotation-only change, `@API(STABLE)`/`@API(INTERNAL)` → `@API(status = API.Status.DEPRECATED)`, plus javadoc pointing at `symphony-bdk-http-jdk`
- `docs/tech/architecture.md`, `docs/getting-started.md`, `docs/migration.md`: flip the default HTTP module recommendation from jersey2 to jdk

**APIs**:
- New artifact `org.finos.symphony.bdk:symphony-bdk-http-jdk` (additive)
- No changes to `symphony-bdk-http-api`'s `ApiClient`, `ApiClientBuilder`, `ApiClientBuilderProvider`, `ApiException`, or any generated class
- Consumers choosing the jdk module get a subset of `ApiClientBuilder`'s optional knobs (see the two documented gaps above); this is implementation-specific behavior, not a contract change
- `symphony-bdk-http-jersey2`'s public classes gain a `DEPRECATED` `@API` status; this is a source-compatible annotation change, not a binary or behavioral change — the module still compiles, ships, and functions identically for existing consumers

**Dependencies**: none new beyond what's already BOM-pinned (`jackson-databind`, `jackson-datatype-jsr310`, `jackson-databind-nullable`, `slf4j-api`, `apiguardian-api`). No new external HTTP library — that is the point of the module.

**Docs**: three files updated so the new module is the one new users are pointed to first, jersey2 is presented as deprecated-but-supported, and webclient's status (Spring Boot starter default) is unchanged; no versioned-docs split needed since this remains additive and non-breaking, not a forced migration.
