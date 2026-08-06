package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream promote-owner <roomId> <userId>}. */
@Command(
    name = "promote-owner",
    description = "Promote a room member to owner.")
public class StreamPromoteOwnerCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Parameters(index = "1", paramLabel = "<userId>", description = "User id to promote.")
  Long userId;

  @Override
  public Integer call() throws Exception {
    bdk().streams().promoteUserToRoomOwner(userId, roomId);
    return emit(RoomMembershipResult.of(roomId, userId, "promoted"));
  }
}
