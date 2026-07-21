# Stream-Room-Management-Cli Specification

### Requirement: Create a chatroom
The CLI SHALL provide a `bdk stream create-room <name>` command that creates a new chatroom via `StreamService.create(V3RoomAttributes)`, accepting `--description`, `--public`/`--private`, `--discoverable`, `--members-can-invite`, `--read-only`, `--view-history`, and `--cross-pod` options to populate the room attributes, and emits the resulting room detail as JSON on success.

#### Scenario: Create a room with only a name
- **WHEN** the user runs `bdk stream create-room "Trading Desk"` with no other options
- **THEN** the CLI calls `StreamService.create` with a `V3RoomAttributes` whose name is `"Trading Desk"` and all unspecified fields left unset, and emits the created room's `V3RoomDetail` as JSON with exit code 0

#### Scenario: Create a room with attribute options
- **WHEN** the user runs `bdk stream create-room "Trading Desk" --description "Desk chat" --public --discoverable`
- **THEN** the CLI populates the corresponding fields on `V3RoomAttributes` before calling `StreamService.create`, and emits the created room detail as JSON with exit code 0

### Requirement: Update a chatroom's attributes
The CLI SHALL provide a `bdk stream update-room <roomId> [options]` command that updates only the attributes explicitly passed by the user, leaving all other attributes at their current value, via `StreamService.updateRoom`.

#### Scenario: Update a single attribute
- **WHEN** the user runs `bdk stream update-room ROOM1 --description "New description"`
- **THEN** the CLI fetches the room's current attributes via `StreamService.getRoomInfo("ROOM1")`, overrides only the description field, calls `StreamService.updateRoom("ROOM1", ...)` with the merged attributes, and emits the updated room detail as JSON with exit code 0

#### Scenario: Update a room that does not exist
- **WHEN** the user runs `bdk stream update-room UNKNOWN --description "x"` and the underlying API responds with HTTP 404
- **THEN** the CLI exits with code 3

### Requirement: Get full room detail
The CLI SHALL provide a `bdk stream get-room <roomId>` command that fetches a room's full detail (attributes and system info) via `StreamService.getRoomInfo` and emits it as JSON.

#### Scenario: Get an existing room's detail
- **WHEN** the user runs `bdk stream get-room ROOM1` and the room exists
- **THEN** the CLI emits the `V3RoomDetail` returned by `StreamService.getRoomInfo("ROOM1")` as JSON with exit code 0

#### Scenario: Get a room that does not exist
- **WHEN** the user runs `bdk stream get-room UNKNOWN` and the underlying API responds with HTTP 404
- **THEN** the CLI exits with code 3

### Requirement: Activate and deactivate a chatroom
The CLI SHALL provide `bdk stream activate-room <roomId>` and `bdk stream deactivate-room <roomId>` commands that toggle a room's active state via `StreamService.setRoomActive`.

#### Scenario: Deactivate a room
- **WHEN** the user runs `bdk stream deactivate-room ROOM1`
- **THEN** the CLI calls `StreamService.setRoomActive("ROOM1", false)` and emits the resulting `RoomDetail` as JSON with exit code 0

#### Scenario: Activate a room
- **WHEN** the user runs `bdk stream activate-room ROOM1`
- **THEN** the CLI calls `StreamService.setRoomActive("ROOM1", true)` and emits the resulting `RoomDetail` as JSON with exit code 0
