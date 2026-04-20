package com.symphony.bdk.core.auth;

import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;

import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * Extension App Authentication session handle. The {@link ExtAppAuthSession#refresh()} will trigger a re-auth against the API endpoints.
 */
@API(status = API.Status.STABLE)
public interface ExtAppAuthSession {
  /**
   * Extension app session token.
   *
   * @return extension app session token
   */
  @Nullable
  String getAppSession();

  /**
   * Trigger re-authentication to refresh session token.
   */
  void refresh() throws AuthUnauthorizedException;
}
