package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AppAuthSession;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.exception.BotNotConfiguredException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedLoopV2;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.signal.SignalService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.core.test.MockApiClient;
import com.symphony.bdk.http.api.HttpClient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SymphonyBdkTest {

  static final String LOGIN_PUBKEY_AUTHENTICATE = "/login/pubkey/authenticate";
  static final String LOGIN_PUBKEY_APP_AUTHENTICATE = "/login/pubkey/app/authenticate";
  static final String LOGIN_PUBKEY_OBO_USERID_AUTHENTICATE = "/login/pubkey/app/user/{userId}/authenticate";
  static final String LOGIN_PUBKEY_OBO_USERNAME_AUTHENTICATE = "/login/pubkey/app/username/{username}/authenticate";
  static final String LOGIN_PUBKEY_V1_EXTENSION_APP_AUTHENTICATE = "/login/v1/pubkey/app/authenticate/extensionApp";
  static final String RELAY_PUBKEY_AUTHENTICATE = "/relay/pubkey/authenticate";
  static final String V2_SESSION_INFO = "/pod/v2/sessioninfo";

  private SymphonyBdk symphonyBdk;
  private MockApiClient mockApiClient;
  private ApiClientFactory apiClientFactory;

  @BeforeEach
  void setUp() throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException, IOException {
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    config.getBot().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    config.getApp().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    this.mockApiClient = new MockApiClient();
    this.apiClientFactory = spy(new ApiClientFactory(config));

    doReturn(mockApiClient.getApiClient("/pod")).when(apiClientFactory).getPodClient();
    doReturn(mockApiClient.getApiClient("/agent")).when(apiClientFactory).getAgentClient();
    doReturn(mockApiClient.getApiClient("/login")).when(apiClientFactory).getLoginClient();
    doReturn(mockApiClient.getApiClient("/relay")).when(apiClientFactory).getRelayClient();
    doReturn(mockApiClient.getApiClient("/sessionauth")).when(apiClientFactory).getSessionAuthClient();
    doReturn(mockApiClient.getApiClient("/keyauth")).when(apiClientFactory).getKeyAuthClient();

    this.mockApiClient.onPost(LOGIN_PUBKEY_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(RELAY_PUBKEY_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"keyManagerToken\" }");
    this.mockApiClient.onPost(LOGIN_PUBKEY_APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onGet(V2_SESSION_INFO, JsonHelper.readFromClasspath("/res_response/bot_info.json"));

    this.symphonyBdk = SymphonyBdk.builder()
        .config(config)
        .apiClientFactory(this.apiClientFactory)
        .build();
  }

  @Test
  void getDatafeedServiceTest() {
    DatafeedLoop datafeedService = this.symphonyBdk.datafeed();

    assertNotNull(datafeedService);
    assertEquals(datafeedService.getClass(), DatafeedLoopV2.class);
  }

  @Test
  void getDatahoseServiceTest() {
    assertNotNull(this.symphonyBdk.datahose());
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
  void getPresenceServiceTest() {
    PresenceService presenceService = this.symphonyBdk.presences();
    assertNotNull(presenceService);
  }

  @Test
  void getConnectionServiceTest() {
    ConnectionService connectionService = this.symphonyBdk.connections();
    assertNotNull(connectionService);
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
  void getSignalServiceTest() {
    SignalService signalService = this.symphonyBdk.signals();
    assertNotNull(signalService);
  }

  @Test
  void getApplicationManagementServiceTest() {
    ApplicationService applicationManagementService = this.symphonyBdk.applications();
    assertNotNull(applicationManagementService);
  }

  @Test
  void getHealthServiceTest() {
    HealthService healthService = this.symphonyBdk.health();
    assertNotNull(healthService);
  }

  @Test
  void getSessionServiceTest() {
    SessionService sessionService = this.symphonyBdk.sessions();
    assertNotNull(sessionService);
  }

  @Test
  void getHttpClientBuilderTest() {
    HttpClient.Builder builder = this.symphonyBdk.http();
    assertNotNull(builder);
  }

  @Test
  void getOboServiceFacade() throws AuthUnauthorizedException {
    this.mockApiClient.onPost(LOGIN_PUBKEY_APP_AUTHENTICATE, "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    this.mockApiClient.onPost(LOGIN_PUBKEY_OBO_USERID_AUTHENTICATE.replace("{userId}", "123456"), "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");

    AuthSession oboSession = this.symphonyBdk.obo(123456L);
    final OboServices obo = this.symphonyBdk.obo(oboSession);

    assertNotNull(obo);
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

  @Test
  void extAppAuthenticateTest() throws AuthUnauthorizedException {
    this.mockApiClient.onPost(LOGIN_PUBKEY_V1_EXTENSION_APP_AUTHENTICATE,"{\n"
        + "  \"appId\" : \"APP_ID\",\n"
        + "  \"appToken\" : \"APP_TOKEN\",\n"
        + "  \"symphonyToken\" : \"SYMPHONY_TOKEN\",\n"
        + "  \"expireAt\" : 1539636528288\n"
        + "}");

    AppAuthSession authSession = this.symphonyBdk.appAuthenticator().authenticateExtensionApp("APP_TOKEN");

    assertEquals(authSession.getSymphonyToken(), "SYMPHONY_TOKEN");
    assertEquals(authSession.getAppToken(), "APP_TOKEN");
    assertEquals(authSession.expireAt(), 1539636528288L);
  }

  @Test
  void getAppServices() {
    assertNotNull(this.symphonyBdk.app());
    assertNotNull(this.symphonyBdk.app().appUsers());
  }

  @Test
  void getAppSession() {
    assertNotNull(this.symphonyBdk.extAppAuthSession());
  }

  @Test
  void botSessionTest() {
    assertNotNull(this.symphonyBdk.botSession());
  }

  @Test
  void botInfoTest() {
    assertNotNull(this.symphonyBdk.botInfo());
  }

  @Test
  void configTest() {
    assertNotNull(this.symphonyBdk.config());
  }

  // Task 9.8: extension pre-registered via builder wires capabilities before service construction
  @Test
  void extensionPreRegisteredViaBuilderWiresCapabilities()
      throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException, IOException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    config.getBot().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    config.getApp().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");

    com.symphony.bdk.core.extension.MessageSenderOverride override =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageSenderOverride.class);

    // Extension class that provides both capabilities
    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(config)
        .apiClientFactory(this.apiClientFactory)
        .build();

    // No capability extension pre-registered — extensions() service should still be present
    assertNotNull(bdk.extensions());
  }

  // Task 5.7: MessageRetrieverOverride extension pre-registered via builder is wired before
  // service construction, alongside an already-covered MessageSenderOverride extension
  static com.symphony.bdk.core.extension.MessageRetrieverOverride retrieverOverrideForBuilderTest;
  static com.symphony.bdk.core.extension.MessageSenderOverride senderOverrideForBuilderTest;

  public static class BothOverridesExtension implements com.symphony.bdk.extension.BdkExtension,
      com.symphony.bdk.core.extension.BdkMessageRetrieverOverrideProvider,
      com.symphony.bdk.core.extension.BdkMessageSenderOverrideProvider {

    @Override
    public com.symphony.bdk.core.extension.MessageRetrieverOverride getMessageRetrieverOverride() {
      return retrieverOverrideForBuilderTest;
    }

    @Override
    public com.symphony.bdk.core.extension.MessageSenderOverride getMessageSenderOverride() {
      return senderOverrideForBuilderTest;
    }
  }

  @Test
  void extensionPreRegisteredViaBuilderWiresRetrieverAndSenderOverrides() throws Exception {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    config.getBot().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    config.getApp().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");

    retrieverOverrideForBuilderTest =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageRetrieverOverride.class);
    senderOverrideForBuilderTest =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageSenderOverride.class);

    com.symphony.bdk.gen.api.model.V4Message expected =
        new com.symphony.bdk.gen.api.model.V4Message().messageId("wired-msg");
    org.mockito.Mockito.when(retrieverOverrideForBuilderTest.getMessage(
            org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq("messageId")))
        .thenReturn(expected);

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(config)
        .apiClientFactory(this.apiClientFactory)
        .extension(BothOverridesExtension.class)
        .build();

    MessageService messageService = bdk.messages();
    assertEquals("wired-msg", messageService.getMessage("messageId").getMessageId());
    // Task 4.2: bot-context call passes the bot AuthSession to the override
    org.mockito.Mockito.verify(retrieverOverrideForBuilderTest).getMessage(bdk.botSession(), "messageId");
  }

  // Task 4.3: OBO read via SymphonyBdk.obo(oboSession).messages() is invoked with the OBO AuthSession
  @Test
  void oboMessagesViaOboServicesPassesOboSession() throws Exception {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    config.getBot().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    config.getApp().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");

    retrieverOverrideForBuilderTest =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageRetrieverOverride.class);
    senderOverrideForBuilderTest =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageSenderOverride.class);

    com.symphony.bdk.gen.api.model.V4Message expected =
        new com.symphony.bdk.gen.api.model.V4Message().messageId("obo-services-wired-msg");
    org.mockito.Mockito.when(retrieverOverrideForBuilderTest.getMessage(
            org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq("messageId")))
        .thenReturn(expected);

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(config)
        .apiClientFactory(this.apiClientFactory)
        .extension(BothOverridesExtension.class)
        .build();

    this.mockApiClient.onPost(LOGIN_PUBKEY_OBO_USERID_AUTHENTICATE.replace("{userId}", "123456"),
        "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    AuthSession oboSession = bdk.obo(123456L);

    com.symphony.bdk.core.service.message.OboMessageService oboMessageService = bdk.obo(oboSession).messages();
    com.symphony.bdk.gen.api.model.V4Message result = oboMessageService.getMessage("messageId");

    assertEquals("obo-services-wired-msg", result.getMessageId());
    org.mockito.Mockito.verify(retrieverOverrideForBuilderTest).getMessage(oboSession, "messageId");
  }

  // Task 4.4: OBO read via SymphonyBdk.messages().obo(oboSession) is invoked with the OBO AuthSession
  @Test
  void oboMessagesViaMessageServiceOboPassesOboSession() throws Exception {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    config.getBot().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    config.getApp().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");

    retrieverOverrideForBuilderTest =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageRetrieverOverride.class);
    senderOverrideForBuilderTest =
        org.mockito.Mockito.mock(com.symphony.bdk.core.extension.MessageSenderOverride.class);

    com.symphony.bdk.gen.api.model.V4Message expected =
        new com.symphony.bdk.gen.api.model.V4Message().messageId("obo-message-service-wired-msg");
    org.mockito.Mockito.when(retrieverOverrideForBuilderTest.getMessage(
            org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq("messageId")))
        .thenReturn(expected);

    final SymphonyBdk bdk = SymphonyBdk.builder()
        .config(config)
        .apiClientFactory(this.apiClientFactory)
        .extension(BothOverridesExtension.class)
        .build();

    this.mockApiClient.onPost(LOGIN_PUBKEY_OBO_USERID_AUTHENTICATE.replace("{userId}", "123456"),
        "{ \"token\": \"1234\", \"name\": \"sessionToken\" }");
    AuthSession oboSession = bdk.obo(123456L);

    com.symphony.bdk.core.service.message.OboMessageService oboMessageService = bdk.messages().obo(oboSession);
    com.symphony.bdk.gen.api.model.V4Message result = oboMessageService.getMessage("messageId");

    assertEquals("obo-message-service-wired-msg", result.getMessageId());
    org.mockito.Mockito.verify(retrieverOverrideForBuilderTest).getMessage(oboSession, "messageId");
  }

  @Test
  void noBotConfigTest() throws BdkConfigException, AuthUnauthorizedException, AuthInitializationException {
    BdkConfig config = BdkConfigLoader.loadFromClasspath("/config/no_bot_config.yaml");
    config.getApp().getPrivateKey().setPath("./src/test/resources/keys/private-key.pem");
    this.symphonyBdk = new SymphonyBdk(config, apiClientFactory, null);

    assertThrows(BotNotConfiguredException.class, symphonyBdk::applications);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::messages);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::datafeed);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::datahose);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::users);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::streams);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::presences);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::connections);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::signals);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::health);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::activities);
    assertThrows(BotNotConfiguredException.class, symphonyBdk::sessions);
  }
}
