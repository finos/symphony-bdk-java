## Context

BDK 4.x will make two moves that each invalidate build tooling: **Spring Boot 3.5 → 4.x** (`migrate-spring-boot-4`) and **Java 17 → 25** (`adopt-java-25-baseline`). The second is the destructive one for the build, because JDK 25 emits class file version 69 and every tool in the build that reads bytecode has a version ceiling:

```
JDK 25  ──emits──▶  class file v69
                        │
        ┌───────────────┴────────────────────────────────┐
        │  tools in this build that must understand v69  │
        ├───────────────────────────────────────────────┤
        │  Gradle 8.14.5      → cannot target JDK 25     │
        │  byte-buddy 1.12.19 → explicit pin in core     │
        │  mapstruct 1.4.2    → annotation processor     │
        │  archunit 1.2.1     → reads our own classes    │
        │  jacoco (default)   → instruments bytecode     │
        │  mockito (via SB BOM) → byte-buddy transitively│
        │  lombok (via SB BOM) → hooks compiler internals│
        └────────────────────────────────────────────────┘
```

All of these can be raised while still targeting Java 17 and Spring Boot 3.5. That makes this change a self-contained, independently shippable prerequisite rather than part of the migration proper. It is sequenced **first** and lands on `main` (the 3.x line); the `next` branch is cut from — or rebased onto — `main` after it merges.

The scope boundary is drawn at "does this alter what consumers compile against?" Gradle version, processor versions, and test-harness versions do not. Java version, Spring Boot version, and generated model classes do. The latter three are excluded.

## Goals / Non-Goals

**Goals:**
- Build runs on Gradle 9.x with no deprecation warnings that block Gradle 10
- Target Java version declared via a **toolchain**, so a later change flips one number in one place
- Every bytecode-processing tool in the build sits at a version that supports class file version 69, verified by actually building on JDK 25 in a throwaway run — even though the committed toolchain stays at 17
- Lombok's version controlled by this repo, not inherited from `spring-boot-dependencies`
- Three dead dependencies gone (`jaxb-api`, `reactor-spring`, `jsr250-api`)
- Shippable as 3.6.0 with zero source changes and zero public API change

**Non-Goals:**
- Changing the Java baseline — the toolchain lands pinned at 17 (`adopt-java-25-baseline` flips it)
- Changing the Spring Boot version (`migrate-spring-boot-4`)
- Bumping `openapi-generator` 6.6.0 → 7.x — it regenerates 377 consumer-visible classes and belongs on the 4.x line (but see **D1**, which can force it)
- Adopting Java 17+ language features, `module-info.java`, or any source-level modernization
- CI JDK matrix changes — CI stays on JDK 17 here

## Decisions

### D1 — Gradle 9 and `openapi-generator` 6.6.0 are interlocked; verify before committing to the split

**Decision**: Before any other work, verify that `org.openapitools:openapi-generator-gradle-plugin:6.6.0` runs under Gradle 9.x. Treat the result as a fork in the plan:

- **If it works** → proceed as proposed. Generator bump stays out of this change and lands in `adopt-java-25-baseline`.
- **If it does not work** → choose one, do not improvise:
  - **(a)** Pull the generator bump forward into this change, and take on the 377-class generated diff review here. Still shippable as 3.6.0, but the diff must be reviewed against the public API surface before release.
  - **(b)** Hold Gradle at 8.14.5 in this change, ship only the processor/tooling bumps and dead-dependency removals as 3.6.0, and move Gradle 9 into `adopt-java-25-baseline` where the generator bump already lives.

**Rationale**: `openapi-generator-gradle-plugin` 6.6.0 is a 2023 release, and Gradle 9 removed APIs that plugins of that era commonly use. `buildSrc/build.gradle` already carries a hand-written dependency override block because "owasp-dependencycheck 12.2.2 requires newer versions of several libraries than openapi-generator 6.6.0" — evidence that this plugin is already the awkward one in the build.

This is the single decision most likely to reshape the change. It is cheap to answer (one wrapper bump, one `./gradlew :symphony-bdk-core:openApiGenerate`) and expensive to discover late, so it is task 1.1.

**Alternative considered**: assume it works and find out mid-change. Rejected — the fallback branches lead to materially different scopes and release plans, and picking one under time pressure is how the generated-code diff ships unreviewed.

---

### D2 — Java toolchain, pinned at 17 in this change

**Decision**: Replace `sourceCompatibility = JavaVersion.VERSION_17` in `bdk.java-common-conventions.gradle` with:

```groovy
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
```

**Rationale**: Two reasons, one forced and one strategic.

Forced: Gradle 9 removed the project-level `sourceCompatibility` convention that the current line relies on, so this file has to change regardless.

Strategic: a toolchain is what makes `adopt-java-25-baseline` a one-line change instead of a hunt. It also decouples "which JDK runs Gradle" from "which JDK compiles the code", which is what lets us test-build on JDK 25 during *this* change without committing to it.

**Note on `-parameters`**: the existing `options.compilerArgs << '-parameters'` stays. It matters to Spring's constructor binding and to Jackson parameter-name resolution, and is easy to lose in a conventions rewrite.

---

### D3 — Replace `mockserver-netty`, don't bump it

