package com.symphony.bdk.cli.command.stream;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** {@code bdk stream list [--limit N] [--skip N]}. */
@Command(
    name = "list",
    description = "List the streams the bot is a member of.")
public class StreamListCommand extends BaseCommand {

  @Option(names = "--limit", defaultValue = "50", description = "Max streams to return (default: ${DEFAULT-VALUE}).")
  int limit;

  @Option(names = "--skip", defaultValue = "0", description = "Streams to skip (default: ${DEFAULT-VALUE}).")
  int skip;

  @Override
  public Integer call() throws Exception {
    return emit(bdk().streams().listStreams(null, new PaginationAttribute(skip, limit)));
  }
}
