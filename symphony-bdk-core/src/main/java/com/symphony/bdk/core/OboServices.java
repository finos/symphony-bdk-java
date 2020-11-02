package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.connection.OboConnectionService;
import com.symphony.bdk.core.service.message.OboMessageService;
import com.symphony.bdk.core.service.presence.OboPresenceService;
import com.symphony.bdk.core.service.signal.OboSignalService;
import com.symphony.bdk.core.service.stream.OboStreamService;
import com.symphony.bdk.core.service.user.OboUserService;

import org.apiguardian.api.API;

/**
 * Entry point for OBO-enabled services.
 */
@API(status = API.Status.EXPERIMENTAL)
public class OboServices {

  private final OboStreamService oboStreamService;
  private final OboUserService oboUserService;
  private final OboMessageService oboMessageService;
  private final OboPresenceService oboPresenceService;
  private final OboConnectionService oboConnectionService;
  private final OboSignalService oboSignalService;

  public OboServices(BdkConfig config, AuthSession oboSession) {
    final ServiceFactory serviceFactory = new ServiceFactory(new ApiClientFactory(config), oboSession, config);

    oboStreamService = serviceFactory.getStreamService();
    oboUserService = serviceFactory.getUserService();
    oboMessageService = serviceFactory.getMessageService();
    oboPresenceService = serviceFactory.getPresenceService();
    oboConnectionService = serviceFactory.getConnectionService();
    oboSignalService = serviceFactory.getSignalService();
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
   * Get the {@link OboPresenceService} using the provided OBO session in constructor.
   *
   * @return an {@link OboPresenceService} instance with the provided OBO session.
   */
  public OboPresenceService presences() {
    return oboPresenceService;
  }

  /**
   * Get the {@link OboConnectionService} using the provided OBO session in constructor.
   *
   * @return an {@link OboConnectionService} instance with the provided OBO session.
   */
  public OboConnectionService connections() {
    return oboConnectionService;
  }

  /**
   * Get the {@link OboSignalService} using the provided OBO session in constructor.
   * The returned signal service instance.
   *
   * @return an {@link OboSignalService} instance with the provided OBO session.
   */
  public OboSignalService signals() {
    return oboSignalService;
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
