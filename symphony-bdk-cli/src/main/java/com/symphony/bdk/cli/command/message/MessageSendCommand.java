package com.symphony.bdk.cli.command.message;

import com.symphony.bdk.cli.command.BaseCommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** {@code bdk message send <streamId> --message "<text>"}. */
@Command(
    name = "send",
    description = "Send a message to a stream.")
public class MessageSendCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<streamId>", description = "Target stream id.")
  String streamId;

  @Option(
      names = {"-m", "--message"},
      required = true,
      paramLabel = "<text>",
      description = "Message body (MessageML or plain text).")
  String message;

  @Override
  public Integer call() throws Exception {
    return emit(bdk().messages().send(streamId, message));
  }
}
