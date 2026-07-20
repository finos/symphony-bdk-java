package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.List;

/** {@code bdk stream create-im <userId>...}. */
@Command(
    name = "create-im",
    description = "Create an IM (one user id) or MIM (several user ids), bot included.")
public class StreamCreateImCommand extends BaseCommand {

  @Parameters(index = "0..*", paramLabel = "<userId>", description = "User id(s) to include, other than the bot.")
  List<Long> userIds;

  @Override
  public Integer call() throws Exception {
    return emit(bdk().streams().create(userIds));
  }
}
