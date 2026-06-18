package com.symphony.bdk.cli.command.message;

import com.symphony.bdk.cli.command.ContainerCommand;

import picocli.CommandLine.Command;

/** {@code bdk message} — send and read messages. */
@Command(
    name = "message",
    description = "Send and read messages.",
    subcommands = {
        MessageSendCommand.class,
        MessageGetCommand.class,
        MessageListCommand.class
    })
public class MessageCommand extends ContainerCommand {
}
