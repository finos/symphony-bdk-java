package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream add-member <roomId> <userId>}. */
@Command(
    name = "add-member",
    description = "Add a member to a room.")
public class StreamAddMemberCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Parameters(index = "1", paramLabel = "<userId>", description = "User id to add.")
  Long userId;

  @Override
  public Integer call() throws Exception {
    bdk().streams().addMemberToRoom(userId, roomId);
    return emit(RoomMembershipResult.of(roomId, userId, "added"));
  }
}
