package com.symphony.ms.bot.sdk.internal.command;

import com.symphony.ms.bot.sdk.internal.command.model.AuthenticationContext;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Extends {@link CommandHandler} to simplify handling commands that require
 * authenticated communication with external systems. Interacts with an
 * implementation of {@link AuthenticationProvider} to completely separate
 * authentication logic from the business logic implemented by the
 * {@link CommandHandler}.
 *
 * @author Marcus Secato
 *
 */
public abstract class AuthenticatedCommandHandler extends CommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatedCommandHandler.class);

  protected AuthenticationProvider authenticationProvider;

  /**
   * Makes sure the Symphony user is authenticated to the external system
   * associated with the bot command before triggering the command handling. If
   * not authenticated, {@link AuthenticationProvider} to handle
   * unauthenticated user.
   */
  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    AuthenticationContext authContext = authenticationProvider
        .getAuthenticationContext(command.getMessageEvent().getUserId());

    if (authContext != null && authContext.isAuthenticated()) {
      LOGGER.debug("User authenticated");
      handle(command, commandResponse, authContext);
    } else {
      LOGGER.info("Not authenticated. Deferring to AuthenticationProvider");
      authenticationProvider.handleUnauthenticated(command, commandResponse);
    }
  }

  /**
   * Handles a command issued to the bot
   *
   * @param command
   * @param commandResponse
   * @param authenticationContext
   */
  public abstract void handle(BotCommand command,
      SymphonyMessage commandResponse,
      AuthenticationContext authenticationContext);

  public void setAuthenticationProvider(
      AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

}
