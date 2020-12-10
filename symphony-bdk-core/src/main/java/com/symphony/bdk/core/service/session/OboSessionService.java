package com.symphony.bdk.core.service.session;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.gen.api.model.UserV2;

import org.apiguardian.api.API;

/**
 * Service interface exposing OBO-enabled endpoints to get user session information.
 */
@API(status = API.Status.STABLE)
public interface OboSessionService {

  /**
   * Retrieves the {@link UserV2} session from the pod using an {@link AuthSession} holder.
   *
   * @return User session info.
   */
  UserV2 getSession();
}
