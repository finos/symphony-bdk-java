package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.AuthSession;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public interface BdkAuthenticationAware {

  void setAuthSession(AuthSession session);
}