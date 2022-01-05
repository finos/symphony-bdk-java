package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;

public interface BdkAuthenticationAware {

  void setAuthSession(AuthSession session);
}
