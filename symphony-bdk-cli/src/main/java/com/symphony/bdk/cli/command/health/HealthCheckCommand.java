package com.symphony.bdk.cli.command.health;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;

/** {@code bdk health check}. */
@Command(
    name = "check",
    description = "Report the health status of the agent and connected components.")
public class HealthCheckCommand extends BaseCommand {

  @Override
  public Integer call() throws Exception {
    return emit(bdk().health().healthCheck());
  }
}
