## Context

See `proposal.md` for background and motivation. `symphony-bdk-http-jdk` provides a complete implementation of `com.symphony.bdk.http.api.ApiClient` using the standard JDK 11+ `java.net.http.HttpClient`. However, `symphony-bdk-core-spring-boot-starter` still depended on `symphony-bdk-http-jersey` at `implementation` scope and hardcoded `new ApiClientBuilderProviderJersey2()` in `BdkCoreConfig`, preventing users from benefiting from the zero-dependency transport out-of-the-box.

## Goals / Non-Goals

**Goals:**
- Make `symphony-bdk-http-jdk` the default HTTP client for `symphony-bdk-core-spring-boot-starter`, `symphony-bdk-cli`, and example modules.
- Make the HTTP transport pluggable in Spring Boot by exposing an `ApiClientBuilderProvider` bean.
- Maintain full backward compatibility for Spring Boot configurations and `ApiClientFactory` calls.
- Maintain isolated test compatibility for test fixtures that depend on `MockApiClient`.

**Non-Goals:**
- Removing or altering the behavior of `symphony-bdk-http-jersey` or `symphony-bdk-http-webclient` (they remain supported alternatives).
- Rewriting `MockApiClient` in `symphony-bdk-core`'s internal test harness.

## Decisions

### D1: Expose `ApiClientBuilderProvider` as an injectable Spring bean
In `BdkCoreConfig`:
```java
@Bean
@ConditionalOnMissingBean
public ApiClientBuilderProvider apiClientBuilderProvider() {
  return new ApiClientBuilderProviderJdk();
}

@Bean
@ConditionalOnMissingBean
public ApiClientFactory apiClientFactory(SymphonyBdkCoreProperties properties,
    ApiClientBuilderProvider apiClientBuilderProvider) {
  return new ApiClientFactory(properties, apiClientBuilderProvider);
}
```
**Rationale**: This enables consumers to easily plug in an alternative transport (e.g. `symphony-bdk-http-jersey` or `symphony-bdk-http-webclient`) simply by declaring an `ApiClientBuilderProvider` bean, without needing to replace or re-implement the entire `ApiClientFactory` bean.

### D2: Retain single-parameter `apiClientFactory` overload for source compatibility
Provide:
```java
public ApiClientFactory apiClientFactory(SymphonyBdkCoreProperties properties) {
  return apiClientFactory(properties, apiClientBuilderProvider());
}
```
**Rationale**: Existing Java unit tests or custom configurations invoking `config.apiClientFactory(properties)` directly continue to compile and function without modification.

### D3: Scope `symphony-bdk-http-jersey` strictly to `testImplementation` in starter
In `symphony-bdk-core-spring-boot-starter/build.gradle`:
- `implementation project(':symphony-bdk-http:symphony-bdk-http-jdk')`
- `testImplementation project(':symphony-bdk-http:symphony-bdk-http-jersey')`

And in `SymphonyBdkMockedConfiguration$ApiClientFactoryMock`:
```java
public ApiClientFactoryMock(BdkConfig config) {
  super(config, new ApiClientBuilderProviderJdk());
}
```
**Rationale**: `MockApiClient` (inherited from `symphony-bdk-core` test output) relies on Jersey's `ApiClientJersey2` and `RuntimeDelegate` at test time. Retaining Jersey in `testImplementation` allows tests to run without leaking Jersey into consumer runtime classpaths. Explicitly passing `ApiClientBuilderProviderJdk` to `super(...)` prevents `ServiceLookup.lookupSingleService` from encountering multiple SPI implementations during tests.

### D4: Update CLI distribution and examples to runtimeOnly JDK
CLI distribution (`symphony-bdk-cli`) and examples (`bdk-ai-agent-example`, `bdk-core-examples`, `bdk-group-example`, `bdk-multi-instances-example`) bundle or reference `symphony-bdk-http-jdk` at `runtimeOnly` (or `implementation`) scope, eliminating obsolete Jersey dependencies.

## Risks / Trade-offs

- **[Risk] Behavioral difference between JDK HttpClient and Jersey client (e.g. read timeout semantics)** → Documented in `docs/migration-4.x.md`; consumers requiring Jersey can supply an `ApiClientBuilderProvider` bean or dependency override.
- **[Risk] Multiple SPI providers on classpath if consumer adds both jars** → `ServiceLookup` fails fast with an explicit error message prompting the user to remove one of the conflicting dependencies.
