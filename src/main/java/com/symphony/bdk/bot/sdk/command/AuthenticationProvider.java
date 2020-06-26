package com.symphony.bdk.bot.sdk.command;

import com.symphony.bdk.bot.sdk.command.model.AuthenticationContext;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

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
   * @param command the command issued in chat room
   * @param commandResponse the response to be sent to chat room
   */
  void handleUnauthenticated(
      BotCommand command, SymphonyMessage commandResponse);

}
