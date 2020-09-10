package com.symphony.bdk.core;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Optional;

/**
 * BDK entry point.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class SymphonyBdk {

  private final OboAuthenticator oboAuthenticator;

  private final ActivityRegistry activityRegistry;
  private final SessionService sessionService;
  private final StreamService streamService;
  private final UserService userService;
  private final MessageService messageService;
  private final DatafeedService datafeedService;

  public SymphonyBdk(BdkConfig config) throws AuthInitializationException, AuthUnauthorizedException {
    ApiClientFactory apiClientFactory = new ApiClientFactory(config);
    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(config, apiClientFactory);
    AuthSession botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
    this.oboAuthenticator = config.isOboConfigured() ? authenticatorFactory.getOboAuthenticator() : null;
    ServiceFactory serviceFactory =
        new ServiceFactory(apiClientFactory.getPodClient(), apiClientFactory.getAgentClient(), botSession, config);

    // service init
    this.sessionService = serviceFactory.getSessionService();
    this.userService = serviceFactory.getUserService();
    this.streamService = serviceFactory.getStreamService();
    this.messageService = serviceFactory.getMessageService();
    this.datafeedService = serviceFactory.getDatafeedService();

    // setup activities
    this.activityRegistry = new ActivityRegistry(this.sessionService.getSession(botSession), this.datafeedService::subscribe);
  }

  public MessageService messages() {
    return this.messageService;
  }

//  public MessageService messages(Obo.Handle oboHandle) throws AuthUnauthorizedException {
//    AuthSession oboSession;
//    if (oboHandle.hasUsername()) {
//      oboSession = this.getOboAuthenticator().authenticateByUsername(oboHandle.getUsername());
//    } else {
//      oboSession = this.getOboAuthenticator().authenticateByUserId(oboHandle.getUserId());
//    }
//    return new MessageService(new MessagesApi(this.agentClient), oboSession);
//  }

  /**
   * Get the {@link DatafeedService} from a Bdk entry point.
   * The returned datafeed service instance depends on the configuration of datafeed version.
   *
   * @return {@link DatafeedService} datafeed service instance.
   */
  public DatafeedService datafeed() {
    return this.datafeedService;
  }

  /**
   * Get the {@link UserService} from a Bdk entry point.
   *
   * @return {@link UserService} user service instance.
   */
  public UserService users() {
    return this.userService;
  }

  /**
   * Get the {@link StreamService} from a Bdk entry point.
   *
   * @return {@link StreamService} user service instance.
   */
  public StreamService streams() {
    return this.streamService;
  }

  /**
   * Returns the {@link ActivityRegistry} in order to register Command or Form activities.
   *
   * @return the single {@link ActivityRegistry}
   */
  public ActivityRegistry activities() {
    return this.activityRegistry;
  }

  public AuthSession obo(Long id) throws AuthUnauthorizedException {
    return this.getOboAuthenticator().authenticateByUserId(id);
  }

  public AuthSession obo(String username) throws AuthUnauthorizedException {
    return this.getOboAuthenticator().authenticateByUsername(username);
  }

  protected OboAuthenticator getOboAuthenticator() {
    return Optional.ofNullable(this.oboAuthenticator)
        .orElseThrow(() -> new IllegalStateException("OBO is not configured."));
  }

}
