package com.symphony.bdk.cli.command.user;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.UserSearchQuery;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/** {@code bdk user search <query> [--local] [--limit N] [--skip N]}. */
@Command(
    name = "search",
    description = "Search users by a free-text query.")
public class UserSearchCommand extends BaseCommand {

  @Parameters(index = "0", paramLabel = "<query>", description = "Free-text search query.")
  String query;

  @Option(names = "--local", description = "Restrict the search to the local pod.")
  boolean local;

  @Option(names = "--limit", defaultValue = "50", description = "Max users to return (default: ${DEFAULT-VALUE}).")
  int limit;

  @Option(names = "--skip", defaultValue = "0", description = "Users to skip (default: ${DEFAULT-VALUE}).")
  int skip;

  @Override
  public Integer call() throws Exception {
    final UserSearchQuery searchQuery = new UserSearchQuery().query(query);
    return emit(bdk().users().searchUsers(searchQuery, local, new PaginationAttribute(skip, limit)));
  }
}
