## Context

BDK 4.x changes two independent platform axes: the framework (Spring Boot 3.5 → 4.x) and the JDK (Java 17 → 25). Each has its own failure modes, and they are not the same kind of failure — framework breakage is mostly compile-time and package-move shaped, JDK breakage is mostly bytecode-tooling and agent-attach shaped.

Doing them in one step means every failure has two candidate causes and the build is never green in between. Splitting them requires picking an order, and the order is not arbitrary:

```
     ┌──────────────── intermediate state viability ─────────────────┐
     │                                                               │
 A)  SB4 first                          B)  JDK 25 first             │
                                                                     │
 Java 17 + Spring Boot 3.5              Java 17 + Spring Boot 3.5    │
        │                                      │                     │
        ▼                                      ▼                     │
 Java 17 + Spring Boot 4        ✅      Java 25 + Spring Boot 3.5  ❓ │
 (SB4 baselines Java 17 —               (SB 3.5 was not built with   │
  a supported, tested combo)             JDK 25 as a target)         │
        │                                      │                     │
        ▼                                      ▼                     │
 Java 25 + Spring Boot 4                Java 25 + Spring Boot 4      │
     └───────────────────────────────────────────────────────────────┘
```

Order A has a known-good checkpoint in the middle. Order B's checkpoint depends on whether Spring Boot 3.5.16 tolerates JDK 25 — plausible on a late patch release, but not something the migration plan should rest on. This change is the first half of order A.

`modernize-build-toolchain` is a prerequisite and lands on `main` first, so this change starts from Gradle 9 and current bytecode tooling. `next` is cut from — or rebased onto — `main` after that merge.

## Goals / Non-Goals

**Goals:**
- `spring-boot-dependencies:4.x` imported by `symphony-bdk-bom`, with all inherited platform moves (Jakarta EE 11, Netty 4.2, Tomcat 11, Micrometer 2) absorbed
- Both starters autoconfigure correctly under Spring Boot 4, verified by a test that boots a real application through the published starter rather than assembling a context by hand
- The Jackson major version question answered deliberately, with the classpath-splitting outcome explicitly rejected or explicitly accepted (D2)
- BOM cleaned of constraints that only existed to work around Spring Boot 3.5 (CVE overrides, Jakarta EE 10 API pins)
- Breaking changes that can only happen in a major taken now: `http-jersey2` rename, JSpecify, `BdkConfigParser` signatures
- Build and all tests green on **Java 17** + Spring Boot 4 before `adopt-java-25-baseline` starts
- A migration guide a 3.x consumer can actually follow

**Non-Goals:**
- Changing the Java baseline — that is `adopt-java-25-baseline`, and this change must not pre-empt it
- Virtual threads in `DatafeedService` / `DatafeedAsyncLauncherService`. Enabled by the Java 25 baseline, but a behaviour change with its own risk profile — a separate change after 4.0
- A `RestClient`-based HTTP implementation, or retiring either existing HTTP module. Spring Boot 4 makes `RestClient` the recommended sync client, which makes this worth revisiting — but as its own change, not as scope creep here
- `module-info.java` / full JPMS. `Automatic-Module-Name` stays as-is
- Adopting Spring Boot 4's API-versioning or `@HttpExchange` interface-client features

## Decisions

### D1 — Spring Boot 4 lands before the JDK move, on Java 17

**Decision**: This change keeps `bdk.java-common-conventions`' toolchain at `JavaLanguageVersion.of(17)`. The definition of done is "green on Java 17 + Spring Boot 4".

**Rationale**: See Context. Spring Boot 4 baselines Java 17, so this intermediate state is one the framework vendor supports and tests. The reverse order's intermediate state (Spring Boot 3.5 on JDK 25) is unverified, and if it turns out not to work the two changes collapse back into one — losing the whole benefit of the split at the worst possible moment.

A second reason: the Jackson decision (D2) is a framework question, not a JDK question. Resolving it while the JDK is held constant means a serialization difference can only have one cause.

