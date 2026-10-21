## 1. OpenAPI Specification Bump & Code Generation

- [x] 1.1 Bump `apiBaseUrl` in `symphony-bdk-core/build.gradle` to FINOS `symphony-api-spec` commit `a0b152881a0405b7510cfc7e3a9698e8cb8c566c`
- [x] 1.2 Generate OpenAPI client and models: `ClientDataApi`, `ClientDataResponse`, `ClientDataStructure`, `DistributionLists`, `DistributionListRecipient`
- [x] 1.3 Align `StreamService` and `ServiceFactory` with `MessagesApi` replacing `StreamContextApi`

## 2. Core Service Implementation

- [x] 2.1 Create `com.symphony.bdk.core.service.clientdata.OboClientDataService` interface declaring `getClientData()`, `getUserFolders()`, `getUserWorkspaces()`, `getUserDistributionLists()`
- [x] 2.2 Create `com.symphony.bdk.core.service.clientdata.ClientDataService` implementing `OboClientDataService` and `OboService<OboClientDataService>` with `RetryWithRecoveryBuilder`
- [x] 2.3 Add `getClientDataService()` factory method to `ServiceFactory`
- [x] 2.4 Add `clientData()` and `getClientDataService()` accessors to `SymphonyBdk`
- [x] 2.5 Add `clientData()` and `getClientDataService()` accessors to `OboServices`
- [x] 2.6 Add `package-info.java` annotated with `@NullMarked`

## 3. Spring Boot Auto-Configuration

- [x] 3.1 Register `@Bean clientDataService` in `BdkServiceConfig`
- [x] 3.2 Register `@Bean oboClientDataService` in `BdkOboServiceConfig`
- [x] 3.3 Update `BdkApiClientsConfig`, `BdkServiceConfig`, and `BdkOboServiceConfig` for `MessagesApi`

## 4. Testing & Verification

- [x] 4.1 Unit tests in `ClientDataServiceTest` covering `getClientData`, `getUserFolders`, `getUserWorkspaces`, `getUserDistributionLists`, empty response, OBO mode, and unauthenticated errors
- [x] 4.2 Update `StreamServiceTest`, `ServiceFactoryTest`, and `OboServicesTest`
- [x] 4.3 Run full Gradle build, checks, and test coverage verification
