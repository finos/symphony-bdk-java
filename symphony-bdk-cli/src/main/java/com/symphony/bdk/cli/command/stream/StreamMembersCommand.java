package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream members <streamId>}. */
@Command(
    name = "members",
    description = "List the members of a stream.")
public class StreamMembersCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<streamId>", description = "Stream id.")
  String streamId;

  @Override
  public Integer call() throws Exception {
    return emit(bdk().streams().listStreamMembers(streamId));
  }
}