**Decision**: Replace `org.mock-server:mockserver-netty:5.15.0` with WireMock or OkHttp `MockWebServer` across the 4 test files that use it, rather than chasing a newer MockServer release.

**Rationale**: MockServer 5.15.0 is stale, and it embeds Netty — which Spring Boot 4 moves from 4.1 to 4.2. That means it is a version-conflict source in *two* future changes, not one. The BOM already hand-pins Netty above Spring Boot's choice for CVE reasons, so this build has a history of fighting Netty resolution.

4 test files is a small enough surface that replacing the dependency outright is cheaper than carrying it through two migrations.

**Alternative considered**: bump MockServer and keep it. Viable if a current release exists that tracks Netty 4.2 — worth a look before committing, but the default is replacement.

---

### D4 — BOM constraint removals get their own commit

**Decision**: The removal of `reactor-spring` and the addition of the explicit Lombok constraint land as a separate, self-contained commit with the reasoning in the message — not folded into the Gradle 9 commit.

**Rationale**: A BOM constraint is a promise to consumers. Any consumer who imports `symphony-bdk-bom` and does not declare `reactor-spring` themselves gets a different resolved version — or none — after this change. Nothing fails at build time; something moves at runtime. That class of change deserves a reviewable diff of its own rather than being three lines inside a hundred-line build modernization.

The same discipline applies (and is repeated) in `migrate-spring-boot-4`, which deletes the Netty and Tomcat CVE overrides.

---

### D5 — MapStruct's generated output is diffed, not assumed

**Decision**: After the `mapstruct-processor` bump, diff the generated implementation of `UserDetailMapper` (the tree's only mapper) before and after.

**Rationale**: MapStruct 1.4 → 1.6 spans several years of null-handling and default-value behaviour changes. `UserDetailMapper` maps into `symphony-bdk-core`'s user service surface, so a silent change in how unmapped or null fields are treated is a behaviour change in a shipped 3.6.0 — exactly the kind of thing a "build-only" change is assumed not to contain. One mapper makes this a five-minute check.

## Risks / Trade-offs

**[Risk — highest] `openapi-generator` 6.6.0 is incompatible with Gradle 9, collapsing the clean scope split**
→ *Mitigation*: D1 makes this task 1.1 with two pre-agreed fallbacks. The change is explicitly allowed to shrink (option b) rather than silently absorb the generator bump.

**[Risk] Lombok pinned explicitly now diverges from `spring-boot-dependencies` later**
→ *Mitigation*: this is the intended trade-off, not an accident. Pinning means a Spring Boot bump can no longer silently move Lombok — which is the point, since Lombok is used in 13 modules and lags JDK releases. Cost is one more version to watch; `dependencyUpdates` already covers it.

**[Risk] Gradle 9 deprecation cleanup is wider than the 5 known call sites**
→ *Mitigation*: run with `--warning-mode all` and treat the output as the real task list. The known surface (3 `buildDir`, 2 `tasks.create`, 1 `sourceCompatibility`) is what static grep finds; configuration-time deprecations in `buildSrc` plugins won't show up that way.

**[Trade-off] Shipping this as 3.6.0 means the 3.x line takes a build change it doesn't strictly need**
→ Accepted. The alternative — doing it only on `next` — means the 3.x maintenance line stays on Gradle 8 indefinitely while receiving CVE backports, and every backport from `next` has to be translated across a build-system gap. Given that 3.x becomes a long-lived line under the Java 25 baseline decision, keeping the two builds structurally identical is worth more than avoiding one minor release.

## Migration Plan

Consumer-facing: none required. 3.6.0 is a drop-in replacement for 3.5.x with two exceptions to note in release notes:

1. `org.projectreactor:reactor-spring` is no longer constrained by `symphony-bdk-bom`. Consumers who relied on it must declare a version themselves. (Expected impact: zero — the artifact was last released in 2017.)
2. Lombok is now constrained by `symphony-bdk-bom` rather than inherited from `spring-boot-dependencies`. Consumers who import both platforms will see `symphony-bdk-bom`'s constraint participate in resolution.

Contributor-facing: Gradle 9 requires a JDK that Gradle 9 supports for the daemon. `./gradlew` handles the distribution; the toolchain handles compilation. Document the minimum daemon JDK in `CONTRIBUTING`/`CLAUDE.md` once the exact Gradle 9.x patch is chosen.

Branch: lands on `main`. `next` is cut from or rebased onto `main` after merge — see `migrate-spring-boot-4`.

## Open Questions

- **Which exact Gradle 9.x patch?** Pick the latest that supports the JDK CI runs, and confirm it supports toolchain provisioning for JDK 25 (needed by `adopt-java-25-baseline`, not by this change).
- **Does a current MockServer release track Netty 4.2?** If yes, D3's default (replace) could become a bump. Worth ten minutes before rewriting 4 test files.
- **Is `jacoco` coverage output stable across the toolVersion bump?** Every module has a `jacocoTestCoverageVerification` rule with a hard minimum (0.8–0.9). A change in how JaCoCo counts lines — not a change in test quality — could fail the build. If thresholds need adjusting, adjust them in a separate commit with the JaCoCo version named in the message, so it is never confused with a real coverage regression.
