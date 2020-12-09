package com.symphony.bdk.core;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.exception.NoBotConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.signal.SignalService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.core.util.ServiceLookup;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;
import com.symphony.bdk.http.api.HttpClient;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Optional;

/**
 * BDK entry point.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class SymphonyBdk {

  private static final String NO_BOT_CONFIG_MESSAGE =
      "Bot info is not configured. The bot can be now only runnable only in OBO if the app authentication info is configured";

  private final BdkConfig config;

  private final OboAuthenticator oboAuthenticator;
  private final ExtensionAppAuthenticator extensionAppAuthenticator;

  private AuthSession botSession;
  private UserV2 botInfo;
  private ActivityRegistry activityRegistry;
  private StreamService streamService;
  private UserService userService;
  private MessageService messageService;
  private DatafeedService datafeedService;
  private PresenceService presenceService;
  private ConnectionService connectionService;
  private SignalService signalService;
  private ApplicationService applicationService;
  private HealthService healthService;

  public SymphonyBdk(BdkConfig config) throws AuthInitializationException, AuthUnauthorizedException {
    this(config, new ApiClientFactory(config));
  }

  protected SymphonyBdk(BdkConfig config, ApiClientFactory apiClientFactory)
      throws AuthInitializationException, AuthUnauthorizedException {
    this.config = config;

    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(config, apiClientFactory);
    this.oboAuthenticator = config.isOboConfigured() ? authenticatorFactory.getOboAuthenticator() : null;
    this.extensionAppAuthenticator =
        config.isOboConfigured() ? authenticatorFactory.getExtensionAppAuthenticator() : null;

    if (config.isBotConfigured()) {
      this.botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
      // service init
      final ServiceFactory serviceFactory = new ServiceFactory(apiClientFactory, this.botSession, config);
      SessionService sessionService = serviceFactory.getSessionService();
      this.userService = serviceFactory.getUserService();
      this.streamService = serviceFactory.getStreamService();
      this.presenceService = serviceFactory.getPresenceService();
      this.connectionService = serviceFactory.getConnectionService();
      this.signalService = serviceFactory.getSignalService();
      this.applicationService = serviceFactory.getApplicationService();
      this.healthService = serviceFactory.getHealthService();
      this.messageService = serviceFactory.getMessageService();
      this.datafeedService = serviceFactory.getDatafeedService();

      // retrieve bot session info
      this.botInfo = sessionService.getSession(this.botSession);

      // setup activities
      this.activityRegistry = new ActivityRegistry(this.botInfo, this.datafeedService::subscribe);
    } else {
      log.warn(NO_BOT_CONFIG_MESSAGE);
    }
  }

  /**
   * Get the {@link HttpClient.Builder} from a Bdk entry point.
   * The returned HttpClient builder instance depends on which {@link ApiClientBuilderProvider} is implemented.
   *
   * @return {@link HttpClient.Builder} HttpClient builder instance.
   */
  public HttpClient.Builder http() {
    return HttpClient.builder(ServiceLookup.lookupSingleService(ApiClientBuilderProvider.class));
  }

  /**
   * Get the {@link MessageService} from a Bdk entry point.
   * The returned message service instance.
   *
   * @return {@link MessageService} message service instance.
   */
  public MessageService messages() {
    if (this.messageService != null) {
      return this.messageService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link DatafeedService} from a Bdk entry point.
   * The returned datafeed service instance depends on the configuration of datafeed version.
   *
   * @return {@link DatafeedService} datafeed service instance.
   */
  public DatafeedService datafeed() {
    if (this.datafeedService != null) {
      return this.datafeedService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link UserService} from a Bdk entry point.
   *
   * @return {@link UserService} user service instance.
   */
  public UserService users() {
    if (this.userService != null) {
      return this.userService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link StreamService} from a Bdk entry point.
   *
   * @return {@link StreamService} user service instance.
   */
  public StreamService streams() {
    if (this.streamService != null) {
      return this.streamService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link PresenceService} from a Bdk entry point.
   *
   * @return {@link PresenceService} presence service instance.
   */
  public PresenceService presences() {
    if (this.presenceService != null) {
      return this.presenceService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link ConnectionService} from a Bdk entry point.
   *
   * @return {@link ConnectionService} connection service instance.
   */
  public ConnectionService connections() {
    if (this.connectionService != null) {
      return this.connectionService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link SignalService} from a Bdk entry point.
   *
   * @return {@link SignalService} signal service instance.
   */
  public SignalService signals() {
    if (this.signalService != null) {
      return this.signalService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link ApplicationService} from a Bdk entry point.
   *
   * @return {@link ApplicationService} application service instance.
   */
  public ApplicationService applications() {
    if (this.applicationService != null) {
      return this.applicationService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Get the {@link HealthService} from a Bdk entry point.
   *
   * @return {@link HealthService} health service instance.
   */
  public HealthService health() {
    if (this.healthService != null) {
      return this.healthService;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * Returns the {@link ActivityRegistry} in order to register Command or Form activities.
   *
   * @return the single {@link ActivityRegistry}
   */
  public ActivityRegistry activities() {
    if (this.activityRegistry != null) {
      return this.activityRegistry;
    }
    throw new NoBotConfigException(NO_BOT_CONFIG_MESSAGE);
  }

  /**
   * OBO Authenticate by using user Id.
   *
   * @param id User id
   * @return Obo authentication session
   */
  public AuthSession obo(Long id) throws AuthUnauthorizedException {
    return this.getOboAuthenticator().authenticateByUserId(id);
  }

  /**
   * OBO Authenticate by using username.
   *
   * @param username Username
   * @return Obo authentication session
   */
  public AuthSession obo(String username) throws AuthUnauthorizedException {
    return this.getOboAuthenticator().authenticateByUsername(username);
  }

  /**
   * Get an {@link OboServices} gathering all OBO enabled services
   *
   * @param oboSession the OBO session to use
   * @return an {@link OboServices} instance using the provided OBO session
   */
  public OboServices obo(AuthSession oboSession) {
    return new OboServices(config, oboSession);
  }

  /**
   * Returns the {@link ExtensionAppAuthenticator}.
   *
   * @return the {@link ExtensionAppAuthenticator}
   */
  public ExtensionAppAuthenticator appAuthenticator() {
    return this.getExtensionAppAuthenticator();
  }

  /**
   * Returns the Bot session.
   *
   * @return the bot {@link AuthSession}
   */
  @API(status = API.Status.EXPERIMENTAL)
  public AuthSession botSession() {
    return this.botSession;
  }

  /**
   * Returns the bot information.
   *
   * @return bot information.
   */
  @API(status = API.Status.EXPERIMENTAL)
  public UserV2 botInfo() {
    return this.botInfo;
  }

  protected ExtensionAppAuthenticator getExtensionAppAuthenticator() {
    return Optional.ofNullable(this.extensionAppAuthenticator)
        .orElseThrow(() -> new IllegalStateException("Extension app is not configured."));
  }

  protected OboAuthenticator getOboAuthenticator() {
    return Optional.ofNullable(this.oboAuthenticator)
        .orElseThrow(() -> new IllegalStateException("OBO is not configured."));
  }

}
