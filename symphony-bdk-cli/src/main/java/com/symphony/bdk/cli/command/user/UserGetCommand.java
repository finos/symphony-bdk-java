package com.symphony.bdk.cli.command.user;

import com.symphony.bdk.cli.command.BaseCommand;
import com.symphony.bdk.cli.internal.NotFoundException;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.model.UserV2;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * {@code bdk user get <emailOrId>} — detects whether the argument is a numeric user id or an email
 * address and looks the user up accordingly.
 */
@Command(
    name = "get",
    description = "Get a user by numeric id or email address.")
public class UserGetCommand extends BaseCommand {

  private static final Pattern NUMERIC = Pattern.compile("\\d+");
  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  @Parameters(index = "0", paramLabel = "<emailOrId>", description = "Numeric user id or email address.")
  String identifier;

  @Override
  public Integer call() throws Exception {
    // detect the lookup mode before authenticating, so a malformed argument is a pure usage error
    final boolean byId = NUMERIC.matcher(identifier).matches();
    final boolean byEmail = !byId && EMAIL.matcher(identifier).matches();
    if (!byId && !byEmail) {
      throw new CommandLine.ParameterException(spec.commandLine(),
          "'" + identifier + "' is neither a numeric user id nor an email address");
    }

    final UserService users = bdk().users();
    final List<UserV2> matches = byId
        ? users.listUsersByIds(Collections.singletonList(Long.parseLong(identifier)))
        : users.listUsersByEmails(Collections.singletonList(identifier));

    if (matches == null || matches.isEmpty()) {
      throw NotFoundException.of("user", identifier);
    }
    return emit(matches.get(0));
  }
}
