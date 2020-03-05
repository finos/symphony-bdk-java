package com.symphony.bot.sdk.command;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import com.symphony.bot.sdk.internal.command.AuthenticatedCommandHandler;
import com.symphony.bot.sdk.internal.command.config.CommandAuthenticationProvider;
import com.symphony.bot.sdk.internal.command.model.AuthenticationContext;
import com.symphony.bot.sdk.internal.command.model.BotCommand;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Sample code to demonstrate how to use {@link AuthenticatedCommandHandler} along with an
 * AuthenticationProvider that implements basic authentication.
 */
@CommandAuthenticationProvider(name = "BasicAuthenticationProvider")
public class LoginCommandHandler extends AuthenticatedCommandHandler {

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /login$")
        .asPredicate();
  }

  /**
   * Invoked when command matches
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse,
      AuthenticationContext authenticationContext) {

    commandResponse.setMessage("<b>User authenticated</b>. "
        + "Please add the following HTTP header to your requests:<br /><br />"
        + "<code>Authorization: "
        + authenticationContext.getAuthScheme() + " "
        + authenticationContext.getAuthToken() + "</code>");

  }

}
