package com.symphony.bdk.cli.command.user;

import com.symphony.bdk.cli.command.ContainerCommand;

import picocli.CommandLine.Command;

/** {@code bdk user} — look up and search users. */
@Command(
    name = "user",
    description = "Look up and search users.",
    subcommands = {
        UserGetCommand.class,
        UserSearchCommand.class
    })
public class UserCommand extends ContainerCommand {
}
