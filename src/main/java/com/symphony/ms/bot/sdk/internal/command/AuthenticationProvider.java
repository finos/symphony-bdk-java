package com.symphony.ms.bot.sdk.internal.command;

import com.symphony.ms.bot.sdk.internal.command.model.AuthenticationContext;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * AuthenticationProvider interface. Offers authentication methods for
 * {@link AuthenticatedCommandHandler}
 *
 * @author Marcus Secato
 *
 */
public interface AuthenticationProvider {

  /**
   * Abstracts the underlying authentication mechanism by returning an {@link AuthenticationContext}
   * object.
   *
   * @param userId the Symphony userId
   * @return the authentication context
   */
  AuthenticationContext getAuthenticationContext(Long userId);

  /**
   * Handles unauthenticated user
   *
   * @param command
   * @param commandResponse
   */
  void handleUnauthenticated(
      BotCommand command, SymphonyMessage commandResponse);

}
