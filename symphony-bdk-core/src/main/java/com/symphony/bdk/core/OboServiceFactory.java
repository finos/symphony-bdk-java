package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.message.OboMessageService;
import com.symphony.bdk.core.service.stream.OboStreamService;
import com.symphony.bdk.core.service.user.OboUserService;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apiguardian.api.API;

/**
 * Factory responsible for creating BDK OBO-enabled service instances for {@link OboServicesFacade}.
 * :
 * <ul>
 *   <li>{@link OboUserService}</li>
 *   <li>{@link OboStreamService}</li>
 *   <li>{@link OboMessageService}</li>
 * </ul>
 */
@API(status = API.Status.INTERNAL)
public class OboServiceFactory {

  protected final ApiClient podClient;
  protected final ApiClient agentClient;
  protected final AuthSession authSession;
  protected final TemplateEngine templateEngine;
  protected final BdkConfig config;
  protected final RetryWithRecoveryBuilder<?> retryBuilder;

  public OboServiceFactory(ApiClientFactory apiClientFactory, AuthSession authSession, BdkConfig config) {
    this.podClient = apiClientFactory.getPodClient();
    this.agentClient = apiClientFactory.getAgentClient();
    this.authSession = authSession;
    this.templateEngine = TemplateEngine.getDefaultImplementation();
    this.config = config;
    this.retryBuilder = new RetryWithRecoveryBuilder<>()
        .retryConfig(config.getRetry())
        .recoveryStrategy(ApiException::isUnauthorized, authSession::refresh);
  }

  /**
   * Returns a fully initialized {@link OboUserService}
   *
   * @return a new {@link OboUserService} instance
   */
  public OboUserService getObUserService() {
    return new OboUserService(new UserApi(podClient), new UsersApi(podClient), authSession, retryBuilder);
  }

  /**
   * Returns a fully initialized {@link OboStreamService}
   *
   * @return a new {@link OboStreamService} instance
   */
  public OboStreamService getOboStreamService() {
    return new OboStreamService(new StreamsApi(podClient), new RoomMembershipApi(podClient), new ShareApi(agentClient),
        authSession, retryBuilder);
  }

  /**
   * Returns a fully initialized {@link OboMessageService}
   *
   * @return a new {@link OboMessageService} instance
   */
  public OboMessageService getOboMessageService() {
    return new OboMessageService(new MessagesApi(this.agentClient), this.authSession, this.templateEngine,
        this.retryBuilder);
  }
}
