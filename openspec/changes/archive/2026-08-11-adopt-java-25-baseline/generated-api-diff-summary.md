# Generated API diff: openapi-generator 6.6.0/jersey2 → 7.14.0/jersey3

Deliverable for task 3.2 (D2). Feeds migration guide task 7.2. Produced by generating all
5 `symphony-bdk-core` API specs plus the `symphony-group-extension` spec with both generator
versions into separate trees and running `diff -r` across all 377 generated classes.

## Method

1. Snapshot of the 6.6.0/jersey2 output (377 `.java` files) taken before touching any build file.
2. `openapi-generator-gradle-plugin` bumped to 7.14.0 (latest 7.x; the Gradle plugin has not
   published past 7.14.0 even though the core `openapi-generator` artifact has newer 7.x releases).
   `library` switched from `jersey2` to `jersey3` in both `bdk.java-codegen-conventions.gradle`
   and the `apisToGenerate` loop in `symphony-bdk-core/build.gradle`.
3. Output regenerated into a second tree (356 `.java` files) and diffed against the snapshot,
   ignoring import-line reordering and the `@Generated` annotation's timestamp/version fields.

## Findings

### 1. Fluent array/map builder methods were about to change casing — fixed

`pojo.mustache` (customized since 2021) calls `add{{nameInCamelCase}}Item` / `put{{nameInCamelCase}}Item`
for array- and map-typed properties. Between 6.6.0 and 7.14.0, upstream renamed this variable:
`nameInCamelCase` now means true camelCase (lowercase first letter), and a **new** variable
`nameInPascalCase` carries the old semantics the method names actually need. Left unported, every
generated `addXxxItem`/`putXxxItem` method would have been renamed to `addxxxItem`/`putxxxItem`
(lowercase first letter) — a silent, build-succeeding, binary-incompatible rename of every
collection-property builder method across the generated model surface.

**Fix applied**: `templates/pojo.mustache` now uses `{{nameInPascalCase}}` in both spots. Confirmed
by regenerating: zero method-name changes remain anywhere in the 356 common classes.

### 2. 21 dead "AllOf" companion classes are no longer generated

6.6.0 emitted an orphaned `XxxAllOf.java` class for every schema using `allOf` composition
(e.g. `MessageAllOf`, `CreateGroupAllOf`), even though nothing — not the corresponding model,
not any other generated class, not any hand-written BDK code — ever referenced them. 7.x's
improved `allOf` handling stops emitting these. Confirmed via repo-wide grep: no hand-written
source references any `*AllOf` symbol. **No consumer impact** — removing an unreferenced,
unreachable public class is not a behavioral change for any caller who could have compiled
against it.

Removed: `ConnectionRequestMessageAllOf`, `MessageAllOf`, `RoomCreatedMessageAllOf`,
`RoomDeactivatedMessageAllOf`, `RoomMemberDemotedFromOwnerMessageAllOf`,
`RoomMemberPromotedToOwnerMessageAllOf`, `RoomReactivatedMessageAllOf`, `SignalAllOf`,
`UserJoinedRoomMessageAllOf`, `UserLeftRoomMessageAllOf`, `V2MessageAllOf`, `V2PresenceAllOf`,
`V2RoomSearchCriteriaAllOf`, `V2UserPresenceAllOf` (symphony-bdk-core); `CreateGroupAllOf`,
`ProfileAllOf`, `ReadGroupAllOf`, `ReadMemberAllOf`, `TypeAllOf`, `UpdateGroupAllOf`
(symphony-group-extension).

### 3. `@Generated` annotation moves from `javax.annotation` to `jakarta.annotation`

`library=jersey3` unconditionally forces the generator's internal `useJakartaEe` flag (it is not
optional for this library — Jersey 3 *is* the Jakarta EE JAX-RS generation, confirmed by reading
`JavaClientCodegen`'s `libJersey3` branch), which retargets the `@Generated` annotation import.
This broke compilation until the module dependencies were updated (`javax.annotation:jsr250-api:1.0`
→ `jakarta.annotation:jakarta.annotation-api`, version managed by the Spring Boot 4 platform
already imported by `symphony-bdk-bom`) in both `bdk.java-codegen-conventions.gradle` and
`symphony-bdk-core/build.gradle`. Purely a build-time dependency fix; the annotation is
`RetentionPolicy.SOURCE` and carries no runtime or consumer-visible effect.

### 4. `@javax.annotation.Nullable` is unaffected, correctly

