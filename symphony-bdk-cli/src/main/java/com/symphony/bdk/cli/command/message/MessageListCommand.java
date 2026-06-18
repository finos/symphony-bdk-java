package com.symphony.bdk.cli.command.message;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.time.Duration;
import java.time.Instant;

/** {@code bdk message list <streamId> [--since <ts>] [--limit N] [--skip N]}. */
@Command(
    name = "list",
    description = "List messages in a stream.")
public class MessageListCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<streamId>", description = "Stream id to read from.")
  String streamId;

  @Option(
      names = "--since",
      paramLabel = "<ts>",
      description = "Lower time bound: epoch millis or ISO-8601 instant. Default: last 24h.")
  String since;

  @Option(names = "--limit", defaultValue = "50", description = "Max messages to return (default: ${DEFAULT-VALUE}).")
  int limit;

  @Option(names = "--skip", defaultValue = "0", description = "Messages to skip (default: ${DEFAULT-VALUE}).")
  int skip;

  @Override
  public Integer call() throws Exception {
    final Instant sinceInstant = parseSince(since);
    return emit(bdk().messages().listMessages(streamId, sinceInstant, new PaginationAttribute(skip, limit)));
  }

  private static Instant parseSince(String value) {
    if (value == null || value.isBlank()) {
      return Instant.now().minus(Duration.ofHours(24));
    }
    if (value.matches("\\d+")) {
      return Instant.ofEpochMilli(Long.parseLong(value));
    }
    return Instant.parse(value);
  }
}
