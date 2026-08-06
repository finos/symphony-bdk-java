package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream demote-owner <roomId> <userId>}. */
@Command(
    name = "demote-owner",
    description = "Demote a room owner to participant.")
public class StreamDemoteOwnerCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Parameters(index = "1", paramLabel = "<userId>", description = "User id to demote.")
  Long userId;

  @Override
  public Integer call() throws Exception {
    bdk().streams().demoteUserToRoomParticipant(userId, roomId);
    return emit(RoomMembershipResult.of(roomId, userId, "demoted"));
  }
}
