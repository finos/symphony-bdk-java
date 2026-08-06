package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.cli.internal.NotFoundException;
import com.symphony.bdk.gen.api.model.V3RoomDetail;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream get-room <roomId>}. */
@Command(
    name = "get-room",
    description = "Get a chatroom's full detail (attributes and system info).")
public class StreamGetRoomCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Override
  public Integer call() throws Exception {
    final V3RoomDetail room = bdk().streams().getRoomInfo(roomId);
    if (room == null) {
      throw NotFoundException.of("room", roomId);
    }
    return emit(room);
  }
}
