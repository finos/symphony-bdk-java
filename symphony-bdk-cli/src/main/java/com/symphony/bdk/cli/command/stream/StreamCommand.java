package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.ContainerCommand;

import picocli.CommandLine.Command;

/** {@code bdk stream} — inspect the bot's streams. */
@Command(
    name = "stream",
    description = "List streams, members and stream details.",
    subcommands = {
        StreamListCommand.class,
        StreamMembersCommand.class,
        StreamGetCommand.class
    })
public class StreamCommand extends ContainerCommand {
}
