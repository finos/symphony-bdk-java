package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.model.UserV2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SymphonyBdkTest {

  private SymphonyBdk symphonyBdk;

  @BeforeEach
  void setUp() throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    ApiClientFactory apiClientFactory = new ApiClientFactory(config);

    AuthSession authSession = mock(AuthSession.class);
    doReturn("1234").when(authSession).getSessionToken();
    doReturn("1234").when(authSession).getKeyManagerToken();

    BotAuthenticator botAuthenticator = mock(BotAuthenticator.class);
    doReturn(authSession).when(botAuthenticator).authenticateBot();

    OboAuthenticator oboAuthenticator = mock(OboAuthenticator.class);
    doReturn(authSession).when(oboAuthenticator).authenticateByUsername(anyString());
    doReturn(authSession).when(oboAuthenticator).authenticateByUserId(anyLong());

    AuthenticatorFactory authenticatorFactory = mock(AuthenticatorFactory.class);
    doReturn(botAuthenticator).when(authenticatorFactory).getBotAuthenticator();
    doReturn(oboAuthenticator).when(authenticatorFactory).getOboAuthenticator();

    ServiceFactory serviceFactory = new ServiceFactory(apiClientFactory.getPodClient(), apiClientFactory.getAgentClient(), authSession, config);
    ServiceFactory spiedServiceFactory = spy(serviceFactory);
    SessionService sessionService = mock(SessionService.class);
    doReturn(new UserV2().id(123L)).when(sessionService).getSession(authSession);
    doReturn(sessionService).when(spiedServiceFactory).getSessionService();

    this.symphonyBdk = new SymphonyBdk(config, spiedServiceFactory, authenticatorFactory);
  }

  @Test
  void getDatafeedServiceTest() {
    DatafeedService datafeedService = this.symphonyBdk.datafeed();

    assertNotNull(datafeedService);
    assertEquals(datafeedService.getClass(), DatafeedServiceV1.class);
  }

  @Test
  void getStreamServiceTest() {
    StreamService streamService = this.symphonyBdk.streams();
    assertNotNull(streamService);
  }

  @Test
  void getUserServiceTest() {
    UserService userService = this.symphonyBdk.users();
    assertNotNull(userService);
  }

  @Test
  void getActivitiesTest() {
    ActivityRegistry registry = this.symphonyBdk.activities();
    assertNotNull(registry);
  }

  @Test
  void getMessageServiceTest() {
    MessageService messageService = this.symphonyBdk.messages();
    assertNotNull(messageService);
  }

  @Test
  void oboAuthenticateTest() throws AuthUnauthorizedException {
    AuthSession authSessionById = this.symphonyBdk.obo(123456L);
    assertEquals(authSessionById.getSessionToken(), "1234");
    assertEquals(authSessionById.getKeyManagerToken(), "1234");

    AuthSession authSessionByUsername = this.symphonyBdk.obo("username");
    assertEquals(authSessionByUsername.getKeyManagerToken(), "1234");
    assertEquals(authSessionByUsername.getSessionToken(), "1234");
  }
}
