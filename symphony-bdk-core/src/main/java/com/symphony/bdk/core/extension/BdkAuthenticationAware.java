package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.auth.BotAuthSession;

import org.apiguardian.api.API;

/**
 * Interface to be implemented by any {@link com.symphony.bdk.extension.BdkExtension} that wishes to use the {@link BotAuthSession}
 * of the bot service account.
 *
 * @see com.symphony.bdk.extension.BdkExtension
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkAuthenticationAware {

  /**
   * Set the {@link BotAuthSession} object.
   *
   * @param session the {@code AuthSession} instance to be used by this object
   */
  void setAuthSession(BotAuthSession session);
}