**Alternative considered**: JDK 25 first, so that the riskiest single decision (Java 25 baseline) is validated earliest. Rejected because it front-loads risk onto an *unverified* intermediate configuration. If validating the Java 25 baseline early is the priority, the cheap way is a throwaway spike — which `modernize-build-toolchain` task 7.1 already schedules — not a reordering of shippable changes.

---

### D2 — The Jackson major version is gated on the Jersey provider question; a split classpath is a rejected outcome, not a fallback

**Decision**:
Jackson 3 JSON provider (`tools.jackson.jakarta:jackson-jakarta-rs-json-provider`) exists for Jersey 4.0.x (which is pinned by Spring Boot 4), and `jackson-annotations` 3.x retains core annotations under the legacy package name `com.fasterxml.jackson.annotation`.

Therefore, we **adopt Jackson 3**. We will migrate the 22 hand-written `databind`/`core` usages to `tools.jackson.*`, change `BdkConfigParser`'s 3 public `JsonNode` signatures, and rewrite `symphony-bdk-http-jersey`'s `JSON.java` / `RFC3339DateFormat.java` against the Jackson 3 provider.

**Rationale**: The tempting middle option is the dangerous one. `symphony-bdk-http-jersey`'s `JSON.java` is a `ContextResolver<ObjectMapper>` — a Jackson 2 construct. If Spring brings Jackson 3 while that module stays on Jackson 2, both are on the classpath and the two HTTP implementations serialize differently:

```
identical BDK call, different http module on the classpath
        │
        ├── http-jersey    → Jackson 2, our ContextResolver's ObjectMapper config
        └── http-webclient → Jackson 3, Spring Boot 4's configured mapper
                              │
                              └─▶ divergent date format, null inclusion,
                                  unknown-property handling
                                  → wire-format differences that depend on
                                    a runtimeOnly dependency choice
```

That is a bug class that reproduces only in consumer deployments and is nearly impossible to diagnose from a stack trace. Shipping 4.0 on Jackson 2 is unglamorous but honest and reversible; a split classpath is neither.

Note the migration is small in either direction: 22 files, and the 377 generated classes use only `com.fasterxml.jackson.annotation.*`, which Jackson 3 retains under the original package name. Confirm that against the actual `jackson-annotations` 3.x artifact rather than trusting it — if it is wrong, the generated-code cost jumps from zero to 377 files and this decision changes completely.

---

### D3 — The Netty and Tomcat CVE overrides are deleted, not carried forward

**Decision**: Remove `platform('io.netty:netty-bom:4.1.136.Final')` and the three `tomcat-embed-*:10.1.57` constraints from `symphony-bdk-bom`, along with their CVE comments.

**Rationale**: These exist solely to resolve above Spring Boot 3.5.16's pins. Spring Boot 4 brings Netty 4.2 and Tomcat 11, both of which win conflict resolution against these constraints anyway — so they become dead weight the moment the platform bumps.

Dead weight is not the real cost. The comments enumerate specific CVEs as "fixed in 10.1.56 / 10.1.57", which is true of the Tomcat 10 line and irrelevant to Tomcat 11. Anyone auditing the BOM later reads a confident, precise, obsolete claim. Stale security comments are worse than no comments.

After removal, re-run `./gradlew dependencyCheck` against the Spring Boot 4 platform and add whatever *new* overrides that run genuinely justifies — with fresh comments.

---

### D4 — JSR-305 → JSpecify happens here, in the major

**Decision**: Replace all 82 `javax.annotation.Nonnull` / `javax.annotation.Nullable` usages with JSpecify annotations, and drop `com.google.code.findbugs:jsr305` from the modules that only used it for nullability.

**Rationale**: JSR-305 was never a finished standard, is unmaintained, and squats on the `javax.annotation` package — which causes split-package problems for any consumer doing JPMS or strict dependency analysis. Spring Framework 7 standardises on JSpecify, so staying on JSR-305 means the BDK's nullability contract and Spring's stop being expressible in the same vocabulary.

