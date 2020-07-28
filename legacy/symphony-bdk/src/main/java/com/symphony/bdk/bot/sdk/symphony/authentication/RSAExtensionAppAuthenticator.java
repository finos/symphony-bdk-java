package com.symphony.bdk.bot.sdk.symphony.authentication;

import authentication.SymExtensionAppRSAAuth;
import authentication.extensionapp.TokensRepository;
import configuration.SymConfig;
import model.AppAuthResponse;
import model.UserInfo;
import utils.HttpClientBuilderHelper;

public class RSAExtensionAppAuthenticator implements ExtensionAppAuthenticator {

  private final SymExtensionAppRSAAuth symExtensionAppRSAAuth;

  public RSAExtensionAppAuthenticator(SymConfig symConfig, TokensRepository tokensRepository) {
    this.symExtensionAppRSAAuth = new SymExtensionAppRSAAuth(symConfig,
        HttpClientBuilderHelper.getPodClientConfig(symConfig),
        tokensRepository);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AppAuthResponse appAuthenticate() {
    return symExtensionAppRSAAuth.appAuthenticate();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean validateTokens(String appToken, String symphonyToken) {
    return symExtensionAppRSAAuth.validateTokens(appToken, symphonyToken);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserInfo verifyJWT(String jwt) {
    return symExtensionAppRSAAuth.verifyJWT(jwt);
  }

}
