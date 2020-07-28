package com.symphony.bdk.bot.sdk.command.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds authentication details used to communicate with external system.
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class AuthenticationContext {

  private String authToken;

  private String authScheme;

  public boolean isAuthenticated() {
    return authToken != null;
  }

}
