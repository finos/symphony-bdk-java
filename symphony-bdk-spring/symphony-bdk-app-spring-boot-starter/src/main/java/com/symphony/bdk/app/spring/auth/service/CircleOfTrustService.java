package com.symphony.bdk.app.spring.auth.service;

import com.symphony.bdk.app.spring.auth.model.AppToken;
import com.symphony.bdk.app.spring.exception.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.exception.BdkAppException;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import lombok.AllArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

/**
 * Service layer class used for Circle of Trust authentication.
 */
@Service
@AllArgsConstructor
public class CircleOfTrustService {

  private static final SecureRandom secureRandom = new SecureRandom();

  private final ExtensionAppAuthenticator authenticator;
  private final SymphonyBdkCoreProperties properties;

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
      final AppAuthSession authSession = authenticator.authenticateExtensionApp(this.generateToken());
      return new AppToken(authSession.getAppToken());
    } catch (AuthUnauthorizedException e) {
      throw new BdkAppException(BdkAppErrorCode.AUTH_FAILURE, e, this.properties.getApp().getAppId());
    }
  }

  /**
   * Validate the pair of app and symphony tokens.
   *
   * @param tokenPair the token pair to be validated.
   */
  public void validateTokens(TokenPair tokenPair) {
    if (!authenticator.validateTokens(tokenPair.appToken(), tokenPair.symphonyToken())) {
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
      final UserClaim userClaim = authenticator.validateJwt(jwt);
      return new UserId(userClaim.getId());
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
