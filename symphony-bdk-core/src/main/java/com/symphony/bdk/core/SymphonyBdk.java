package com.symphony.bdk.core;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.HelpCommand;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.exception.BotNotConfiguredException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
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

  private final BdkConfig config;

  private final OboAuthenticator oboAuthenticator;
  private final ExtensionAppAuthenticator extensionAppAuthenticator;

  private final AuthSession botSession;
  private final UserV2 botInfo;
  private final DatafeedLoop datafeedLoop;
  private final ActivityRegistry activityRegistry;
  private final StreamService streamService;
  private final UserService userService;
  private final MessageService messageService;
  private final PresenceService presenceService;
  private final ConnectionService connectionService;
  private final SignalService signalService;
  private final ApplicationService applicationService;
  private final SessionService sessionService;
  private final HealthService healthService;

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

    ServiceFactory serviceFactory = null;
    if (config.isBotConfigured()) {
      this.botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
      // service init
      serviceFactory = new ServiceFactory(apiClientFactory, this.botSession, config);
    } else {
      log.info(
          "Bot (service account) credentials have not been configured. You can however use services in OBO mode if app authentication is configured.");
      this.botSession = null;
    }
    this.sessionService = serviceFactory != null ? serviceFactory.getSessionService() : null;
    this.userService = serviceFactory != null ? serviceFactory.getUserService() : null;
    this.streamService = serviceFactory != null ? serviceFactory.getStreamService() : null;
    this.presenceService = serviceFactory != null ? serviceFactory.getPresenceService() : null;
    this.connectionService = serviceFactory != null ? serviceFactory.getConnectionService() : null;
    this.signalService = serviceFactory != null ? serviceFactory.getSignalService() : null;
    this.applicationService = serviceFactory != null ? serviceFactory.getApplicationService() : null;
    this.healthService = serviceFactory != null ? serviceFactory.getHealthService() : null;
    this.messageService = serviceFactory != null ? serviceFactory.getMessageService() : null;
    this.datafeedLoop = serviceFactory != null ? serviceFactory.getDatafeedLoop() : null;

    // retrieve bot session info
    this.botInfo = sessionService != null ? sessionService.getSession() : null;

    // setup activities
    if (this.datafeedLoop != null) {
      this.activityRegistry = new ActivityRegistry(this.botInfo, this.datafeedLoop);
      if (this.messageService != null) {
        HelpCommand helpCommand = new HelpCommand(this.activityRegistry, this.messageService);
        this.activityRegistry.register(helpCommand);
      }
    } else {
      this.activityRegistry = null;
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
    return getOrThrowNoBotConfig(this.messageService);
  }

  /**
   * Get the {@link DatafeedLoop} from a Bdk entry point.
   * The returned datafeed service instance depends on the configuration of datafeed version.
   *
   * @return {@link DatafeedLoop} datafeed service instance.
   */
  public DatafeedLoop datafeed() {
    return getOrThrowNoBotConfig(this.datafeedLoop);
  }

  /**
   * Get the {@link UserService} from a Bdk entry point.
   *
   * @return {@link UserService} user service instance.
   */
  public UserService users() {
    return getOrThrowNoBotConfig(this.userService);
  }

  /**
   * Get the {@link StreamService} from a Bdk entry point.
   *
   * @return {@link StreamService} user service instance.
   */
  public StreamService streams() {
    return getOrThrowNoBotConfig(this.streamService);
  }

  /**
   * Get the {@link PresenceService} from a Bdk entry point.
   *
   * @return {@link PresenceService} presence service instance.
   */
  public PresenceService presences() {
    return getOrThrowNoBotConfig(this.presenceService);
  }

  /**
   * Get the {@link ConnectionService} from a Bdk entry point.
   *
   * @return {@link ConnectionService} connection service instance.
   */
  public ConnectionService connections() {
    return getOrThrowNoBotConfig(this.connectionService);
  }

  /**
   * Get the {@link SignalService} from a Bdk entry point.
   *
   * @return {@link SignalService} signal service instance.
   */
  public SignalService signals() {
    return getOrThrowNoBotConfig(this.signalService);
  }

  /**
   * Get the {@link ApplicationService} from a Bdk entry point.
   *
   * @return {@link ApplicationService} application service instance.
   */
  public ApplicationService applications() {
    return getOrThrowNoBotConfig(this.applicationService);
  }

  /**
   * Get the {@link HealthService} from a Bdk entry point.
   *
   * @return {@link HealthService} health service instance.
   */
  public HealthService health() {
    return getOrThrowNoBotConfig(this.healthService);
  }

  /**
   * Get the {@link SessionService} from a Bdk entry point.
   *
   * @return {@link SessionService} session service instance.
   */
  public SessionService sessions() {
    return getOrThrowNoBotConfig(this.sessionService);
  }

  /**
   * Returns the {@link ActivityRegistry} in order to register Command or Form activities.
   *
   * @return the single {@link ActivityRegistry}
   */
  public ActivityRegistry activities() {
    return getOrThrowNoBotConfig(this.activityRegistry);
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

  private <T> T getOrThrowNoBotConfig(T field) {
    return Optional.ofNullable(field).orElseThrow(BotNotConfiguredException::new);
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
