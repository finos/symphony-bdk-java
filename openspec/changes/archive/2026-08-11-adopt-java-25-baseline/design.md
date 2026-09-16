## Context

This is the last of three changes making up the BDK 4.x platform migration:

```
modernize-build-toolchain        →  main (3.x), ships as 3.6.0
  Gradle 9 · toolchain · bytecode tooling · dead deps
  + records residual JDK 25 failures from a throwaway build
        │
        ▼
migrate-spring-boot-4            →  next
  Spring Boot 4 on Java 17  ← known-good intermediate state
        │
        ▼
adopt-java-25-baseline           →  next   ◀── this change
  Java 25 on Spring Boot 4  ← what 4.0.0 actually ships
```

By the time this change starts, the build is on Gradle 9 with current bytecode tooling and a green Spring Boot 4 test suite. The remaining JDK 25 work is therefore narrow and already partly inventoried: `modernize-build-toolchain` task 7.1 ran a throwaway JDK 25 build and recorded what failed.

The exception is the OpenAPI generator. It was deliberately excluded from `modernize-build-toolchain` — where it would have shipped a 377-class regeneration into a 3.x minor release — and it lands here, in the major, where consumer-visible generated-API changes are expected and documented.

## Goals / Non-Goals

**Goals:**
- Java 25 as the committed, single baseline for every published module
- CI, including the release workflow, running on JDK 25
- `openapi-generator` on 7.x with the `jersey3` library, custom templates rebased, and the full generated diff reviewed and documented
- Mockito working without relying on JVM self-attach
- The Java 25 requirement and the 3.x support window both written down before 4.0.0 ships
- No source-level use of post-17 language features — this change flips a baseline and nothing else

**Non-Goals:**
- Virtual threads in `DatafeedService` / `DatafeedAsyncLauncherService`. The most valuable thing the baseline unlocks, and precisely why it must not ride along: it is a concurrency behaviour change to the component most consumers depend on most heavily. Separate change, after 4.0.0
- Records, sealed types, pattern matching, or any API redesign using post-17 features
- A JDK matrix in CI. Java 25 is a floor, not an option
- Revisiting the baseline decision itself (D1)
- `module-info.java` / JPMS

## Decisions

### D1 — Java 25 is the baseline; the accepted cost is a long-lived 3.x line, and this change owns making that explicit

**Decision**: Baseline Java 25. The consequence — that consumers unable to run Java 25 cannot adopt BDK 4.x at all — is accepted. This change carries the obligation to record a **stated support window for the 3.x line** before 4.0.0 ships (task 7.4).

**Rationale**: The decision is a project-level one and is not re-opened here. What this change is responsible for is not leaving its consequence undocumented.

The consequence is concrete. Spring Boot 4 itself baselines Java 17, so the BDK's Java requirement is *stricter than the framework it sits on*. A consumer on Spring Boot 4 and Java 21 is a perfectly supported Spring application that cannot use BDK 4.x. Given the BDK's consumer profile, that means 3.x is not a rollback branch — it is a parallel production line receiving CVE backports for as long as those consumers exist.

That reframes an ordinary release into an ongoing commitment, and the commitment is much cheaper to scope now than to discover later. A stated window drives concrete decisions: whether `cve-scanning-gradle.yml` runs on both branches, whether the two builds are kept structurally identical to make backports cherry-pickable (which is why `modernize-build-toolchain` shipped on 3.x at all), and what the migration guide tells a consumer who cannot move.

**Alternative considered and rejected by the project**: baseline 21 with Java 25 in a test matrix, which would have kept 4.x reachable for most consumers while still allowing virtual threads. Recorded here for the benefit of whoever reads this in two years, not as a live option.

---

### D2 — The generator bump is gated on a full diff review, and the diff is a deliverable

**Decision**: Before the generator bump is merged, generate with 6.6.0 and 7.x into two trees, diff all 377 classes, and produce a written summary of every consumer-visible change. That summary becomes a section of the migration guide. If the diff contains changes nobody can explain, the bump does not merge until they are explained.

