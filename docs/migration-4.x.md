---
layout: default
title: Migration Guide (4.x)
nav_order: 3.1
---

# Migration guide to Symphony BDK 4.0 from 3.x

> [!IMPORTANT]
> **BDK 4.x requires Spring Boot 4.x.** Spring Boot 3.x combined with BDK 4.x is **not supported**. The BDK does not
> attempt cross-major compatibility through conditional autoconfiguration or runtime version detection — a Spring
> Boot 3 application must stay on BDK 3.x.
>
> **BDK 4.x requires Java 25.** This is a change from BDK 3.x's Java 17 requirement.
>
> BDK 3.x will continue to receive critical security fixes for **6 months** after the 4.0.0 release (where possible,
> since Spring itself gives no support guarantees for superseded package versions). The `3.x` branch remains
> available on GitHub for the duration of that window. See [Support window](#support-window) below.

This guide covers what changes for a bot or extension application moving from BDK 3.x to BDK 4.x. The underlying
change is a move from Spring Boot 3.5 to Spring Boot 4 (Spring Framework 7), which brings Jakarta EE 11, Jackson 3,
Netty 4.2, Tomcat 11 and Micrometer 2 along with it, plus a small number of mechanical, major-only cleanups that
rode along with the framework move.

## 1. Move to Spring Boot 4 first

BDK 4.x's Spring modules (`symphony-bdk-core-spring-boot-starter`, `symphony-bdk-app-spring-boot-starter`,
`symphony-bdk-test-spring-boot`) autoconfigure against Spring Boot 4 only. Upgrade your application's Spring Boot
version to 4.x *before* bumping the BDK version, so that the two migrations aren't debugged together:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>4.0.0</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

If your application cannot move to Spring Boot 4 yet, stay on BDK 3.x — there is no partial-compatibility path.

## 2. Java 25 is required

BDK 4.0.0 requires **Java 25**. Update your `JAVA_HOME`, IDE project SDK, and CI runners accordingly, and update any
`--release`/toolchain configuration in your own build to target 25.

> :bulb: If you are following the BDK's own development history, intermediate builds during the 4.x migration
> targeted Java 17 (Spring Boot 4 itself only baselines Java 17). That intermediate state was never released —
> the Java requirement for the published 4.0.0 artifacts is 25.

## 3. Dependency coordinate rename: `symphony-bdk-http-jersey2` → `symphony-bdk-http-jersey`

The Jersey-based HTTP implementation module has been on Jersey 3.x for some time, so the `jersey2` artifactId no
longer described what it shipped. The coordinate is renamed in this major, with **no relocation POM or forwarding
artifact** — a dependency on the old coordinate will fail to resolve rather than silently pulling in an empty jar.

BDK 3.x:
```xml
<dependency>
    <groupId>org.finos.symphony.bdk</groupId>
    <artifactId>symphony-bdk-http-jersey2</artifactId>
    <scope>runtime</scope>
</dependency>
```

BDK 4.x:
```xml
<dependency>
    <groupId>org.finos.symphony.bdk</groupId>
    <artifactId>symphony-bdk-http-jersey</artifactId>
    <scope>runtime</scope>
</dependency>
```

If you use `symphony-bdk-http-webclient` instead, no change is needed here.

## 4. `BdkConfigParser`'s `JsonNode` signatures change package

BDK 4.x adopts Jackson 3 (`tools.jackson.*`) instead of Jackson 2 (`com.fasterxml.jackson.*`) on its public API
surface, to avoid two JSON databind implementations resolving on the same classpath (which would otherwise make the
same BDK call serialize differently depending on which HTTP module — Jersey or WebClient — is present).

This is visible wherever code calls `BdkConfigParser` directly:

| Method                    | BDK 3.x parameter/return type      | BDK 4.x parameter/return type   |
|----------------------------|-------------------------------------|----------------------------------|
| `parse(...)`               | `com.fasterxml.jackson.databind.JsonNode` | `tools.jackson.databind.JsonNode` |
| `parseJsonNode(...)`       | `com.fasterxml.jackson.databind.JsonNode` | `tools.jackson.databind.JsonNode` |
| `interpolateProperties(...)` | `com.fasterxml.jackson.databind.JsonNode` | `tools.jackson.databind.JsonNode` |

This only affects applications calling `BdkConfigParser` directly — most bots load configuration via
`BdkConfigLoader` and never touch this class. If you have hand-written Jackson 2 code elsewhere in your application
that interacts with BDK-returned `JsonNode` instances, it needs the same package update.

