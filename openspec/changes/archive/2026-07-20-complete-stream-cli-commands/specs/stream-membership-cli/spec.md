## ADDED Requirements

### Requirement: Create an IM or MIM
The CLI SHALL provide a `bdk stream create-im <userId>...` command that creates an instant message (one other user id) or multi-party instant message (multiple other user ids) including the bot, via `StreamService.create(List<Long>)`, accepting one or more positional user ids.

#### Scenario: Create an IM with a single user
- **WHEN** the user runs `bdk stream create-im 12345`
- **THEN** the CLI calls `StreamService.create(List.of(12345L))` and emits the resulting `Stream` as JSON with exit code 0

#### Scenario: Create a MIM with multiple users
- **WHEN** the user runs `bdk stream create-im 111 222 333`
- **THEN** the CLI calls `StreamService.create(List.of(111L, 222L, 333L))` and emits the resulting `Stream` as JSON with exit code 0

### Requirement: Add a member to a room
The CLI SHALL provide a `bdk stream add-member <roomId> <userId>` command that adds a user to a room via `StreamService.addMemberToRoom`.

#### Scenario: Add a member successfully
- **WHEN** the user runs `bdk stream add-member ROOM1 12345`
- **THEN** the CLI calls `StreamService.addMemberToRoom(12345L, "ROOM1")` and, on success, emits a JSON confirmation object with exit code 0

#### Scenario: Add a member to a non-existent room
- **WHEN** the user runs `bdk stream add-member UNKNOWN 12345` and the underlying API responds with HTTP 404
- **THEN** the CLI exits with code 3

### Requirement: Remove a member from a room
The CLI SHALL provide a `bdk stream remove-member <roomId> <userId>` command that removes a user from a room via `StreamService.removeMemberFromRoom`.

#### Scenario: Remove a member successfully
- **WHEN** the user runs `bdk stream remove-member ROOM1 12345`
- **THEN** the CLI calls `StreamService.removeMemberFromRoom(12345L, "ROOM1")` and, on success, emits a JSON confirmation object with exit code 0

### Requirement: Promote a room member to owner
The CLI SHALL provide a `bdk stream promote-owner <roomId> <userId>` command that promotes a room member to owner via `StreamService.promoteUserToRoomOwner`.

#### Scenario: Promote a member to owner
- **WHEN** the user runs `bdk stream promote-owner ROOM1 12345`
- **THEN** the CLI calls `StreamService.promoteUserToRoomOwner(12345L, "ROOM1")` and, on success, emits a JSON confirmation object with exit code 0

### Requirement: Demote a room owner to participant
The CLI SHALL provide a `bdk stream demote-owner <roomId> <userId>` command that demotes a room owner to participant via `StreamService.demoteUserToRoomParticipant`.

#### Scenario: Demote an owner to participant
- **WHEN** the user runs `bdk stream demote-owner ROOM1 12345`
- **THEN** the CLI calls `StreamService.demoteUserToRoomParticipant(12345L, "ROOM1")` and, on success, emits a JSON confirmation object with exit code 0