Our `pojo.mustache` hardcodes `@javax.annotation.Nullable` (JSR-305, from
`com.google.code.findbugs:jsr305`) on every nullable getter/setter. This annotation was **never**
renamed to the `jakarta` namespace — JSR-305 is unrelated to Jakarta EE/JAX-RS, unlike the
`@Generated` annotation above. Verified empirically: no `jakarta.annotation.Nullable` class exists
on any dependency's classpath, so had the template been "fixed" to follow `{{javaxPackage}}` here,
every generated model would fail to compile. The existing hardcoded `javax` reference is correct
and was left unchanged.

### 5. Everything else is byte-for-byte identical modulo import order and timestamps

After fix #1, only 2 of the 356 common classes had any further diff, and both are non-functional:
- `Profile.java` (symphony-group-extension): `@ApiModelProperty(example = ...)` array-example
  string formatting changed from `[\"Services\"]` to `[Services]` — a Javadoc/Swagger metadata
  string, not consumer-visible code.
- `PodCertificate.java`: differs only in the OpenAPI spec description/version comment header,
  caused by which of two API specs defining an identical `PodCertificate` schema happened to
  generate last in this run (`skipOverwrite`-driven, pre-existing non-determinism unrelated to
  the generator version).

## Template re-justification (task 3.6)

- **`pojo.mustache`**: kept, with the one-line `nameInPascalCase` fix above. Still required —
  upstream's own `libraries/jersey3/pojo.mustache` and base `Java/pojo.mustache` are structurally
  different from BDK's fluent/no-JsonNullable/no-`toUrlQueryString` model shape; there is no
  native 7.x equivalent to delete in favor of.
- **`api.mustache`**: kept unchanged. Regenerated output is identical to 6.6.0's aside from the
  `@Generated` annotation — BDK's `XxxWithHttpInfo`/`ApiXxxRequest` builder shape has no upstream
  equivalent to adopt instead.
- **`modelInnerEnum.mustache`**: kept unchanged. Regenerated output identical to 6.6.0's aside from
  the `@Generated` annotation.

## Config options (task 3.5)

- `dateLibrary: "java8"` — still a recognized value in 7.14.0; moot in practice since none of the
  5 BDK API specs currently define a date/time-typed property.
- `sortParamsByRequiredFlag: "false"` — still a recognized, same-meaning option in 7.14.0.
- `useJakartaEe` — not set explicitly; not applicable for `library=jersey3`, which forces it to
  `true` unconditionally regardless of the config option's value.
- `org.openapitools:jackson-databind-nullable:0.2.11` — kept. Not produced or consumed by the
  current generation either before or after the bump (no schema triggers the `JsonNullable`
  wrapper strategy); the dependency exists solely so `symphony-bdk-http-jersey`'s hand-written
  `JSON.java` can register `JsonNullableJackson3Module` defensively. Unaffected by this change.

## Invoker seam / Jersey version (tasks 3.8–3.11)

- `globalProperties.supportingFiles: "false"` continues to suppress every file the `jersey3`
  library would otherwise emit into `invokerPackage` (`JSON.java`, `ApiResponse.java`,
  `AbstractOpenApiSchema.java`) — confirmed empirically: the generated `com/symphony/bdk/http/api`
  directories are empty. BDK's hand-written `ApiClient`, `ApiResponse`, `Pair`, and `TypeReference`
  in `symphony-bdk-http-api` are untouched and did not need any change.
- Generated model/API classes never reference `jakarta.ws.rs`/`javax.ws.rs` types directly (that's
  confined to supportingFiles this build doesn't generate), so there is no version coupling between
  openapi-generator's internal "jersey3 library" Jersey pin (3.1.1, used only in its own unused
  scaffolding) and the actual runtime Jersey version (4.0.0, imported via `symphony-bdk-bom`'s
  `org.glassfish.jersey:jersey-bom:4.0.0`, matching `migrate-spring-boot-4` task 1.2).
- `skipOverwrite = true` continues to behave as before: it only matters when a target file already
  exists on disk from a previous run; a clean `build/generated/openapi` regenerates fully.
- Full `./gradlew build` (JDK 17, pre-Java-25-flip) is green with the bump applied — no hand-written
  call site anywhere in the repository needed a change (task 3.10 confirmed by the build passing
  outright, not just by inspection).

## Consumer-facing summary (for docs/migration-4.x.md, task 7.2)

- 21 unreferenced `*XxxAllOf*` generated classes no longer exist. If your code somehow referenced
  one directly (unlikely — nothing in BDK itself ever did), it will need to be removed.
- No other generated class in `com.symphony.bdk.gen.api` / `com.symphony.bdk.gen.api.model` (or the
  `symphony-group-extension` equivalents) changes shape, method signatures, `equals`/`hashCode`/
  `toString`, or annotations in any consumer-visible way.
