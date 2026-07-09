package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.cli.internal.NotFoundException;
import com.symphony.bdk.gen.api.model.V2StreamAttributes;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/** {@code bdk stream get <streamId>}. */
@Command(
    name = "get",
    description = "Get a stream's details.")
public class StreamGetCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<streamId>", description = "Stream id.")
  String streamId;

  @Override
  public Integer call() throws Exception {
    final V2StreamAttributes stream = bdk().streams().getStream(streamId);
    if (stream == null) {
      throw NotFoundException.of("stream", streamId);
    }
    return emit(stream);
  }
}