## 5. Nullability annotations: JSR-305 → JSpecify

The BDK's public API used `javax.annotation.Nonnull` / `javax.annotation.Nullable` (JSR-305) for nullability. These
are replaced with [JSpecify](https://jspecify.dev/) (`org.jspecify.annotations.NonNull` / `Nullable`), which Spring
Framework 7 itself standardises on.

This is **source-compatible** — nothing in application code needs to change to compile and run. It is visible to:
- IDE null-checking and static analysis tools that read nullability annotations
- Any application code that directly imports `com.google.code.findbugs:jsr305` alongside the BDK and expected JSR-305
  annotations on BDK types

If your application declared its own `@NullMarked` packages against the BDK's previous JSR-305 contract, re-verify
them against the new JSpecify annotations — JSpecify's package-level defaulting is not identical to JSR-305's.

## 6. BOM constraints removed

`symphony-bdk-bom` no longer constrains the following, because they existed solely to pull versions above what
Spring Boot 3.5 pinned — versions Spring Boot 4 already supersedes:
- `io.netty:netty-bom` (Netty 4.1 CVE override)
- `org.apache.tomcat.embed:tomcat-embed-core` / `tomcat-embed-el` / `tomcat-embed-websocket` (Tomcat 10 CVE overrides)
- `jakarta.ws.rs:jakarta.ws.rs-api` and `jakarta.validation:jakarta.validation-api` (Jakarta EE 10 pins)

If your application imported `symphony-bdk-bom` specifically to inherit one of these pins, you now inherit Spring
Boot 4's own choices (Netty 4.2, Tomcat 11, Jakarta EE 11) instead. This is the intended behaviour — re-pin
explicitly in your own build only if you have a reason to diverge from Spring Boot 4's platform.

## 7. Test dependencies

`symphony-bdk-test-spring-boot` re-exports `spring-boot-starter-test` and `spring-boot-starter-web` as `api`
dependencies, so applications depending on it for test support inherit Spring Boot 4's test stack — including
whatever JUnit and Mockito versions Spring Boot 4 manages. Review your test code for any direct dependence on the
previous JUnit/Mockito versions if you don't otherwise manage them explicitly.

If your application uses `symphony-bdk-test-jupiter` or `symphony-bdk-test-spring-boot` (both bring in Mockito
through `@SymphonyBdkTest` / `@SymphonyBdkSpringBootTest`), your own test JVM is subject to the same JDK 25
self-attach restriction the BDK's own build hit: Mockito's inline mock maker can no longer self-attach its agent at
runtime. Configure your build to pass the Mockito agent explicitly, e.g. in Gradle:

```groovy
configurations {
    mockitoAgent
}
dependencies {
    mockitoAgent('org.mockito:mockito-core') { transitive = false }
}
test {
    doFirst {
        jvmArgs "-javaagent:${configurations.mockitoAgent.asPath}"
    }
}
```

Do not silence the self-attach warning with `-XX:+EnableDynamicAgentLoading` — it only postpones the failure to the
JDK release that removes self-attach outright.

## 8. Generated API changes: `openapi-generator` 6.6.0 → 7.x

The generated classes under `com.symphony.bdk.gen.api` and `com.symphony.bdk.gen.api.model` were regenerated with
`openapi-generator` 7.x (`jersey3` library, up from `jersey2`). All 377 classes were diffed between the two
generator versions; the only consumer-visible change is:

- **21 unreferenced `*AllOf` companion classes no longer exist** (e.g. `MessageAllOf`, `CreateGroupAllOf`,
  `V2PresenceAllOf`). These were orphaned classes emitted for schemas using `allOf` composition that nothing in the
  BDK, or any known consumer, ever referenced directly. If your code somehow imported one of these classes by name,
  it needs to be removed — there is no replacement, because nothing replaced their function.

No other generated class changes shape, method signatures, `equals`/`hashCode`/`toString`, or annotations in any
consumer-visible way. Fluent builder method names (`addXxxItem`, `putXxxItem`, etc.) and all constructors are
unchanged.

## 9. `symphony-bdk-http-jdk` becomes the default HTTP client module — migrate off `symphony-bdk-http-jersey` / `symphony-bdk-http-webclient`

BDK 4.x introduces `symphony-bdk-http-jdk`, a third `ApiClient` implementation built directly on
`java.net.http.HttpClient` (available since Java 11, and part of the JDK itself), with **no third-party HTTP
dependency**. Starting with 4.x, `symphony-bdk-http-jdk` **is the default and recommended `ApiClient` implementation**
for both `symphony-bdk-core` and `symphony-bdk-spring` — `docs/getting-started.md` and `docs/tech/architecture.md`
present it first, ahead of `symphony-bdk-http-jersey` and `symphony-bdk-http-webclient`.

**Consumers on `symphony-bdk-http-jersey` or `symphony-bdk-http-webclient` should plan to migrate to
`symphony-bdk-http-jdk`.** Both older modules keep shipping and working for now — existing consumers see no
functional change beyond `symphony-bdk-http-jersey`'s classes now being annotated
`@API(status = API.Status.DEPRECATED)` (a documentation/IDE-warning signal only, not yet a removal notice) — but they
are no longer the modules new and existing projects should depend on going forward.

To switch, replace your `symphony-bdk-http-jersey` (or `symphony-bdk-http-webclient`) runtime dependency
with `symphony-bdk-http-jdk` — `ServiceLoader` picks up the new module's `ApiClientBuilderProvider` automatically, no
code change to `SymphonyBdkBuilder` usage required. Before switching, be aware of two behavioral differences from
`symphony-bdk-http-jersey`:

- **Read timeout semantics.** `java.net.http.HttpClient` has no distinct socket/read timeout, only a connect timeout
  and a per-request *total* timeout. `ApiClientBuilder#withReadTimeout` is mapped to the per-request total timeout —
  the closest available approximation, but it bounds "the whole request took too long" rather than "no bytes arrived
  for N ms". This can matter for large, legitimately slow responses (e.g. large file downloads) that previously
  fit comfortably under a read-timeout-only budget.
- **Filter support.** `java.net.http.HttpClient` has no request/response filter chain. `ApiClientBuilder#addFilter`
  on `ApiClientBuilderJdk` only accepts a `Function<HttpRequest.Builder, HttpRequest.Builder>` (request mutation
  only, e.g. adding a header) instead of an arbitrary Jersey `ClientRequestFilter`/`ClientResponseFilter`. Response-
  inspecting filters cannot be ported to this module.

`symphony-bdk-http-jdk` is currently `@API(status = API.Status.EXPERIMENTAL)`. If you need an `@API(STABLE)`-only
dependency tree, stay on `symphony-bdk-http-jersey` for now.

### 4.2: Spring modules complete the default-to-JDK migration

Through 4.0 and 4.1, `symphony-bdk-core-spring-boot-starter` was the one exception to the above: `BdkCoreConfig`
still depended on `symphony-bdk-http-jersey` at `implementation` scope and hardcoded
`new ApiClientBuilderProviderJersey2()`, so Spring Boot consumers kept getting the Jersey transport by default
regardless of the rest of the BDK's direction. BDK 4.2 closes that last gap: the starter now depends on
`symphony-bdk-http-jdk` at `implementation` scope instead, and `BdkCoreConfig` exposes a
`@Bean @ConditionalOnMissingBean ApiClientBuilderProvider` returning `ApiClientBuilderProviderJdk`, wired into the
`ApiClientFactory` bean — mirroring plain `symphony-bdk-core`'s behaviour.

**For most Spring Boot applications, this is transparent.** If your application only consumes the starter's
auto-configured beans (`agentApiClient`, `podApiClient`, `loginApiClient`, and the services built on top of them) and
does not declare its own `ApiClientBuilderProvider` or `ApiClientFactory` bean, no code change is required — Spring
Boot's dependency management pulls in `symphony-bdk-http-jdk` transitively and the transport switches automatically.

**Possible breaking changes to check before upgrading:**

- **The Jersey-vs-JDK behavioral differences described above now reach Spring consumers too.** The read-timeout
  (total timeout vs. read-only timeout) and filter-support (`Function<HttpRequest.Builder, HttpRequest.Builder>`-only
  vs. JAX-RS `ClientRequestFilter`/`ClientResponseFilter`) differences apply to every Spring Boot application using
  the default starter configuration, not just direct `symphony-bdk-core` users. Check any custom `ApiClientBuilder`
  filter or read-timeout-dependent long-running request after upgrading.
- **`BdkCoreConfig` subclasses overriding `apiClientFactory(SymphonyBdkCoreProperties)` lose bean registration.** That
  single-argument method is no longer annotated `@Bean` — it is kept only as a source-compatible overload delegating
  to the new two-argument, `@Bean`-annotated `apiClientFactory(properties, apiClientBuilderProvider)`. A custom
  configuration that subclassed `BdkCoreConfig` and overrode the single-argument method expecting Spring to invoke it
  as the factory bean will now see that override silently ignored; override the two-argument method instead, or
  declare your own `ApiClientBuilderProvider` bean.
- **`symphony-bdk-http-jersey` no longer rides in transitively through the starter.** It is retained at
  `testImplementation` scope inside the starter module itself (for internal `MockApiClient` test fixture
  compatibility only), not exposed to consumer applications. If your application relied on Jersey/JAX-RS classes
  being present purely because the starter pulled them in, add `symphony-bdk-http-jersey` (or
  `symphony-bdk-http-webclient`) explicitly and declare your own `ApiClientBuilderProvider` bean to keep the previous
  transport.

## 10. `UserService` lookups move to `/v5/users`, the `local` overloads are deprecated

`UserService#listUsersByIds`, `listUsersByEmails` and `listUsersByUsernames` (and their `OboUserService`
counterparts) now call the `/v5/users` lookup endpoint instead of `/pod/v3/users`, except for the overloads taking a
`local` parameter. Those are deprecated and will be removed in a future major version. They keep calling
`/pod/v3/users` unchanged in the meantime. Drop the `local` argument to move to `/v5/users`:

- `listUsersByIds(List<Long> uidList, Boolean local, Boolean active)` → `listUsersByIds(List<Long> uidList, Boolean active)`
- `listUsersByEmails(List<String> emailList, Boolean local, Boolean active)` → `listUsersByEmails(List<String> emailList, Boolean active)`

`searchUsers` and `searchAllUsers` still accept `local`.

On `/v5/users`, the caller no longer chooses whether users are looked up locally or in Directory:

- IDs of the caller's pod are resolved locally, IDs from other pods through Directory, as `/pod/v3/users` did without
  `local=true`.
- Emails are matched against the caller's pod first, and only those without a local match are looked up in Directory.
  `/pod/v3/users` without `local=true` looked up every email in Directory.
- Usernames are only resolved in the caller's pod, as before.
- Directory lookups only return users when the caller is entitled to external IMs or external rooms.
- `active` also filters the users found in Directory, `/pod/v3/users` only applied it to the caller's pod.

This also applies to the `listUsersByIds(List<Long>)` and `listUsersByEmails(List<String>)` overloads, which
previously always passed `local=false`.

Values are deduplicated and sent in batches of 100, so lists longer than 100 elements, which `/pod/v3/users` rejected
with a 400, are now accepted. An empty list returns no user without calling the endpoint. A batch in which every email
is malformed matches no user, unless every batch is rejected, in which case the 400 is raised as an
`ApiRuntimeException`.

`OboUserService` gained the `listUsersByIds(List<Long>, Boolean)` and `listUsersByEmails(List<String>, Boolean)`
methods. Classes implementing `OboUserService` themselves must implement them too.

`/v5/users` is served from the pod root rather than `/pod`, so `UserService` now also takes a `SbeUsersApi` built on
`ApiClientFactory#getUsersClient()`. Both public constructors gained a `SbeUsersApi` parameter after `AuditTrailApi`:

- `UserService(UserApi, UsersApi, AuditTrailApi, AuthSession, RetryWithRecoveryBuilder)` → `UserService(UserApi, UsersApi, AuditTrailApi, SbeUsersApi, AuthSession, RetryWithRecoveryBuilder)`
- `UserService(UserApi, UsersApi, AuditTrailApi, RetryWithRecoveryBuilder)` → `UserService(UserApi, UsersApi, AuditTrailApi, SbeUsersApi, RetryWithRecoveryBuilder)`

This only affects code that instantiates `UserService` directly, e.g. a custom `userService` or `oboUserService` bean.
`SymphonyBdk#users()` and the Spring Boot starter are already wired, and the starter exposes a `SbeUsersApi` bean
built on its `usersApiClient` bean.

The generated `ClientDataApi` class is removed: its `getClientData` operation is now generated on `SbeUsersApi`, with
the same signature. Code calling it directly should build a `SbeUsersApi` on the same `ApiClient` instead, or use
`ClientDataService`.

## 11. `UserService#listUsersDetail` and `listAllUsersDetail` deprecated in favor of `listUsers` and `listAllUsers`

The `listUsersDetail` and `listAllUsersDetail` overloads, which call `/pod/v2/admin/user/list` and
`/pod/v1/admin/user/find`, are deprecated and will be removed in a future major version. They keep working
unchanged, so you can migrate gradually.

Their replacements are `UserService#listUsers`, which returns one page, and `UserService#listAllUsers`, which returns
a `Stream` over the pages. They call the `/v5/users/list` endpoint, which pages on the user ID rather than with skip
and limit. It requires the Administrator, Super Administrator or User Provisioning role, where the deprecated methods
only accepted User Provisioning.

`/v5/users/list` only returns normal users and service accounts. SDL accounts, which `/pod/v2/admin/user/list`
returned too, are left out.

| Deprecated | Replacement |
|------------|-------------|
| `listUsersDetail()` | `listUsers(null, null, 100)`, as `listUsers` returns 50 users when no `limit` is given |
| `listUsersDetail(PaginationAttribute)` | `listUsers(null, lastUserId, limit)` |
| `listAllUsersDetail()`, `listAllUsersDetail(StreamPaginationAttribute)` | `listAllUsers(null)`, `listAllUsers(null, pagination)` |
| `listUsersDetail(UserFilter)`, `listUsersDetail(UserFilter, PaginationAttribute)` | `listUsers(UserListFilter, lastUserId, limit)` |
| `listAllUsersDetail(UserFilter)`, `listAllUsersDetail(UserFilter, StreamPaginationAttribute)` | `listAllUsers(UserListFilter)`, `listAllUsers(UserListFilter, pagination)` |

`listUsers` takes the `lastUserId` of the previous page instead of a skip, and returns at most 100 users per page: a
larger `limit` is reduced to 100. Keep paging while `getHasNext()` is true.

Like `listAllUsersDetail()`, `listAllUsers(filter)` stops after 100 users. Pass a `StreamPaginationAttribute` with a
larger total size to `listAllUsers(filter, pagination)` to retrieve more.

`UserListFilter` replaces the generated `UserFilter`:

- `role(String)` → `roles(List<RoleId>)`, matching users having at least one of the roles. Use
  `roleNames(List<String>)` for roles not listed in `RoleId`.
- `feature(String)` → `enabledEntitlements(List<UserFeature>)`, matching users having all of the entitlements
  enabled. Use `enabledEntitlementNames(List<String>)` for entitlements not listed in `UserFeature`.
- `status(ENABLED | DISABLED)` → `active(true | false)`.
- New: `disabledEntitlements(List<UserFeature>)` (or `disabledEntitlementNames(List<String>)`), matching users
  having all of the entitlements disabled.
- New: `userId`, `createdDateFrom` and `createdDateTo`.

Role and entitlement names are not validated: an unknown role or enabled entitlement matches no user, and an unknown
disabled entitlement excludes none, instead of the call being rejected.

Users are returned as a flat `UserResponse` rather than a `V2UserDetail`:

- `userAttributes` fields are directly on `UserResponse`: `getUserAttributes().getUserName()` → `getUsername()`,
  `getCompanyName()` → `getCompany()`, `getDepartment()`, `getTitle()`, `getAccountType()`, etc.
- `userSystemInfo` fields are directly on `UserResponse` too: `getId()` → `getUserId()`,
  `getStatus()` → `getActive()`, `getCreatedDate()`, `getLastLoginDate()`, `getDeactivatedDate()`, `getSuspended()`,
  etc.
- `getAvatars()` and `getEntitlements()` are filled, where the deprecated methods left `avatar` and `features` empty.
  `getEntitlements()` returns the names of the entitlements enabled on the user, as in `UserFeature`. These may not
  match what the `enabledEntitlements` and `disabledEntitlements` filters select, as the filters also take pod and
  feature level values into account.
- `userMetadata`, `twoFactorAuthPhone`, `recommendedLanguage`, `currentKey`, `previousKey` and `lastPasswordReset`
  are not returned, nor are `apps`, `groups` and `disclaimers`, which the deprecated methods left empty too. Use
  `UserService#getUserDetail(Long)` to get them for a given user.

## Support window

BDK 3.x will receive critical security fixes for **6 months** following the BDK 4.0.0 release, where Symphony is
able to backport them (Spring itself provides no support guarantee for superseded package versions once a Spring
Boot major line goes out of support). After that window, BDK 3.x is unsupported.

If your application cannot move to Java 25 within that window, staying on BDK 3.x is the supported option for as
long as this window remains open — there is no partial 4.x compatibility path that avoids the Java 25 requirement.

The `3.x` branch on GitHub continues to exist after `main` moves to 4.x, and its own documentation remains reachable
at [symphony-bdk-java.finos.org/3.x/](https://symphony-bdk-java.finos.org/3.x/).
