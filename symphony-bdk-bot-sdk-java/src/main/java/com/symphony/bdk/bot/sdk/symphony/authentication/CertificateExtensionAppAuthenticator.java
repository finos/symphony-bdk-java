package com.symphony.bdk.bot.sdk.symphony.authentication;

import java.security.SecureRandom;

import org.apache.commons.codec.binary.Hex;

import authentication.SymExtensionAppAuth;
import authentication.extensionapp.TokensRepository;
import configuration.SymConfig;
import model.AppAuthResponse;
import model.UserInfo;

public class CertificateExtensionAppAuthenticator implements ExtensionAppAuthenticator {

  private final TokensRepository tokensRepository;
  private final SymExtensionAppAuth symExtensionAppAuth;

  public CertificateExtensionAppAuthenticator(SymConfig symConfig, TokensRepository tokensRepository) {
    symExtensionAppAuth = new SymExtensionAppAuth(symConfig);
    this.tokensRepository = tokensRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AppAuthResponse appAuthenticate() {
    AppAuthResponse appAuthResponse = symExtensionAppAuth.sessionAppAuthenticate(generateAppToken());
    if (appAuthResponse != null) {
      tokensRepository.save(appAuthResponse);
    }
    return appAuthResponse;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validateTokens(String appToken, String symphonyToken) {
    return tokensRepository.get(appToken)
        .filter(token -> token.getSymphonyToken().equals(symphonyToken))
        .isPresent();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserInfo verifyJWT(String jwt) {
    return symExtensionAppAuth.verifyJWT(jwt, new String[0]);
  }

  private String generateAppToken() {
    byte[] randBytes = new byte[64];
    new SecureRandom().nextBytes(randBytes);
    return Hex.encodeHexString(randBytes);
  }

}
