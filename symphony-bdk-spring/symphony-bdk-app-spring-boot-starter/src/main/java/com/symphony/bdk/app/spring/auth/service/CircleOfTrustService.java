package com.symphony.bdk.app.spring.auth.service;

import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.exception.BdkAppException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import com.symphony.bdk.core.auth.jwt.UserClaim;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service layer class used for Circle of Trust authentication.
 */
@Service
public class CircleOfTrustService {

  private static final SecureRandom secureRandom = new SecureRandom();
  private final ExtensionAppAuthenticator authenticator;

  public CircleOfTrustService(ExtensionAppAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  /**
   * Authenticate the extension application.
   * The extension application backend will generate an {@link AppToken} and send to Symphony Backend
   * for authenticating the application. The symphony token will be returned by Symphony Backend.
   * This pair of tokens will be stored in application backend.
   *
   * @return The generated {@link AppToken}.
   */
  public AppToken authenticate() {
    try {
      String token = this.generateToken();
      AppAuthSession authSession = authenticator.authenticateExtensionApp(token);
      return new AppToken(authSession.getAppToken());
    } catch (AuthUnauthorizedException e) {
      throw new BdkAppException(BdkAppErrorCode.AUTH_FAILURE, e);
    }
  }

  /**
   * Validate the pair of app and symphony tokens.
   *
   * @param tokenPair the token pair to be validated.
   */
  public void validateTokens(TokenPair tokenPair) {
    if (!authenticator.validateTokens(tokenPair.getAppToken(), tokenPair.getSymphonyToken())) {
      throw new BdkAppException(BdkAppErrorCode.INVALID_TOKEN);
    }
  }

  /**
   * Validate the JWT signed by the application frontend.
   *
   * @param jwt the signed JWT
   * @return The userId signed with the JWT.
   */
  public UserId validateJwt(String jwt) {
    try {
      UserClaim userClaim = authenticator.validateJwt(jwt);
      Long userId = userClaim.getId();
      return new UserId(userId);
    } catch (AuthInitializationException e) {
      throw new BdkAppException(BdkAppErrorCode.INVALID_JWT, e);
    }
  }

  private String generateToken() {
    byte[] randBytes = new byte[64];
    secureRandom.nextBytes(randBytes);
    return Hex.encodeHexString(randBytes);
  }

}
