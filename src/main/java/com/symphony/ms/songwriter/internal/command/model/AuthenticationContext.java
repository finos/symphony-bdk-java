package com.symphony.ms.songwriter.internal.command.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthenticationContext {

  private String authToken;

  private String authScheme;

  public boolean isAuthenticated() {
    return authToken != null;
  }

}
