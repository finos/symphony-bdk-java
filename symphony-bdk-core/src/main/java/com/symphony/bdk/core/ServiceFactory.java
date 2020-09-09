package com.symphony.bdk.core;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;

class ServiceFactory {

  private final UserService userService;
  private final StreamService streamService;
  private final MessageService messageService;
  private final SessionService sessionService;
  private final DatafeedService datafeedService;

  public ServiceFactory(ApiClient podClient, ApiClient agentClient, AuthSession authSession, BdkConfig config) {

    this.userService = new UserService(new UserApi(podClient), new UsersApi(podClient), authSession);
    this.streamService = new StreamService(new StreamsApi(podClient), authSession);
    this.messageService = new MessageService(new MessagesApi(agentClient), authSession);
    this.sessionService = new SessionService(new SessionApi(podClient));
    if (DatafeedVersion.of(config.getDatafeed().getVersion()) == DatafeedVersion.V2) {
      this.datafeedService = new DatafeedServiceV2(new DatafeedApi(agentClient), authSession, config);
    } else {
      this.datafeedService = new DatafeedServiceV1(new DatafeedApi(agentClient), authSession, config);
    }
  }

  public UserService getUserService() {
    return this.userService;
  }

  public StreamService getStreamService() {
    return this.streamService;
  }

  public MessageService getMessageService() {
    return this.messageService;
  }

  public SessionService getSessionService() {
    return this.sessionService;
  }

  public DatafeedService getDatafeedService() {
    return this.datafeedService;
  }
}
