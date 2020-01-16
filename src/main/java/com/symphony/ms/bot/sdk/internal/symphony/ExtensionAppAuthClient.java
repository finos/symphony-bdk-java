package com.symphony.ms.bot.sdk.internal.symphony;

import com.symphony.ms.bot.sdk.internal.symphony.model.AuthenticateResponse;

/**
 * All support required to authenticate an extension app
 *
 * @author msecato
 *
 */
public interface ExtensionAppAuthClient {

  /**
   * Initializes the process of authenticating an extension app
   *
   * @param appId
   * @return authenticate response
   */
  AuthenticateResponse appAuthenticate(String appId);

  /**
   * Validates tokens as part of extension app authentication process
   *
   * @param appToken
   * @param symphonyToken
   * @return true if tokens are valid, false otherwise
   */
  boolean validateTokens(String appToken, String symphonyToken);

  /**
   * Verifies the given JWT
   *
   * @param jwt
   * @return userId
   */
  Long verifyJWT(String jwt);

}
