## 1. Spring Boot Starter HTTP Transport

- [x] 1.1 In `symphony-bdk-spring/symphony-bdk-core-spring-boot-starter/build.gradle`, replace `implementation project(':symphony-bdk-http:symphony-bdk-http-jersey')` with `implementation project(':symphony-bdk-http:symphony-bdk-http-jdk')` and retain `testImplementation project(':symphony-bdk-http:symphony-bdk-http-jersey')`.
- [x] 1.2 In `BdkCoreConfig`, expose `@Bean @ConditionalOnMissingBean ApiClientBuilderProvider` returning `ApiClientBuilderProviderJdk`.
- [x] 1.3 In `BdkCoreConfig`, inject `ApiClientBuilderProvider` into `apiClientFactory`, and add single-argument `apiClientFactory(properties)` delegation overload for source compatibility.
- [x] 1.4 In `SymphonyBdkMockedConfiguration$ApiClientFactoryMock`, explicitly pass `new ApiClientBuilderProviderJdk()` to `super(...)` to prevent classpath `ServiceLookup` conflicts.
- [x] 1.5 In `BdkCoreConfigTest`, add unit tests verifying `apiClientBuilderProvider()` produces `ApiClientBuilderProviderJdk` by default and custom provider injection works with `apiClientFactory`.
- [x] 1.6 Verify `symphony-bdk-core-spring-boot-starter` and `symphony-bdk-app-spring-boot-starter` build and tests pass via `./gradlew :symphony-bdk-spring:symphony-bdk-core-spring-boot-starter:check :symphony-bdk-spring:symphony-bdk-app-spring-boot-starter:check`.

## 2. CLI and Example Modules

- [x] 2.1 In `symphony-bdk-cli/build.gradle`, switch bundled runtime dependency from `symphony-bdk-http-jersey` to `symphony-bdk-http-jdk`.
- [x] 2.2 In `symphony-bdk-examples/bdk-ai-agent-example/build.gradle`, switch `symphony-bdk-http-jersey` to `symphony-bdk-http-jdk`.
- [x] 2.3 In `symphony-bdk-examples/bdk-core-examples/build.gradle`, remove unused Jersey client/multipart dependencies and switch to `runtimeOnly project(':symphony-bdk-http:symphony-bdk-http-jdk')`.
- [x] 2.4 In `symphony-bdk-examples/bdk-group-example/build.gradle`, switch `symphony-bdk-http-jersey` to `symphony-bdk-http-jdk`.
- [x] 2.5 In `symphony-bdk-examples/bdk-multi-instances-example/build.gradle`, add `runtimeOnly project(':symphony-bdk-http:symphony-bdk-http-jdk')`.
- [x] 2.6 Verify CLI and all examples build and pass tests via `./gradlew :symphony-bdk-cli:check :symphony-bdk-examples:bdk-core-examples:check :symphony-bdk-examples:bdk-spring-boot-example:check :symphony-bdk-examples:bdk-app-spring-boot-example:check :symphony-bdk-examples:bdk-ai-agent-example:check :symphony-bdk-examples:bdk-group-example:check :symphony-bdk-examples:bdk-multi-instances-example:check`.

## 3. Documentation

- [x] 3.1 In `docs/migration.md`, update Maven dependency snippets and prose references from `symphony-bdk-http-jersey` to `symphony-bdk-http-jdk`.

## 4. End-to-End Validation

- [x] 4.1 Run full project verification via `./gradlew check` and confirm all tasks succeed.
