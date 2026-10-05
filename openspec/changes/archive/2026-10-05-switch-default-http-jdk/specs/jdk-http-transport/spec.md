## ADDED Requirements

### Requirement: Spring Boot Starter Default Transport
The `symphony-bdk-core-spring-boot-starter` module SHALL declare `symphony-bdk-http-jdk` as its default `implementation` HTTP client dependency, and instantiate `ApiClientBuilderProviderJdk` by default.

#### Scenario: Spring Boot application startup with default starter
- **WHEN** a Spring Boot application starts with `symphony-bdk-core-spring-boot-starter` and no custom HTTP client beans
- **THEN** the application context creates an `ApiClientFactory` backed by `ApiClientBuilderProviderJdk` and connects using Java's built-in `HttpClient`

#### Scenario: No Jersey runtime required
- **WHEN** a consumer includes only `symphony-bdk-core-spring-boot-starter` without `symphony-bdk-http-jersey`
- **THEN** application compilation and runtime execution succeed without Jersey or Apache HttpClient classes on the classpath

### Requirement: Spring Boot Starter Transport Pluggability
The `BdkCoreConfig` configuration class SHALL expose an `ApiClientBuilderProvider` bean annotated with `@ConditionalOnMissingBean` to allow overriding the HTTP transport via standard Spring bean definition.

#### Scenario: Custom ApiClientBuilderProvider bean provided
- **WHEN** a Spring Boot application defines a custom bean implementing `ApiClientBuilderProvider`
- **THEN** the custom provider is injected into `ApiClientFactory` instead of `ApiClientBuilderProviderJdk`

### Requirement: CLI Distribution Default Transport
The `symphony-bdk-cli` application module SHALL package `symphony-bdk-http-jdk` as its runtime HTTP implementation for the standalone CLI distribution.

#### Scenario: Running CLI command
- **WHEN** the standalone `bdk` CLI distribution binary executes an API command against Symphony
- **THEN** the command uses `ApiClientJdk` without external third-party HTTP client libraries
