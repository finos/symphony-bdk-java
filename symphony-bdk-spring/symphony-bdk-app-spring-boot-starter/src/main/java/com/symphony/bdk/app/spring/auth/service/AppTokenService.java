package com.symphony.bdk.app.spring.auth.service;

import com.symphony.bdk.app.spring.auth.model.TokenPair;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;

import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AppTokenService {

  private static final SecureRandom secureRandom = new SecureRandom();
  private final ExtensionAppAuthenticator authenticator;

  public AppTokenService(ExtensionAppAuthenticator authenticator) {
    this.authenticator = authenticator;
  }

  public Boolean validateTokens(TokenPair tokenPair) {
    return authenticator.validateTokens(tokenPair.getAppToken(), tokenPair.getSymphonyToken());
  }

  public String generateToken() {
    byte[] randBytes = new byte[64];
    secureRandom.nextBytes(randBytes);
    return Hex.encodeHexString(randBytes);
  }
}
