## Context

The `GET /v5/users/current/clientdata` endpoint returns personal client data for the authenticated user, aggregated across custom folders, multi-chat workspaces, and distribution lists for application `symphonyPrime`.

This design introduces a first-class `ClientDataService` and its OBO counterpart `OboClientDataService` in `symphony-bdk-core`, wired into `ServiceFactory`, `SymphonyBdk`, `OboServices`, and Spring Boot auto-configurations.

## Goals / Non-Goals

**Goals:**
- Provide a first-class service for retrieving client data (`ClientDataResponse`), folders, workspaces, and distribution lists.
- Support both bot service account sessions and On-Behalf-Of (OBO) user sessions.
- Provide automatic retry with recovery on 401 unauthorized (token refresh) via `RetryWithRecoveryBuilder`.
- Provide Spring Boot starter auto-configurations for bot and OBO modes.
- Align with upstream FINOS `symphony-api-spec` updates (`MessagesApi` replacing `StreamContextApi`).

**Non-Goals:**
- Modifying or updating client data (endpoint is read-only).
- Creating or managing distribution list recipients.

## Decisions

**D1 — Package Structure.** The service is placed in its own dedicated package, `com.symphony.bdk.core.service.clientdata`, alongside other functional service packages (`stream`, `message`, `user`, etc.). It includes `package-info.java` annotated with `@NullMarked`.

**D2 — Service Interface and Ergonomics.** Following BDK conventions (e.g., `UserService` and `OboUserService`), `ClientDataService` implements `OboClientDataService` and `OboService<OboClientDataService>`. In addition to the full aggregated `getClientData()` method, convenience methods (`getUserFolders()`, `getUserWorkspaces()`, `getUserDistributionLists()`) extract sub-components directly.

**D3 — OBO Support.** Calling `.obo(AuthSession)` on `ClientDataService` creates a new instance scoped to the user session, while maintaining the same retry policies. `OboServices.clientData()` provides entry-point access.

**D4 — Upstream MessagesApi Alignment.** OpenAPI specification commit `a0b152881a0405b7510cfc7e3a9698e8cb8c566c` on FINOS includes PR #238, which changed the OpenAPI tag for `/v1/stream/{sid}/context` from `StreamContext` to `Messages`. Consequently, `StreamContextApi` was removed by OpenAPI generator and its operation consolidated into `MessagesApi`. `StreamService`, `ServiceFactory`, and Spring starter configurations are updated to use `MessagesApi` directly.
