package com.symphony.bdk.core.auth.impl;

import com.symphony.bdk.core.auth.ExtAppAuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;
import org.jetbrains.annotations.Nullable;

/**
 * {@link ExtAppAuthSession} impl for Extension App Certificate authentication mode.
 */
@API(status = API.Status.INTERNAL)
public class ExtAppAuthSessionCertImpl implements ExtAppAuthSession {

  String appSession;
  ExtAppAuthenticatorCertImpl authenticator;

  public ExtAppAuthSessionCertImpl(ExtAppAuthenticatorCertImpl authenticator) {
    this.authenticator = authenticator;
  }

  @Nullable
  @Override
  public String getAppSession() {
    return appSession;
  }

  @Override
  public void refresh() throws AuthUnauthorizedException {
    this.appSession = this.authenticator.retrieveAppSessionToken();
  }

  /**
   * This method is only visible for testing.
   */
  protected ExtAppAuthenticatorCertImpl getAuthenticator() {
    return authenticator;
  }
}