It rides along here rather than getting its own change because it is mechanical, it is only possible in a major, and deferring it means carrying it to 5.x. The one caveat: JSpecify's defaulting semantics are not identical to JSR-305's — `@NullMarked` at package scope changes what an unannotated type means. Verify per-module rather than doing a blind find-and-replace.

Note `jsr305` is also pulled in by `bdk.java-codegen-conventions` for generated code; that usage is separate and stays until the generator is bumped.

---

### D5 — Starter correctness is verified by booting a real application, not by assembling a context

**Decision**: Add a smoke test per starter that boots a minimal `@SpringBootApplication` depending only on the published starter, and asserts the expected beans are present. These are new tests, added before the Spring Boot 4 bump so they can demonstrate they pass on 3.5 first.

**Rationale**: Spring Boot 4's autoconfiguration repackaging fails *quietly*. The observable symptom is not a compile error — it is autoconfiguration silently not applying, so a consumer's application starts with missing beans while the BDK's own tests pass, because those tests construct contexts explicitly (`AutoConfigurations` is used directly in 3 places) rather than going through the discovery mechanism that actually broke.

`symphony-bdk-test-spring-boot` makes this worse by re-exporting `spring-boot-starter-test` and `spring-boot-starter-web` as `api` dependencies. Consumers' *test* setups inherit our choices, so breakage there is invisible locally and lands directly on consumers.

Writing these tests against Spring Boot 3.5 first is what makes them trustworthy: a test written after the bump that passes tells you nothing about whether it would have caught the failure.

---

### D6 — Spring Boot 3 + BDK 4 is explicitly unsupported and loudly documented

**Decision**: State in the migration guide and in the BOM's documentation that BDK 4.x requires Spring Boot 4.x. Do not attempt cross-compatibility, conditional autoconfiguration, or reflective version detection.

**Rationale**: The combination will half-work. Core, config, and the HTTP modules have no Spring dependency at all, so a Spring Boot 3 consumer using only `symphony-bdk-core` would appear fine — right up to the point where a transitively-pulled Jakarta EE 11 or Netty 4.2 artifact meets Spring Boot 3's expectations. Partial success is worse than a clean failure, because it produces bug reports that look like BDK bugs.

The honest framing for consumers: the Spring Boot major and the BDK major move together.

---

### D7 — `symphony-bdk-http-jersey2` is renamed, and the old coordinate is not aliased

**Decision**: Rename the module and its published artifactId to `symphony-bdk-http-jersey`. Do not publish a relocation POM or an empty forwarding artifact.

**Rationale**: The module has been on Jersey 3.x since the Jakarta migration; `jersey2` is simply false, and it misleads people reading the dependency tree about which Jakarta generation they are on. An artifactId change is major-only, so the choice is now or 5.x.

No alias, because the migration guide already requires consumers to make coordinate-level changes (the Spring Boot major, the BOM version), so a single additional rename is not what makes the upgrade hard — and a forwarding artifact would need maintaining for the life of 4.x to serve a one-line edit.

The generator's `library = 'jersey2'` setting is a separate, unrelated string that belongs to `openapi-generator` and is addressed in `adopt-java-25-baseline` alongside the generator bump.

## Risks / Trade-offs

**[Risk — highest] Autoconfiguration silently stops applying and the existing test suite stays green**
→ *Mitigation*: D5. This is the one risk where the mitigation has to be built before the change, not after. If the smoke tests slip, the change is not done.

**[Risk] No Jackson 3 provider for Jersey, and schedule pressure makes the split classpath look reasonable**
→ *Mitigation*: D2 names the split classpath as a rejected outcome up front, precisely so it is not reconsidered as a compromise later. Shipping 4.0 on Jackson 2 is the pre-agreed answer.

**[Risk] `jackson-annotations` 3.x does not retain `com.fasterxml.jackson.annotation`, turning a 22-file migration into a 399-file one**
→ *Mitigation*: verify against the published artifact as task 1.3, before any Jackson work starts. If it is wrong, D2's decision is re-opened with a completely different cost basis.

