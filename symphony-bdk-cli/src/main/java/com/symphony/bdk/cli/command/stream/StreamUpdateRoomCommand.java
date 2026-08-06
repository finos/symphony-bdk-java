package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.cli.internal.NotFoundException;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

/** {@code bdk stream update-room <roomId> [options]}. */
@Command(
    name = "update-room",
    description = "Update a chatroom's attributes. Fetches the room's current attributes first "
        + "and only overrides the ones explicitly passed.")
public class StreamUpdateRoomCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Mixin
  RoomAttributesOptions options;

  @Override
  public Integer call() throws Exception {
    final V3RoomDetail current = bdk().streams().getRoomInfo(roomId);
    if (current == null) {
      throw NotFoundException.of("room", roomId);
    }
    final V3RoomAttributes attributes = current.getRoomAttributes();
    options.applyTo(attributes);
    return emit(bdk().streams().updateRoom(roomId, attributes));
  }
}
