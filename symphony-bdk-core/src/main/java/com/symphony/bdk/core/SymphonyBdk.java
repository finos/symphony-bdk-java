package com.symphony.bdk.core;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
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

import com.symphony.bdk.core.util.ProviderLoader;
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

  private final OboAuthenticator oboAuthenticator;
  private final ExtensionAppAuthenticator extensionAppAuthenticator;

  private final ActivityRegistry activityRegistry;
  private final SessionService sessionService;
  private final StreamService streamService;
  private final UserService userService;
  private final MessageService messageService;
  private final DatafeedService datafeedService;

  public SymphonyBdk(BdkConfig config) throws AuthInitializationException, AuthUnauthorizedException {
    this(config, new ApiClientFactory(config));
  }

  protected SymphonyBdk(BdkConfig config, ApiClientFactory apiClientFactory)
      throws AuthInitializationException, AuthUnauthorizedException {
    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(config, apiClientFactory);
    AuthSession botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
    this.oboAuthenticator = config.isOboConfigured() ? authenticatorFactory.getOboAuthenticator() : null;
    this.extensionAppAuthenticator = config.isOboConfigured() ? authenticatorFactory.getExtensionAppAuthenticator() : null;
    ServiceFactory serviceFactory =
        new ServiceFactory(apiClientFactory, botSession, config);
    // service init
    this.sessionService = serviceFactory.getSessionService();
    this.userService = serviceFactory.getUserService();
    this.streamService = serviceFactory.getStreamService();
    this.messageService = serviceFactory.getMessageService();
    this.datafeedService = serviceFactory.getDatafeedService();

    // setup activities
    this.activityRegistry = new ActivityRegistry(this.sessionService.getSession(botSession), this.datafeedService::subscribe);
  }

  /**
   * Get the {@link HttpClient.Builder} from a Bdk entry point.
   * The returned HttpClient builder instance depends on which {@link ApiClientBuilderProvider} is implemented.
   *
   * @return {@link HttpClient.Builder} HttpClient builder instance.
   */
  public HttpClient.Builder http() {
    return HttpClient.builder(ProviderLoader.lookupSingleService(ApiClientBuilderProvider.class));
  }

  /**
   * Get the {@link MessageService} from a Bdk entry point.
   * The returned message service instance.
   *
   * @return {@link MessageService} message service instance.
   */
  public MessageService messages() {
    return this.messageService;
  }

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
   * Extension App Authenticate by app token
   * @param appToken App Token
   * @return Extension App authentication session
   */
  public AppAuthSession app(String appToken) throws AuthUnauthorizedException {
    return this.getExtensionAppAuthenticator().authenticateExtensionApp(appToken);
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
