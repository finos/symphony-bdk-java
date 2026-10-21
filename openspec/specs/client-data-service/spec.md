# client-data-service Specification

## Purpose
Provides access to personal client data (custom folders, multi-chat workspaces, and distribution lists) for authenticated bot and On-Behalf-Of (OBO) user sessions via the `GET /v5/users/current/clientdata` Symphony endpoint.

## Requirements

### Requirement: Client Data Retrieval
`ClientDataService` and `OboClientDataService` SHALL retrieve the authenticated user's aggregated client data from the `GET /v5/users/current/clientdata` endpoint, returning a `ClientDataResponse` containing the user's folders, workspaces, and distribution lists for application `symphonyPrime`.

#### Scenario: Successful client data retrieval
- **WHEN** `getClientData()` is called with an active bot or OBO session
- **THEN** the client data endpoint is queried and a populated `ClientDataResponse` is returned

#### Scenario: Unauthenticated invocation
- **WHEN** `getClientData()` is called on an unauthenticated service instance without an active `AuthSession`
- **THEN** an `IllegalStateException` is thrown before any network call is initiated

### Requirement: Folders and Workspaces Access
`ClientDataService` and `OboClientDataService` SHALL provide convenience accessors `getUserFolders()` and `getUserWorkspaces()` that return lists of `ClientDataStructure` elements from the aggregated client data.

#### Scenario: Retrieve user folders
- **WHEN** `getUserFolders()` is called
- **THEN** the list of `ClientDataStructure` folders is returned, each containing `id`, `name`, and conversation `streamIds`

#### Scenario: Retrieve user workspaces
- **WHEN** `getUserWorkspaces()` is called
- **THEN** the list of `ClientDataStructure` workspaces is returned, each containing `id`, `name`, and multi-chat `streamIds`

### Requirement: Distribution Lists Access
`ClientDataService` and `OboClientDataService` SHALL provide a convenience accessor `getUserDistributionLists()` returning the list of `DistributionLists` from the aggregated client data, each containing recipients of type user, stream, or folder.

#### Scenario: Retrieve distribution lists
- **WHEN** `getUserDistributionLists()` is called
- **THEN** the list of `DistributionLists` is returned with recipient IDs, types, user names, and folder stream IDs

### Requirement: OBO Session Support
`ClientDataService` SHALL implement `OboService<OboClientDataService>` such that calling `.obo(AuthSession)` produces an instance scoped to the specified user session, using the user session token for authentication.

#### Scenario: OBO execution
- **WHEN** `.obo(oboSession)` is invoked with an OBO auth session
- **THEN** subsequent calls execute against `/v5/users/current/clientdata` using the OBO session token

### Requirement: BDK Entry Point Availability
`SymphonyBdk` and `OboServices` SHALL expose `clientData()` and `getClientDataService()` accessors returning `ClientDataService` and `OboClientDataService` respectively. `ServiceFactory` SHALL provide `getClientDataService()`.

#### Scenario: Entry point retrieval
- **WHEN** `bdk.clientData()` or `oboServices.clientData()` is invoked
- **THEN** a fully initialized client data service is returned
