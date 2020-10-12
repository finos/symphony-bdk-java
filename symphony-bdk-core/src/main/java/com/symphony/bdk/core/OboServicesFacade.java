package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.message.OboMessageService;
import com.symphony.bdk.core.service.stream.OboStreamService;
import com.symphony.bdk.core.service.user.OboUserService;

import org.apiguardian.api.API;

/**
 * Entry point for OBO-enabled services.
 */
@API(status = API.Status.EXPERIMENTAL)
public class OboServicesFacade {

  private final OboStreamService oboStreamService;
  private final OboUserService oboUserService;
  private final OboMessageService oboMessageService;

  public OboServicesFacade(BdkConfig config, AuthSession oboSession) {
    final OboServiceFactory serviceFactory = new OboServiceFactory(new ApiClientFactory(config), oboSession, config);

    oboStreamService = serviceFactory.getOboStreamService();
    oboUserService = serviceFactory.getObUserService();
    oboMessageService = serviceFactory.getOboMessageService();
  }

  /**
   * Get the {@link OboStreamService} using the provided OBO session in constructor.
   *
   * @return an {@link OboStreamService} instance with the provided OBO session.
   */
  public OboStreamService streams() {
    return oboStreamService;
  }

  /**
   * Get the {@link OboUserService} using the provided OBO session in constructor.
   *
   * @return an {@link OboUserService} instance with the provided OBO session.
   */
  public OboUserService users() {
    return oboUserService;
  }

  /**
   * Get the {@link OboMessageService} using the provided OBO session in constructor.
   * The returned message service instance.
   *
   * @return an {@link OboMessageService} instance with the provided OBO session.
   */
  public OboMessageService messages() {
    return oboMessageService;
  }
}
