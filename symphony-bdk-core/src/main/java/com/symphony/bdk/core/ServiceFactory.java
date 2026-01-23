package com.symphony.bdk.core;

import static com.symphony.bdk.core.auth.impl.OAuthentication.BEARER_AUTH;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.impl.OAuthSession;
import com.symphony.bdk.core.auth.impl.OAuthentication;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.DatahoseLoop;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV2;
import com.symphony.bdk.core.service.datafeed.impl.DatahoseLoopImpl;
import com.symphony.bdk.core.service.disclaimer.DisclaimerService;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.signal.SignalService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.AppEntitlementApi;
import com.symphony.bdk.gen.api.ApplicationApi;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.AuditTrailApi;
import com.symphony.bdk.gen.api.ConnectionApi;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.DatahoseApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.DisclaimerApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.PresenceApi;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.template.api.TemplateEngine;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * Factory responsible for creating BDK service instances for Symphony Bdk entry point.
 * :
 * <ul>
 *   <li>{@link UserService}</li>
 *   <li>{@link StreamService}</li>
 *   <li>{@link MessageService}</li>
 *   <li>{@link DatafeedLoop}</li>
 *   <li>{@link SessionService}</li>
 * </ul>
 */
@Slf4j
@API(status = API.Status.INTERNAL)
class ServiceFactory {

  private final ApiClient podClient;
  private final ApiClient agentClient;
  private final ApiClient datafeedAgentClient;
  private final ApiClient datahoseAgentClient;
  private final AuthSession authSession;
  private final TemplateEngine templateEngine;
  private final BdkConfig config;
  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public ServiceFactory(ApiClientFactory apiClientFactory, AuthSession authSession, BdkConfig config) {
    this.config = config;
    this.podClient = apiClientFactory.getPodClient();
    this.agentClient = apiClientFactory.getAgentClient();
    this.datafeedAgentClient = apiClientFactory.getDatafeedAgentClient();
    this.datahoseAgentClient = apiClientFactory.getDatahoseAgentClient();
    this.authSession = authSession;
    this.templateEngine = TemplateEngine.getDefaultImplementation();
    this.retryBuilder = new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry());

    if (config.isCommonJwtEnabled()) {
      if (config.isOboConfigured()) {
        throw new UnsupportedOperationException("Common JWT feature is not available yet in OBO mode,"
            + " please set commonJwt.enabled to false.");
      } else {
        final OAuthSession oAuthSession = new OAuthSession(authSession);

        this.podClient.getAuthentications().put(BEARER_AUTH, new OAuthentication(oAuthSession::getBearerToken));
        this.podClient.addEnforcedAuthenticationScheme(BEARER_AUTH);

        this.agentClient.getAuthentications().put(BEARER_AUTH, new OAuthentication(oAuthSession::getBearerToken));
        this.agentClient.addEnforcedAuthenticationScheme(BEARER_AUTH);

        this.datafeedAgentClient.getAuthentications().put(BEARER_AUTH, new OAuthentication(oAuthSession::getBearerToken));
        this.datafeedAgentClient.addEnforcedAuthenticationScheme(BEARER_AUTH);

        this.datahoseAgentClient.getAuthentications().put(BEARER_AUTH, new OAuthentication(oAuthSession::getBearerToken));
        this.datahoseAgentClient.addEnforcedAuthenticationScheme(BEARER_AUTH);
      }
    }
  }

  /**
   * Returns a fully initialized {@link UserService}.
   *
   * @return a new {@link UserService} instance.
   */
  public UserService getUserService() {
    return new UserService(new UserApi(podClient), new UsersApi(podClient), new AuditTrailApi(agentClient), authSession,
        retryBuilder);
  }

  /**
   * Returns a fully initialized {@link StreamService}.
   *
   * @return a new {@link StreamService} instance.
   */
  public StreamService getStreamService() {
    return new StreamService(new StreamsApi(podClient), new RoomMembershipApi(podClient), new ShareApi(agentClient),
        authSession, retryBuilder);
  }

  public DisclaimerService getDisclaimerService() {
    return new DisclaimerService(new DisclaimerApi(podClient), authSession, retryBuilder);
  }

  /**
   * Returns a fully initialized {@link SessionService}.
   *
   * @return a new {@link SessionService} instance.
   */
  public SessionService getSessionService() {
    return new SessionService(new SessionApi(podClient), authSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  /**
   * Returns a fully initialized {@link DatafeedLoop}.
   *
   * @return a new {@link DatafeedLoop} instance.
   */
  public DatafeedLoop getDatafeedLoop(UserV2 botInfo) {
    if (DatafeedVersion.of(config.getDatafeed().getVersion()) == DatafeedVersion.V2) {
      return new DatafeedLoopV2(new DatafeedApi(datafeedAgentClient), authSession, config, botInfo);
    }
    return new DatafeedLoopV1(new DatafeedApi(datafeedAgentClient), authSession, config, botInfo);
  }

  public DatahoseLoop getDatahoseLoop(UserV2 botInfo) {
    return new DatahoseLoopImpl(new DatafeedApi(datahoseAgentClient), authSession, config, botInfo, new DatahoseApi(datahoseAgentClient));
  }

  /**
   * Returns a fully initialized {@link MessageService}.
   *
   * @return a new {@link MessageService} instance.
   */
  public MessageService getMessageService() {
    return new MessageService(
        new MessagesApi(this.agentClient),
        new MessageApi(this.podClient),
        new MessageSuppressionApi(this.podClient),
        new StreamsApi(this.podClient),
        new PodApi(this.podClient),
        new AttachmentsApi(this.agentClient),
        new DefaultApi(this.podClient),
        this.authSession,
        this.templateEngine,
        this.retryBuilder
    );
  }

  /**
   * Returns a fully initialized {@link PresenceService}.
   *
   * @return a new {@link PresenceService} instance.
   */
  public PresenceService getPresenceService() {
    return new PresenceService(new PresenceApi(this.podClient), this.authSession, this.retryBuilder);
  }

  /**
   * Returns a fully initialized {@link ConnectionService}.
   *
   * @return a new {@link ConnectionService} instance.
   */
  public ConnectionService getConnectionService() {
    return new ConnectionService(new ConnectionApi(this.podClient), this.authSession, this.retryBuilder);
  }

  /**
   * Returns a fully initialized {@link SignalService}.
   *
   * @return a new {@link SignalService} instance.
   */
  public SignalService getSignalService() {
    return new SignalService(new SignalsApi(this.agentClient), this.authSession, this.retryBuilder);
  }

  /**
   * Returns a fully initialized {@link ApplicationService}.
   *
   * @return a new {@link ApplicationService} instance.
   */
  public ApplicationService getApplicationService() {
    return new ApplicationService(new ApplicationApi(this.podClient), new AppEntitlementApi(podClient),
        this.authSession, this.retryBuilder);
  }

  /**
   * Returns a fully initialized {@link HealthService}.
   *
   * @return a new {@link HealthService} instance.
   */
  public HealthService getHealthService() {
    return new HealthService(new SystemApi(this.agentClient), new SignalsApi(this.agentClient), this.authSession);
  }

}
