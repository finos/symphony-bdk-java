package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;

public interface AuthSessionAware extends Extension {
  void setAuthSession(AuthSession authSession);
}