**Rationale**: `com.symphony.bdk.gen.api.model.*` and `com.symphony.bdk.gen.api.*` are public API. Consumers construct these models, read their getters, and catch their exceptions. A generator major bump changes them in ways nobody on the team chose: nullable wrapping strategy, fluent-setter shape, `equals`/`hashCode`/`toString`, enum representation, annotation sets, required-vs-optional constructor parameters.

The failure mode without this gate is not a build error — it is a consumer upgrading to 4.0.0 and finding that a model class they construct no longer compiles, with no mention of it in the migration guide. That is a worse experience than a documented breaking change, because it reads as carelessness rather than as a decision.

The diff is cheap: two generator runs and `diff -r`. Treating its *output* as a deliverable rather than a checkpoint is what makes it useful, because it forces someone to read all of it.

**Second, quieter risk in the same bump**: the three custom Mustache templates. `pojo.mustache` carries the project's Jakarta patch. If 7.x restructured the upstream template, a naive rebase can silently drop that patch — and since `useJakartaEe` may now be a first-class `configOption` in 7.x, the correct fix may be to **delete the customization** rather than port it. Each of the three templates should be re-justified against 7.x upstream, not merely made to apply.

---

### D3 — Mockito gets an explicit `-javaagent`, not a self-attach suppression

**Decision**: Add the Mockito agent explicitly to the test JVM arguments in `bdk.java-common-conventions`, resolved from the Mockito artifact. Do not silence the self-attach warning with `-XX:+EnableDynamicAgentLoading`.

**Rationale**: The warning is not noise — it is notice that JVM self-attach is being withdrawn. Suppressing it buys silence until the JDK release that denies self-attach outright, at which point every test module fails at once, on a JDK bump made for an unrelated reason.

Configuring the agent explicitly is a few lines in one convention plugin and is the configuration the JDK is steering toward. Doing it while deliberately touching the JDK is strictly cheaper than doing it under duress later.

---

### D4 — No language-feature adoption in this change

**Decision**: This change contains no source edits other than those forced by the generator regeneration. No records, no pattern matching, no `var` sweep, no sealed hierarchies, no virtual threads.

**Rationale**: The purpose is to make "does the project build and pass on JDK 25" answerable with a clean yes or no. Every voluntary source change added here becomes a candidate explanation for a failure and dilutes that signal.

There is also a review-economics argument. This change already contains a 377-class regeneration diff. Adding stylistic churn on top guarantees the regeneration diff — the part with real consumer impact — gets skimmed.

Virtual threads deserve their own note, because they are the strongest argument for having baselined 25 at all. `DatafeedService` and `DatafeedAsyncLauncherService` are exactly the blocking-loop shapes that virtual threads simplify, and the retry layer wraps them. That is a concurrency behaviour change to the single component most bots depend on, with its own failure modes around thread pinning and `ThreadLocal` usage in the auth session. It is a feature, and it belongs in a change that can be reverted without reverting the baseline.

---

### D5 — Release tag validation gains a branch guard

**Decision**: Add a check to `release.yml` that the release's target branch matches the major version in the tag: `v3.*` releases must target the `3.x` branch, `v4.*` must target `main`.

**Rationale**: The current regex `^v[0-9]+\.[0-9]+\.[0-9]+$` validates the tag's *shape* and nothing about which branch it points at. Once `main` is 4.x and `3.x` is a live maintenance branch, a `v3.6.1` release drafted against `main` would build 4.x code, publish it to Maven Central as 3.6.1, and there is no undo — Maven Central releases are immutable.

The guard is a handful of lines and closes a failure that is unrecoverable rather than merely inconvenient. It belongs here because this is the change that makes the two-branch topology real.

Note the branch cut itself is handled outside these OpenSpec changes; only the workflow guard is in scope.

## Risks / Trade-offs

**[Risk — highest] The generator diff contains unnoticed public API breakage**
→ *Mitigation*: D2 makes the reviewed diff a deliverable that feeds the migration guide, so "we bumped it and the build went green" is not a sufficient state to merge in.

