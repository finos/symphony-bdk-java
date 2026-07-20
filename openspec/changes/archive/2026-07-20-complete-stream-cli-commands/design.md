## Context

`symphony-bdk-cli` follows a strict container/leaf command structure (`ContainerCommand` for noun groups like `bdk stream`, `BaseCommand` for verb leaves) with one class per verb, JSON output via `emit()`, and exit codes mapped centrally by `BdkCliExecutionExceptionHandler`. `StreamService` already implements every operation this change exposes; no `symphony-bdk-core` changes are needed. The existing `stream` group (`get`, `list`, `members`) only covers reads.

## Goals / Non-Goals

**Goals:**
- Add one leaf command per targeted `StreamService` write/read-detail operation, following the existing `stream get`/`list`/`members` conventions exactly (positional params for required ids, `@Option` for optional attributes, `emit()` for output, `NotFoundException` for null lookups).
- Keep each command a thin pass-through to `StreamService` — no new business logic in the CLI layer.
- Cover room lifecycle (create, update, get full detail, activate, deactivate) and membership/roles (create IM/MIM, add/remove member, promote/demote owner).

**Non-Goals:**
- Admin (`*Admin`) endpoints, enterprise-wide stream listing, room/stream search, content sharing, and IM attribute updates are explicitly out of scope (see proposal "Out of scope").
- No changes to `symphony-bdk-core` or `StreamService`.
- No interactive/multi-step flows (e.g. no confirmation prompts) — the CLI stays a one-shot, scriptable tool consistent with its stated design.

## Decisions

- **Command naming**: use hyphenated verb-noun leaf names (`create-room`, `update-room`, `get-room`, `activate-room`, `deactivate-room`, `create-im`, `add-member`, `remove-member`, `promote-owner`, `demote-owner`) rather than nested sub-groups (e.g. no `stream room create`). Rationale: matches the flat leaf style already used elsewhere in the CLI (e.g. `user search`, `message send`) and keeps `bdk stream --help` a single flat list, avoiding an extra container layer for only a handful of room-specific verbs.
- **Room attributes as repeatable options, not a request-body file**: `create-room`/`update-room` expose `V3RoomAttributes` fields as individual `@Option`s (`--description`, `--public`/`--private`, `--discoverable`, `--members-can-invite`, `--read-only`, `--view-history`, `--cross-pod`). Rationale: consistent with how every other CLI command surfaces parameters; avoids introducing a new "JSON body from file" input mechanism for this one command family. Boolean options are tri-state (unset = don't send / use API default) — implemented by using boxed `Boolean` fields (not primitive `boolean`) left `null` unless the flag is passed, so `update-room` only sends fields the user explicitly set. Picocli supports this via `@Option(names = "--public", arity = "0..1")`-style boolean options or negatable option pairs.
- **`update-room` semantics**: since `V3RoomAttributes` has no partial-update/PATCH semantics at the API level (`updateRoom` replaces the full attributes object), the command first calls `getRoomInfo(roomId)` to seed the current attributes, applies only the options the user explicitly passed on top, then calls `updateRoom`. Rationale: matches user expectation of "update just this field" rather than requiring every field to be re-specified on every update.
- **`create-im` takes 1..N positional user ids**: `bdk stream create-im <userId>...` maps directly to `StreamService.create(List<Long>)` (bot included, IM if 1 id, MIM if ≥2). The admin-exclusive variant (`createInstantMessageAdmin`) is intentionally not exposed (matches non-goals).
- **Membership/role commands take `<roomId> <userId>` positionals** (not options), mirroring the existing `stream members <streamId>` positional style and `StreamService`'s own `(userId, roomId)` parameter order reversed for CLI readability (`<roomId>` first, consistent with all other stream commands taking the room/stream id as the first positional).
- **`get-room` vs existing `get`**: kept as a separate command rather than replacing `stream get` because `getStream` (generic, `V2StreamAttributes`) and `getRoomInfo` (room-specific, `V3RoomDetail` with system info) return different shapes and `getStream` also works for non-room streams (IMs/MIMs). Renaming or merging would be a breaking change to `stream get`.
- **Activate/deactivate as separate verbs** rather than a single `set-active --active=true|false` command: mirrors natural CLI phrasing and avoids a boolean-flag command whose name doesn't convey direction.

## Risks / Trade-offs

- [Risk] `update-room`'s "fetch-then-merge" approach makes two API calls per update and has a (small) TOCTOU window between `getRoomInfo` and `updateRoom` if the room is concurrently modified. → Mitigation: acceptable for a one-shot CLI tool; document the behavior in the command's `--help` description so scripted callers are aware.
- [Risk] Boolean tri-state options add minor complexity vs. plain `boolean` fields used elsewhere in the CLI (e.g. `UserSearchCommand`'s `--local`). → Mitigation: scope this pattern only to `create-room`/`update-room`; other new commands (membership, roles, IM creation, activate/deactivate) have no optional booleans and keep the simple style.
- [Risk] Ten new leaf classes increase `StreamCommand`'s subcommand list significantly, which could clutter `bdk stream --help`. → Mitigation: picocli groups/sorts subcommands alphabetically by default and each has a one-line description; no further grouping needed given the CLI's existing flat style.

## Open Questions

None — scope and conventions are fully determined by the existing CLI patterns and `StreamService`'s current API surface.
