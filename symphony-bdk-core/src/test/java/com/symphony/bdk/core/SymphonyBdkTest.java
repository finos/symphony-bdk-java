package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SymphonyBdkTest {

  private SymphonyBdk symphonyBdk;
  private MockApiClient mockApiClient;
  private static final String LOGIN_PUBKEY_AUTHENTICATE = "/login/pubkey/authenticate";
  private static final String LOGIN_PUBKEY_APP_AUTHENTICATE = "/login/pubkey/app/authenticate";
  private static final String LOGIN_PUBKEY_OBO_USERID_AUTHENTICATE = "/login/pubkey/app/user/{userId}/authenticate";
  private static final String LOGIN_PUBKEY_OBO_USERNAME_AUTHENTICATE = "/login/pubkey/app/username/{username}/authenticate";
  private static final String RELAY_PUBKEY_AUTHENTICATE = "/relay/pubkey/authenticate";
  private static final String V2_SESSION_INFO = "/pod/v2/sessioninfo";

  @BeforeEach
  void setUp() throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException, IOException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    config.getBot().setPrivateKeyPath("./src/test/resources/keys/private-key.pem");
    config.getApp().setPrivateKeyPath("./src/test/resources/keys/private-key.pem");
    this.mockApiClient = new MockApiClient();
    ApiClientFactory factory = new ApiClientFactory(config);
    ApiClientFactory apiClientFactory = spy(factory);

    doReturn(mockApiClient.getApiClient("/pod")).when(apiClientFactory).getPodClient();
    doReturn(mockApiClient.getApiClient("/agent")).when(apiClientFactory).getAgentClient();
    doReturn(mockApiClient.getApiClient("/login")).when(apiClientFactory).getLoginClient();
    doReturn(mockApiClient.getApiClient("/relay")).when(apiClientFactory).getRelayClient();
    doReturn(mockApiClient.getApiClient("/sessionauth")).when(apiClientFactory).getSessionAuthClient();
    doReturn(mockApiClient.getApiClient("/keyauth")).when(apiClientFactory).getKeyAuthClient();

    this.mockApiClient.onPost(LOGIN_PUBKEY_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(RELAY_PUBKEY_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"keyManagerToken\" }");
    this.mockApiClient.onGet(V2_SESSION_INFO, JsonHelper.readFromClasspath("/res_response/bot_info.json"));

    this.symphonyBdk = new SymphonyBdk(config, apiClientFactory);
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
    this.mockApiClient.onPost(LOGIN_PUBKEY_APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");

    this.mockApiClient.onPost(LOGIN_PUBKEY_OBO_USERID_AUTHENTICATE.replace("{userId}", "123456"), "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    AuthSession authSessionById = this.symphonyBdk.obo(123456L);
    assertEquals(authSessionById.getSessionToken(), "1234");
    assertNull(authSessionById.getKeyManagerToken());

    this.mockApiClient.onPost(LOGIN_PUBKEY_OBO_USERNAME_AUTHENTICATE.replace("{username}", "username"), "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    AuthSession authSessionByUsername = this.symphonyBdk.obo("username");
    assertEquals(authSessionByUsername.getSessionToken(), "1234");
    assertNull(authSessionByUsername.getKeyManagerToken());
  }
}
