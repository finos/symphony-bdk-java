package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;

import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ServiceFactoryTest {

  private ServiceFactory serviceFactory;
  private ApiClientFactory apiClientFactory;
  private AuthSession mAuthSession;
  private BdkConfig config;

  @BeforeEach
  void setUp() throws BdkConfigException {
    this.config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    this.mAuthSession = mock(AuthSession.class);
    ApiClient mPodClient = mock(ApiClient.class);
    ApiClient mAgentClient = mock(ApiClient.class);
    this.apiClientFactory = mock(ApiClientFactory.class);

    when(this.apiClientFactory.getPodClient()).thenReturn(mPodClient);
    when(this.apiClientFactory.getAgentClient()).thenReturn(mAgentClient);

    this.serviceFactory = new ServiceFactory(this.apiClientFactory, this.mAuthSession, this.config);
  }

  @Test
  void getUserServiceTest() {
    UserService userService = this.serviceFactory.getUserService();
    assertNotNull(userService);
  }

  @Test
  void getStreamServiceTest() {
    StreamService streamService = this.serviceFactory.getStreamService();
    assertNotNull(streamService);
  }

  @Test
  void getPresenceServiceTest() {
    PresenceService presenceService = this.serviceFactory.getPresenceService();
    assertNotNull(presenceService);
  }

  @Test
  void getConnectionServiceTest() {
    ConnectionService connectionService = this.serviceFactory.getConnectionService();
    assertNotNull(connectionService);
  }

  @Test
  void getSessionServiceTest() {
    SessionService sessionService = this.serviceFactory.getSessionService();
    assertNotNull(sessionService);
  }

  @Test
  void getMessageServiceTest() {
    MessageService messageService = this.serviceFactory.getMessageService();
    assertNotNull(messageService);
  }

  @Test
  void getDatafeedServiceTest() {
    BdkDatafeedConfig datafeedConfig = this.config.getDatafeed();
    datafeedConfig.setVersion("v1");

    this.serviceFactory = new ServiceFactory(this.apiClientFactory, mAuthSession, config);
    DatafeedService datafeedServiceV1 = this.serviceFactory.getDatafeedService();
    assertNotNull(datafeedServiceV1);
    assertEquals(datafeedServiceV1.getClass(), DatafeedServiceV1.class);

    datafeedConfig.setVersion("v2");
    this.serviceFactory = new ServiceFactory(this.apiClientFactory, mAuthSession, config);
    DatafeedService datafeedServiceV2 = this.serviceFactory.getDatafeedService();
    assertNotNull(datafeedServiceV2);
    assertEquals(datafeedServiceV2.getClass(), DatafeedServiceV2.class);

  }
}
