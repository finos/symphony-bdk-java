## Why

The `bdk stream` CLI command group only exposes read operations (`get`, `list`, `members`), while `StreamService` also supports creating rooms and IMs, updating and (de)activating rooms, managing membership, and promoting/demoting owners. Bot operators currently have to write custom BDK code for these operations even though the BDK already implements them, making the CLI an incomplete substitute for scripting these day-to-day admin tasks.

## What Changes

- Add `bdk stream create-room <name> [options]` — creates a chatroom via `StreamService.create(V3RoomAttributes)`, exposing the common room attributes (description, public/private, discoverable, members-can-invite, read-only, view-history, cross-pod) as CLI options.
- Add `bdk stream update-room <roomId> [options]` — updates an existing room's attributes via `StreamService.updateRoom`, using the same option set as `create-room` (only supplied options are applied).
- Add `bdk stream get-room <roomId>` — fetches full room detail (attributes + system info) via `StreamService.getRoomInfo`, complementing the existing generic `stream get` (which returns `V2StreamAttributes`).
- Add `bdk stream activate-room <roomId>` / `bdk stream deactivate-room <roomId>` — toggles a room's active state via `StreamService.setRoomActive`.
- Add `bdk stream create-im <userId>...` — creates an IM or MIM (bot included) via `StreamService.create(List<Long>)`, accepting one or more user ids.
- Add `bdk stream add-member <roomId> <userId>` — adds a member to a room via `StreamService.addMemberToRoom`.
- Add `bdk stream remove-member <roomId> <userId>` — removes a member from a room via `StreamService.removeMemberFromRoom`.
- Add `bdk stream promote-owner <roomId> <userId>` — promotes a room member to owner via `StreamService.promoteUserToRoomOwner`.
- Add `bdk stream demote-owner <roomId> <userId>` — demotes a room owner to participant via `StreamService.demoteUserToRoomParticipant`.

Out of scope (left for a future change, not needed to make the CLI cover the common day-to-day room/membership workflows): admin-only endpoints (`*Admin` variants, enterprise-wide stream listing), IM-specific update (`updateInstantMessage`/`getInstantMessageInfo`, currently limited to pinning a message), room/stream search (`searchRooms`), and content sharing (`share`).

## Capabilities

### New Capabilities
- `stream-room-management-cli`: CLI commands to create, update, activate/deactivate, and fetch full detail for chatrooms.
- `stream-membership-cli`: CLI commands to create IMs/MIMs, add/remove room members, and promote/demote room owners.

### Modified Capabilities
(none — this change only adds new CLI subcommands; no existing CLI command's behavior changes)

## Impact

- Affected code: `symphony-bdk-cli/src/main/java/com/symphony/bdk/cli/command/stream/` (new command classes: `StreamCreateRoomCommand`, `StreamUpdateRoomCommand`, `StreamGetRoomCommand`, `StreamActivateRoomCommand`, `StreamDeactivateRoomCommand`, `StreamCreateImCommand`, `StreamAddMemberCommand`, `StreamRemoveMemberCommand`, `StreamPromoteOwnerCommand`, `StreamDemoteOwnerCommand`) and `StreamCommand.java` (register new subcommands).
- Tests: `symphony-bdk-cli/src/test/java/com/symphony/bdk/cli/CommandTest.java` (or a new dedicated test class) gains coverage for each new command, mocking `StreamService`.
- No changes to `symphony-bdk-core` — all new commands are thin wrappers over existing `StreamService` methods.
- No breaking changes; purely additive CLI surface.
