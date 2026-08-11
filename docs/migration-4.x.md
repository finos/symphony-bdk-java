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

## Support window

BDK 3.x will receive critical security fixes for **6 months** following the BDK 4.0.0 release, where Symphony is
able to backport them (Spring itself provides no support guarantee for superseded package versions once a Spring
Boot major line goes out of support). After that window, BDK 3.x is unsupported.

If your application cannot move to Java 25 within that window, staying on BDK 3.x is the supported option for as
long as this window remains open — there is no partial 4.x compatibility path that avoids the Java 25 requirement.

The `3.x` branch on GitHub continues to exist after `main` moves to 4.x, and its own documentation remains reachable
at [symphony-bdk-java.finos.org/3.x/](https://symphony-bdk-java.finos.org/3.x/).
