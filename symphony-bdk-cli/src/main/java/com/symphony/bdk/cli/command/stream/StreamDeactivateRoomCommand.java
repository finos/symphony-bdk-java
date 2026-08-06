package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream deactivate-room <roomId>}. */
@Command(
    name = "deactivate-room",
    description = "Deactivate a chatroom.")
public class StreamDeactivateRoomCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<roomId>", description = "Room id.")
  String roomId;

  @Override
  public Integer call() throws Exception {
    return emit(bdk().streams().setRoomActive(roomId, false));
  }
}
