package com.symphony.ms.bot.sdk.internal.symphony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.symphony.exception.AppAuthenticateException;
import com.symphony.ms.bot.sdk.internal.symphony.model.AuthenticateResponse;
import authentication.SymExtensionAppRSAAuth;
import model.AppAuthResponse;
import model.UserInfo;

@Service
public class ExtensionAppAuthClientImpl implements ExtensionAppAuthClient {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExtensionAppAuthClientImpl.class);

  private SymExtensionAppRSAAuth symExtensionAppRSAAuth;

  public ExtensionAppAuthClientImpl(SymExtensionAppRSAAuth symExtensionAppRSAAuth) {
    this.symExtensionAppRSAAuth = symExtensionAppRSAAuth;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthenticateResponse appAuthenticate(String appId) {
    try {
      AppAuthResponse appAuthToken = symExtensionAppRSAAuth.appAuthenticate();
      return new AuthenticateResponse(appId, appAuthToken.getAppToken());
    } catch (Exception e) {
      LOGGER.error("Error authentication extension app: {}\n{}", appId, e);
      throw new AppAuthenticateException();
    }
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
  public Long verifyJWT(String jwt) {
    UserInfo userInfo = symExtensionAppRSAAuth.verifyJWT(jwt);
    if (userInfo != null) {
      return userInfo.getId();
    }
    throw new AppAuthenticateException();
  }

}
