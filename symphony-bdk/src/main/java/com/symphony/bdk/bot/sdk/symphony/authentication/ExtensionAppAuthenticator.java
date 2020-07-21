package com.symphony.bdk.bot.sdk.symphony.authentication;

import model.AppAuthResponse;
import model.UserInfo;

/**
 * Abstracts the underlying extension app authentication mechanism (either RSA
 * or certificate-based). Exposes the three steps required to complete Symphony's
 * circle of trust.
 *
 * @author Marcus Secato
 *
 */
public interface ExtensionAppAuthenticator {

  /**
   * Authenticates to Symphony backend
   *
   * @return authentication response containing the app token
   */
  AppAuthResponse appAuthenticate();

  /**
   * Checks if the given appToken and symphonyToken pair matches the one obtained
   * from Symphony backend
   *
   * @param appToken the application token
   * @param symphonyToken the symphony token
   * @return true if the tokens match, false otherwise
   */
  boolean validateTokens(String appToken, String symphonyToken);

  /**
   * Validates the given JWT against Symphony backend
   *
   * @param jwt the user JWT
   * @return the user info
   */
  UserInfo verifyJWT(String jwt);
}
