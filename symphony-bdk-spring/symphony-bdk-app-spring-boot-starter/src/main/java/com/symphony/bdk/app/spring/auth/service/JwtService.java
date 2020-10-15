package com.symphony.bdk.app.spring.auth.service;

import com.symphony.bdk.app.spring.auth.model.BdkAppErrorCode;
import com.symphony.bdk.app.spring.auth.model.UserId;
import com.symphony.bdk.app.spring.auth.model.exception.AppAuthException;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.jwt.UserClaim;

import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private final ExtensionAppAuthenticator authenticator;

  public JwtService(ExtensionAppAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  public UserId validateJwt(String jwt) {
    try {
      UserClaim userClaim = authenticator.validateJwt(jwt);
      Long userId = userClaim.getId();
      UserId id = new UserId();
      id.setUserId(userId);
      return id;
    } catch (AuthInitializationException e) {
      throw new AppAuthException(e, BdkAppErrorCode.INVALID_JWT);
    }
  }
}
