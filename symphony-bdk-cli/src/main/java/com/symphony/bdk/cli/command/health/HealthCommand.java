package com.symphony.bdk.cli.command.health;

import com.symphony.bdk.cli.command.ContainerCommand;

import picocli.CommandLine.Command;

/** {@code bdk health} — check connectivity to Symphony components. */
@Command(
    name = "health",
    description = "Check the health of the bot's connected Symphony components.",
    subcommands = {
        HealthCheckCommand.class
    })
public class HealthCommand extends ContainerCommand {
}
