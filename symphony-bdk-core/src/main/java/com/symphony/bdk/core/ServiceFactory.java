package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.http.api.ApiClient;

import com.symphony.bdk.http.api.ApiException;

import org.apiguardian.api.API;

/**
 * Factory responsible for creating BDK service instances for Symphony Bdk entry point.
 * :
 * <ul>
 *   <li>{@link UserService}</li>
 *   <li>{@link StreamService}</li>
 *   <li>{@link MessageService}</li>
 *   <li>{@link DatafeedService}</li>
 *   <li>{@link SessionService}</li>
 * </ul>
 */
@API(status = API.Status.INTERNAL)
class ServiceFactory {

  private final ApiClient podClient;
  private final ApiClient agentClient;
  private final AuthSession authSession;
  private final BdkConfig config;
  private final RetryWithRecoveryBuilder retryBuilder;

  public ServiceFactory(ApiClientFactory apiClientFactory, AuthSession authSession, BdkConfig config) {
    this.podClient = apiClientFactory.getPodClient();
    this.agentClient = apiClientFactory.getAgentClient();
    this.authSession = authSession;
    this.config = config;
    this.retryBuilder = new RetryWithRecoveryBuilder<>()
        .retryConfig(config.getRetry())
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  /**
   * Returns a fully initialized {@link UserService}.
   *
   * @return an new {@link UserService} instance.
   */
  public UserService getUserService() {
    return new UserService(new UserApi(podClient), new UsersApi(podClient), authSession, retryBuilder);
  }

  /**
   * Returns a fully initialized {@link StreamService}.
   *
   * @return an new {@link StreamService} instance.
   */
  public StreamService getStreamService() {
    return new StreamService(new StreamsApi(podClient), new RoomMembershipApi(podClient), new ShareApi(agentClient),
        authSession, retryBuilder);
  }

  /**
   * Returns a fully initialized {@link SessionService}.
   *
   * @return an new {@link SessionService} instance.
   */
  public SessionService getSessionService() {
    return new SessionService(new SessionApi(podClient),
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  /**
   * Returns a fully initialized {@link DatafeedService}.
   *
   * @return an new {@link DatafeedService} instance.
   */
  public DatafeedService getDatafeedService() {
    if (DatafeedVersion.of(config.getDatafeed().getVersion()) == DatafeedVersion.V2) {
      return new DatafeedServiceV2(new DatafeedApi(agentClient), authSession, config);
    }
    return new DatafeedServiceV1(new DatafeedApi(agentClient), authSession, config);
  }

  /**
   * Returns a fully initialized {@link MessageService}.
   *
   * @return an new {@link MessageService} instance.
   */
  public MessageService getMessageService() {
    return new MessageService(new MessagesApi(agentClient), new MessageApi(podClient),
        new MessageSuppressionApi(podClient), new StreamsApi(podClient), new PodApi(podClient),
        new AttachmentsApi(agentClient), new DefaultApi(podClient), authSession, retryBuilder);
  }
}
