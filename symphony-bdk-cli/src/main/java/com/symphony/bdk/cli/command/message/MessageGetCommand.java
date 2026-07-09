package com.symphony.bdk.cli.command.message;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.cli.internal.NotFoundException;
import com.symphony.bdk.gen.api.model.V4Message;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk message get <messageId>}. */
@Command(
    name = "get",
    description = "Retrieve a single message by id.")
public class MessageGetCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<messageId>", description = "Message id to retrieve.")
  String messageId;

  @Override
  public Integer call() throws Exception {
    final V4Message message = bdk().messages().getMessage(messageId);
    if (message == null) {
      throw NotFoundException.of("message", messageId);
    }
    return emit(message);
  }
}