**[Risk] Jakarta EE 11 moves `jakarta.validation` and `jakarta.ws.rs` in ways that break `symphony-bdk-http-api`'s abstraction**
→ *Mitigation*: the hard pins (`ws.rs-api:3.1.0`, `validation-api:3.0.2` — the latter in two places) must be removed early rather than at the end, so that any EE 11 incompatibility surfaces at the start of the change instead of after everything else is done.

**[Risk] Scope creep — a major version invites every deferred cleanup**
→ *Mitigation*: the Non-Goals list is deliberately specific about the tempting ones (virtual threads, `RestClient`, JPMS, retiring an HTTP module). Each is a real improvement and each would extend a long-lived branch that is already accumulating merge debt against `main`. They are 4.1 material.

**[Trade-off] Two breaking changes (JSpecify, the Jersey rename) ride along with the framework migration, widening the diff**
→ Accepted. Both are mechanical, both are major-only, and the alternative is a 5.x whose entire justification is cleanups that could have shipped here. The mitigation is commit hygiene: each rides in its own commit, so the framework migration stays reviewable in isolation.

**[Trade-off] Long-lived `next` branch diverging from `main`**
→ Accepted but bounded: `main` continues to take 3.x CVE fixes throughout. Rebase `next` on `main` at each section boundary in `tasks.md` rather than at the end.

## Migration Plan

Consumer-facing, to be written up as `docs/migration-4.x.md`:

1. **Move to Spring Boot 4 first.** BDK 4.x requires it (D6). A consumer still on Spring Boot 3 should stay on BDK 3.x.
2. **Java 17 is still sufficient after this change** — but not after `adopt-java-25-baseline`. The published 4.0.0 requires Java 25; this intermediate state is never released. The migration guide should state the Java 25 requirement, not Java 17.
3. **Rename the dependency**: `org.finos.symphony.bdk:symphony-bdk-http-jersey2` → `symphony-bdk-http-jersey` (D7).
4. **`BdkConfigParser`** — if Jackson 3 is adopted (D2), the 3 `JsonNode` signatures change package. Affects only consumers calling the config parser directly, which is unusual.
5. **Nullability annotations** change from JSR-305 to JSpecify. Source-compatible for consumers; visible to static analysis and IDE null checking.
6. **BOM constraints removed**: Netty, Tomcat, and the two Jakarta API pins are no longer constrained by `symphony-bdk-bom` (D3). Consumers who relied on the BDK BOM to pin those now inherit Spring Boot 4's choices, which is the correct behaviour.
7. **Test dependencies**: `symphony-bdk-test-spring-boot` re-exports Spring Boot's test starter, so consumers using it inherit Spring Boot 4's test stack — including any JUnit/Mockito version moves.

Documentation must be versioned before 4.0.0 ships, so that 3.x consumers reading `symphony-bdk-java.finos.org` are not shown 4.x instructions.

## Open Questions

- **Which Jersey version does Spring Boot 4.x pin?** Jakarta EE 11 includes Jakarta REST 4.0. If Spring Boot 4 stays on Jersey 3.1.x, `symphony-bdk-http-jersey` is largely untouched; if it moves to a Jersey line implementing REST 4.0, the invoker layer and `JSON.java` shift. Task 1.2.
- **Does a Jackson 3 JSON provider exist for that Jersey version?** The gate for D2. Task 1.4.
- **Do `@ConditionalOnMissingBean` / `@ConditionalOnProperty` / `@ConditionalOnBean` stay in `org.springframework.boot.autoconfigure.condition`?** 25 of the 28 autoconfigure references are these three annotations, so the answer sets the size of section 3. Task 1.1.
- **How long is the 3.x line supported?** Not a question this change answers, but it blocks the migration guide: the guide has to tell a consumer who cannot move to Java 25 what their options are, and "stay on 3.x" is only an answer if 3.x has a stated support window. Needs a decision before 4.0.0 ships.
- **Should `symphony-bdk-http-webclient`'s `guava:31.1-jre` constraint survive?** It is a 2022 pin justified by a comment about "version 28.2-android that is pulled contains security issues". Whatever pulled 28.2-android may no longer do so under Spring Boot 4. Worth re-deriving rather than carrying.
