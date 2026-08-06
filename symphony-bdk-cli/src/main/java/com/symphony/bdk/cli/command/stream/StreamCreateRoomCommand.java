package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

/** {@code bdk stream create-room <name> [options]}. */
@Command(
    name = "create-room",
    description = "Create a chatroom.")
public class StreamCreateRoomCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<name>", description = "Room name.")
  String name;

  @Mixin
  RoomAttributesOptions options;

  @Override
  public Integer call() throws Exception {
    final V3RoomAttributes attributes = new V3RoomAttributes().name(name);
    options.applyTo(attributes);
    return emit(bdk().streams().create(attributes));
  }
}
