package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream remove-member <roomId> <userId>}. */
@Command(
    name = "remove-member",
    description = "Remove a member from a room.")
public class StreamRemoveMemberCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Parameters(index = "1", paramLabel = "<userId>", description = "User id to remove.")
  Long userId;

  @Override
  public Integer call() throws Exception {
    bdk().streams().removeMemberFromRoom(userId, roomId);
    return emit(RoomMembershipResult.of(roomId, userId, "removed"));
  }
}
