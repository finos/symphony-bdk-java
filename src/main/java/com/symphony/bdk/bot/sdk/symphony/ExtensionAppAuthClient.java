package com.symphony.bdk.bot.sdk.symphony;

import com.symphony.bdk.bot.sdk.symphony.exception.SymphonyClientException;
import com.symphony.bdk.bot.sdk.symphony.model.AuthenticateResponse;

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
   * @param appId the application ID
   * @return authenticate response
   * @throws SymphonyClientException if communication with Symphony fails
   */
  AuthenticateResponse appAuthenticate(String appId) throws SymphonyClientException;

  /**
   * Validates tokens as part of extension app authentication process
   *
   * @param appToken the app token
   * @param symphonyToken the symphony token
   * @return true if tokens are valid, false otherwise
   */
  boolean validateTokens(String appToken, String symphonyToken);

  /**
   * Verifies the given JWT
   *
   * @param jwt the user JWT
   * @return userId the user ID
   */
  Long verifyJWT(String jwt);

}
