package com.symphony.bdk.cli.command;

import picocli.CommandLine.Command;

/**
 * {@code bdk whoami} — print the authenticated bot's session identity (a connectivity smoke test).
 */
@Command(
    name = "whoami",
    description = "Print the authenticated bot's session identity.")
public class WhoamiCommand extends BaseCommand {

  @Override
  public Integer call() throws Exception {
    return emit(bdk().sessions().getSession());
  }
}
