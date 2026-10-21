## Why

Symphony users organize personal workflows and conversation spaces in the client through custom folders (grouping conversation streams), multi-chat workspaces, and distribution lists. Previously, the BDK Java SDK had no service to access this client-side organization data, requiring bots or extension applications to either manage state externally or call underlying REST endpoints without BDK retry, auth session, or pagination support.

With the addition of the `GET /v5/users/current/clientdata` endpoint in SBE Users and its formal specification in `symphony-api-spec`, BDK Java needs a first-class service to expose this data cleanly to bots and OBO user sessions.

## What Changes

- **New package `com.symphony.bdk.core.service.clientdata`** containing:
  - `ClientDataService`: Service implementing `OboClientDataService` and `OboService<OboClientDataService>` with automatic retry and 401 session token recovery.
  - `OboClientDataService`: Service interface exposing OBO-enabled client data methods.
  - `package-info.java`: Annotated with `@NullMarked`.
- **Client Data Methods**:
  - `getClientData()`: Retrieves aggregated client data (`ClientDataResponse`) containing folders, workspaces, and distribution lists for application `symphonyPrime`.
  - `getUserFolders()`: Convenience accessor returning list of `ClientDataStructure` folders.
  - `getUserWorkspaces()`: Convenience accessor returning list of `ClientDataStructure` workspaces.
  - `getUserDistributionLists()`: Convenience accessor returning list of `DistributionLists`.
- **BDK Entry Point Access**:
  - `SymphonyBdk.clientData()` and `SymphonyBdk.getClientDataService()` accessors.
  - `OboServices.clientData()` and `OboServices.getClientDataService()` accessors.
  - `ServiceFactory.getClientDataService()` factory method.
- **Spring Boot Starter Configuration**:
  - `@Bean clientDataService(...)` auto-configured in `BdkServiceConfig`.
  - `@Bean oboClientDataService(...)` auto-configured in `BdkOboServiceConfig`.
- **OpenAPI Specification Bump & Upstream Alignment**:
  - Bumped `apiBaseUrl` in `symphony-bdk-core/build.gradle` to FINOS `symphony-api-spec` commit `a0b152881a0405b7510cfc7e3a9698e8cb8c566c`.
  - Aligned with upstream PR #238 (`BE-10206`): migrated `StreamService`, `ServiceFactory`, and Spring starter configurations from `StreamContextApi` to `MessagesApi` following the `/v1/stream/{sid}/context` OpenAPI tag update.
- **Testing**:
  - Comprehensive unit test coverage in `ClientDataServiceTest` covering successful responses, empty responses, recipient details, OBO execution, and unauthorized state exceptions.
  - Updated `StreamServiceTest`, `ServiceFactoryTest`, and `OboServicesTest`.

## Capabilities

### New Capabilities
- `client-data-service`: Access to the authenticated user's personal client data structures (custom folders, workspaces, and distribution lists) via bot and OBO sessions.

### Modified Capabilities
*(none)*

## Impact

**Code**:
- New service package `com.symphony.bdk.core.service.clientdata`
- `SymphonyBdk.java`, `OboServices.java`, `ServiceFactory.java`: added client data accessors
- `StreamService.java`, `ServiceFactory.java`: updated to use `MessagesApi`
- `BdkServiceConfig.java`, `BdkOboServiceConfig.java`, `BdkApiClientsConfig.java`: Spring starter bean configurations updated
- `symphony-bdk-core/build.gradle`: bumped `apiBaseUrl`

**Dependencies**:
- No new external runtime dependencies.

**Docs**:
- Updated documentation and capability specs.
