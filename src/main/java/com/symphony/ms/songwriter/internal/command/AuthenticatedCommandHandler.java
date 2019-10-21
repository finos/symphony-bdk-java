package com.symphony.ms.songwriter.internal.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.songwriter.internal.command.model.AuthenticationContext;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public abstract class AuthenticatedCommandHandler extends CommandHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatedCommandHandler.class);

  protected AuthenticationProvider authenticationProvider;

  @Override
  public void handle(BotCommand command, SymphonyMessage commandResponse) {
    AuthenticationContext authContext = authenticationProvider
        .getAuthenticationContext(command.getUserId());

    if (authContext != null && authContext.isAuthenticated()) {
      LOGGER.debug("User authenticated");
      handle(command, commandResponse, authContext);
    } else {
      LOGGER.info("Not authenticated. Deferring to AuthenticationProvider");
      authenticationProvider.handleUnauthenticated(command, commandResponse);
    }
  }

  public abstract void handle(BotCommand command,
      SymphonyMessage commandResponse,
      AuthenticationContext authenticationContext);

  public void setAuthenticationProvider(
      AuthenticationProvider authenticationProvider) {
    this.authenticationProvider = authenticationProvider;
  }

}
