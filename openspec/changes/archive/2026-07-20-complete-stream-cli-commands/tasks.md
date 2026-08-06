## 1. Room lifecycle commands

- [x] 1.1 Implement `StreamCreateRoomCommand` (`bdk stream create-room <name> [--description] [--public|--private] [--discoverable] [--members-can-invite] [--read-only] [--view-history] [--cross-pod]`), building `V3RoomAttributes` from the supplied options and calling `StreamService.create(V3RoomAttributes)`.
- [x] 1.2 Implement `StreamUpdateRoomCommand` (`bdk stream update-room <roomId> [same options as create-room]`): fetch current attributes via `getRoomInfo`, apply only explicitly-passed options, call `StreamService.updateRoom`.
- [x] 1.3 Implement `StreamGetRoomCommand` (`bdk stream get-room <roomId>`) calling `StreamService.getRoomInfo`, throwing `NotFoundException` if the result is null.
- [x] 1.4 Implement `StreamActivateRoomCommand` (`bdk stream activate-room <roomId>`) calling `StreamService.setRoomActive(roomId, true)`.
- [x] 1.5 Implement `StreamDeactivateRoomCommand` (`bdk stream deactivate-room <roomId>`) calling `StreamService.setRoomActive(roomId, false)`.

## 2. Membership and role commands

- [x] 2.1 Implement `StreamCreateImCommand` (`bdk stream create-im <userId>...`, one or more positional user ids) calling `StreamService.create(List<Long>)`.
- [x] 2.2 Implement `StreamAddMemberCommand` (`bdk stream add-member <roomId> <userId>`) calling `StreamService.addMemberToRoom`.
- [x] 2.3 Implement `StreamRemoveMemberCommand` (`bdk stream remove-member <roomId> <userId>`) calling `StreamService.removeMemberFromRoom`.
- [x] 2.4 Implement `StreamPromoteOwnerCommand` (`bdk stream promote-owner <roomId> <userId>`) calling `StreamService.promoteUserToRoomOwner`.
- [x] 2.5 Implement `StreamDemoteOwnerCommand` (`bdk stream demote-owner <roomId> <userId>`) calling `StreamService.demoteUserToRoomParticipant`.

## 3. Wiring and registration

- [x] 3.1 Register all 10 new leaf commands as `subcommands` on `StreamCommand`.
- [x] 3.2 For the `void`-returning `StreamService` methods (`addMemberToRoom`, `removeMemberFromRoom`, `promoteUserToRoomOwner`, `demoteUserToRoomParticipant`), define and emit a small JSON confirmation payload (e.g. `{"roomId": ..., "userId": ..., "status": "..."}`) consistent across the four commands.

## 4. Tests

- [x] 4.1 Add tests for `create-room` and `update-room`, covering: name-only creation, creation/update with attribute options, update-not-found (404 → exit 3), and verifying only explicitly-passed options are merged into `updateRoom`'s payload.
- [x] 4.2 Add tests for `get-room` (found and not-found/404 cases), `activate-room`, and `deactivate-room`.
- [x] 4.3 Add tests for `create-im` (single user id → IM, multiple ids → MIM).
- [x] 4.4 Add tests for `add-member`, `remove-member`, `promote-owner`, `demote-owner`, including a not-found (404 → exit 3) case for at least one of them.

## 5. Documentation

- [x] 5.1 Update any CLI usage documentation/README listing available `bdk stream` subcommands to include the 10 new commands.
