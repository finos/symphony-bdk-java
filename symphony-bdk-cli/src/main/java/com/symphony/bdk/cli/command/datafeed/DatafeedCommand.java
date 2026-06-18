package com.symphony.bdk.cli.command.datafeed;

import com.symphony.bdk.cli.command.ContainerCommand;

import picocli.CommandLine.Command;

/** {@code bdk datafeed} — stream real-time events. */
@Command(
    name = "datafeed",
    description = "Read the bot's real-time event feed.",
    subcommands = {
        DatafeedReadCommand.class
    })
public class DatafeedCommand extends ContainerCommand {
}
