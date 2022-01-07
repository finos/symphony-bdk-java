package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.extension.BdkExtension;
import com.symphony.bdk.extension.BdkExtensionServiceProvider;

import lombok.Getter;
import lombok.Setter;

public class TestExtensionAuthenticationAware implements BdkExtension, BdkAuthenticationAware,
    BdkExtensionServiceProvider<TestExtensionAuthenticationAware.TestService> {

  @Getter
  @Setter
  public static class TestService {
    AuthSession authSession;
  }

  private final TestService service = new TestService();

  @Override
  public void setAuthSession(AuthSession session) {
    this.service.setAuthSession(session);
  }

  @Override
  public TestService getService() {
    return this.service;
  }
}
