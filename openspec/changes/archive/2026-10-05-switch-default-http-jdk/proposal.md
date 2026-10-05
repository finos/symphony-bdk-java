## Why

In Symphony BDK 4.x, `symphony-bdk-http-jdk` was introduced as the zero-third-party-dependency HTTP client built on Java 11+ `java.net.http.HttpClient` and documented as the recommended default transport for BDK 4.x. However, `symphony-bdk-core-spring-boot-starter` still pulled in `symphony-bdk-http-jersey` as a direct `implementation` dependency and hardcoded `new ApiClientBuilderProviderJersey2()` in `BdkCoreConfig`.

This forced all Spring Boot starter consumers to transitively pull in the heavy Jersey 3 stack, JAX-RS APIs, and Apache HttpClient 5, exposing applications to unnecessary CVE attack surface and preventing easy pluggability. Furthermore, the CLI distribution and example projects still bundled or implemented `symphony-bdk-http-jersey`. Switching `symphony-bdk-spring` and the rest of the workspace to default to `symphony-bdk-http-jdk` fulfills the BDK 4.x architecture vision of a lightweight, zero-third-party default transport.

## What Changes

- **Spring Boot Starter default dependency**: In `symphony-bdk-core-spring-boot-starter`, replace the `implementation` dependency on `symphony-bdk-http-jersey` with `symphony-bdk-http-jdk`.
- **Spring Boot Starter transport pluggability**: In `BdkCoreConfig`, expose a `@ConditionalOnMissingBean` bean of type `ApiClientBuilderProvider` returning `ApiClientBuilderProviderJdk`, and inject it into `apiClientFactory`. Retain a backwards-compatible overload `apiClientFactory(properties)`.
- **Spring Boot Starter test isolation**: Retain `testImplementation project(':symphony-bdk-http:symphony-bdk-http-jersey')` in `symphony-bdk-core-spring-boot-starter` for internal `MockApiClient` compatibility, and pass `ApiClientBuilderProviderJdk` explicitly in `ApiClientFactoryMock` to prevent classpath SPI lookup conflicts during testing.
- **CLI distribution runtime**: Switch `symphony-bdk-cli` distribution runtime dependency from `symphony-bdk-http-jersey` to `symphony-bdk-http-jdk`.
- **Example projects**: Switch `bdk-ai-agent-example`, `bdk-core-examples`, `bdk-group-example`, and `bdk-multi-instances-example` to use `symphony-bdk-http-jdk` as their runtime/default HTTP transport, removing unused Jersey dependencies from `bdk-core-examples`.
- **Documentation**: Update `docs/migration.md` to reference `symphony-bdk-http-jdk` as the default runtime HTTP dependency.

## Capabilities

### Modified Capabilities
- `jdk-http-transport`: Adds requirements for Spring Boot starter default transport wiring, transport pluggability via Spring bean override, and CLI distribution runtime default.

## Impact

- **Dependencies**: Spring Boot applications using `symphony-bdk-core-spring-boot-starter` will now transitively receive `symphony-bdk-http-jdk` instead of `symphony-bdk-http-jersey`. Applications desiring Jersey or WebClient can declare their respective dependency and provide an `ApiClientBuilderProvider` bean or `ApiClientFactory` bean.
- **Transitive CVE surface**: Eliminates Jersey, JAX-RS, and Apache HttpClient from the default starter and CLI dependency trees.
- **Compatibility**: Fully backward compatible for Spring Boot applications using standard BDK starter configurations.