**[Risk] Rebasing the custom Mustache templates silently drops the Jakarta patch in `pojo.mustache`**
→ *Mitigation*: re-justify each of the three templates against 7.x upstream rather than porting them. Verify the regenerated sources contain no `javax.*` imports, as an assertion rather than an inspection (task 3.7).

**[Risk] `jackson-databind-nullable` has no place in the 7.x + Jackson-decision combination**
→ *Mitigation*: `org.openapitools:jackson-databind-nullable:0.2.6` is constrained in the BOM and is a Jackson 2 artifact. Its fate depends on both 7.x's nullable strategy and the Jackson decision made in `migrate-spring-boot-4`. Resolve it explicitly (task 3.5) rather than letting it resolve transitively.

**[Risk] Lombok on JDK 25 fails, blocking 13 modules at once**
→ *Mitigation*: already pinned explicitly by `modernize-build-toolchain`, and its JDK 25 support was confirmed there (task 4.3) and again in the throwaway build (task 7.1/7.2). If it still fails, it fails early and loudly rather than being entangled with the Spring Boot bump — which was the reason for pinning it separately.

**[Risk] JaCoCo coverage thresholds shift on JDK 25 bytecode and fail the build for non-coverage reasons**
→ *Mitigation*: every module carries a `jacocoTestCoverageVerification` minimum of 0.8–0.9. If instrumentation of class file 69 counts differently, adjust thresholds in a dedicated commit naming the JDK, so it can never be mistaken for a real coverage regression. Same discipline as `modernize-build-toolchain` task 3.6.

**[Trade-off] Single-JDK CI means no evidence the BDK works on anything but 25**
→ Accepted, and it is the honest reflection of the baseline decision: there is no supported alternative to test. Adding 21 to a matrix would produce a green check for a configuration that is not supported, which is worse than no check.

**[Trade-off] The most valuable thing the baseline unlocks — virtual threads — is explicitly deferred**
→ Accepted. Shipping the baseline and the behaviour change together would mean a datafeed concurrency regression could only be fixed by reverting the baseline.

## Migration Plan

Consumer-facing, folded into the `docs/migration-4.x.md` written by `migrate-spring-boot-4`:

1. **Java 25 is required.** Not recommended — required. Consumers on 17 or 21 stay on BDK 3.x.
2. **The 3.x support window**, stated explicitly, so a consumer who cannot move to Java 25 knows what they have and for how long.
3. **Generated API changes** from the generator bump, enumerated from D2's reviewed diff, with before/after signatures for anything a consumer constructs or reads directly.
4. **Test infrastructure**: consumers using `symphony-bdk-test-jupiter` or `symphony-bdk-test-spring-boot` inherit the explicit Mockito agent configuration and must run tests on JDK 25.
5. **Build tooling**: consumers building against BDK 4.x need a JDK 25 toolchain; those on Gradle need a version supporting it.

Internal: after this change, `main` (once the branch cut lands) is Java 25 + Spring Boot 4, and `3.x` is Java 17 + Spring Boot 3.5. Backports across that gap are non-trivial — which is the reason `modernize-build-toolchain` was landed on 3.x rather than only on `next`.

## Open Questions

- **Does `openapi-generator` 7.x's `jersey3` library target the same Jersey version Spring Boot 4 pins?** `migrate-spring-boot-4` task 1.2 establishes the Jersey version; this change must confirm the generator's library targets it. A mismatch means the generated invoker layer compiles against a different Jakarta REST generation than the runtime provides.
- **Does 7.x change `invokerPackage` output enough to affect `symphony-bdk-http-api`?** The generator writes into `com.symphony.bdk.http.api` with `supportingFiles: "false"`, so BDK owns `ApiClient`, `ApiResponse`, `Pair`, and `TypeReference` by hand while the generated APIs call them. If 7.x changes the calling convention, that hand-written seam moves.
- **Is `dateLibrary: "java8"` still the right `configOption` in 7.x**, and does `sortParamsByRequiredFlag: "false"` still exist? Both are set today; either changing default behaviour would show up in the D2 diff as a broad signature change.
- **What is the 3.x support window?** Blocking for 4.0.0's release notes and migration guide (task 7.4). Not answerable inside this change, but it cannot ship without an answer.
