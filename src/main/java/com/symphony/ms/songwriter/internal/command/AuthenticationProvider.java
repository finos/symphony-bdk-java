package com.symphony.ms.songwriter.internal.command;

import com.symphony.ms.songwriter.internal.command.model.AuthenticationContext;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

/**
 * AuthenticationProvider interface. Offers authentication methods for
 * {@link AuthenticatedCommandHandler}
 *
 * @author Marcus Secato
 *
 */
public interface AuthenticationProvider {

  /**
   * Abstracts the underlying authentication mechanism by returning an
   * {@link AuthenticationContext} object.
   *
   * @param userId the Symphony userId
   * @return the authentication context
   */
  AuthenticationContext getAuthenticationContext(String userId);

  /**
   * Handles unauthenticated user
   *
   * @param command
   * @param commandResponse
   */
  void handleUnauthenticated(
      BotCommand command, SymphonyMessage commandResponse);

}
