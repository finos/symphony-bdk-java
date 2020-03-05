package com.symphony.bot.sdk.command.auth;

import java.util.Base64;

import com.symphony.bot.sdk.internal.command.AuthenticationProvider;
import com.symphony.bot.sdk.internal.command.model.AuthenticationContext;
import com.symphony.bot.sdk.internal.command.model.BotCommand;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Sample code. Implementation of {@link AuthenticationProvider} to offer basic authentication.
 */
public class BasicAuthenticationProvider implements AuthenticationProvider {

  private String username = "john.doe@symphony.com";
  private String password = "strongpass";

  @Override
  public AuthenticationContext getAuthenticationContext(Long userId) {
    AuthenticationContext authContext = new AuthenticationContext();
    authContext.setAuthScheme("Basic");
    authContext.setAuthToken(findCredentialsByUserId(userId));

    return authContext;
  }

  @Override
  public void handleUnauthenticated(BotCommand command,
      SymphonyMessage commandResponse) {
    commandResponse.setMessage("Sorry, you are not authorized to perform this"
        + " action using Basic Authentication");
  }

  // Just a simple example. Ideally, implement a service to handle credentials
  // retrieval.
  private String findCredentialsByUserId(Long userId) {
    String credential = username + ":" + password;
    return new String(Base64.getEncoder().encode(
        credential.getBytes()));
  }

}
