package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthSessionRsaImpl;
import com.symphony.bdk.core.client.loadbalancing.LoadBalancedApiClient;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.core.test.InMemoryDatafeedIdRepository;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.Datafeed;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4ConnectionAccepted;
import com.symphony.bdk.gen.api.model.V4ConnectionRequested;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4InstantMessageCreated;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4MessageSuppressed;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V4RoomCreated;
import com.symphony.bdk.gen.api.model.V4RoomDeactivated;
import com.symphony.bdk.gen.api.model.V4RoomMemberDemotedFromOwner;
import com.symphony.bdk.gen.api.model.V4RoomMemberPromotedToOwner;
import com.symphony.bdk.gen.api.model.V4RoomReactivated;
import com.symphony.bdk.gen.api.model.V4RoomUpdated;
import com.symphony.bdk.gen.api.model.V4SharedPost;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4User;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;
import com.symphony.bdk.gen.api.model.V4UserLeftRoom;
import com.symphony.bdk.gen.api.model.V4UserRequestedToJoinRoom;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ProcessingException;

class DatafeedLoopV1Test {

  public static final String DEFAULT_AGENT_BASE_PATH = "https://agent:8443/context";

  private DatafeedLoopV1 datafeedService;
  private BdkConfig bdkConfig;
  private DatafeedIdRepository datafeedIdRepository;
  private ApiClient datafeedApiClient;
  private DatafeedApi datafeedApi;
  private AuthSession authSession;
  private UserV2 botInfo;
  private RealTimeEventListener listener;

  @BeforeEach
  void init() throws BdkConfigException {
    initializeAuthSession();
    initializeBdkConfig();
    initializeBotInfo();

    //datafeed service
    this.datafeedIdRepository = new InMemoryDatafeedIdRepository(DEFAULT_AGENT_BASE_PATH);
    initializeDatafeedApiClient();
    initializeDatafeedApi();
    initializeDatafeedService();
  }

  private void initializeAuthSession() {
    this.authSession = Mockito.mock(AuthSessionRsaImpl.class);
    when(this.authSession.getSessionToken()).thenReturn("1234");
    when(this.authSession.getKeyManagerToken()).thenReturn("1234");
  }

  private void initializeBdkConfig() throws BdkConfigException {
    this.bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");

    BdkDatafeedConfig datafeedConfig = this.bdkConfig.getDatafeed();
    this.bdkConfig.setDatafeed(datafeedConfig);
    this.bdkConfig.setRetry(ofMinimalInterval(2));
  }

  private void initializeBotInfo() {
    this.botInfo = mock(UserV2.class);
    when(botInfo.getId()).thenReturn(1234L);
  }

  private void initializeDatafeedApiClient() {
    this.datafeedApiClient = mock(ApiClient.class);
    doNothing().when(this.datafeedApiClient).rotate();
    when(this.datafeedApiClient.getBasePath()).thenReturn(DEFAULT_AGENT_BASE_PATH + "/agent");
  }

  private void initializeDatafeedApi() {
    this.datafeedApi = mock(DatafeedApi.class);
    when(this.datafeedApi.getApiClient()).thenReturn(this.datafeedApiClient);
  }

  private void initializeDatafeedService() {
    this.datafeedService = new DatafeedLoopV1(
        this.datafeedApi,
        this.authSession,
        this.bdkConfig,
        this.botInfo,
        this.datafeedIdRepository
    );

    this.listener = new RealTimeEventListener() {
      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        datafeedService.stop();
      }
    };
    this.datafeedService.subscribe(listener);
  }

  private List<V4Event> getMessageSentEvent() {
    final V4Event event = new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())
        .initiator(new V4Initiator().user(new V4User().username("username").userId(123456789L)));
    return Collections.singletonList(event);
  }

  @Test
  void startTest() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenReturn(getMessageSentEvent());

    this.datafeedService.start();

    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);

    assertTrue(datafeedIdRepository.read().isPresent());
    assertEquals("test-id", datafeedIdRepository.read().get());
    assertTrue(datafeedIdRepository.readAgentBasePath().isPresent());
    assertEquals(DEFAULT_AGENT_BASE_PATH, datafeedIdRepository.readAgentBasePath().get());
  }

  @Test
  void testDatafeedIdIsReused() throws ApiException, AuthUnauthorizedException {
    datafeedIdRepository.write("persisted-id");
    initializeDatafeedService(); // datafeedId is read from repository in constructor

    when(datafeedApi.v4DatafeedIdReadGet("persisted-id", "1234", "1234", null))
        .thenReturn(getMessageSentEvent());

    this.datafeedService.start();

    verify(datafeedApi, times(0)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("persisted-id", "1234", "1234", null);
  }

  @Test
  void startTestWithLoadBalancing() throws ApiException, AuthUnauthorizedException {
    LoadBalancedApiClient loadBalancedApiClient = mock(LoadBalancedApiClient.class);
    doNothing().when(loadBalancedApiClient).rotate();
    doNothing().when(loadBalancedApiClient).setBasePath(any());
    when(loadBalancedApiClient.getBasePath()).thenReturn("https://agent-lb:7443/path/agent");
    this.datafeedApiClient = loadBalancedApiClient;

    initializeDatafeedApi();
    initializeDatafeedService();

    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenReturn(getMessageSentEvent());

    this.datafeedService.start();

    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);

    assertTrue(datafeedIdRepository.read().isPresent());
    assertEquals("test-id", datafeedIdRepository.read().get());
    assertTrue(datafeedIdRepository.readAgentBasePath().isPresent());
    assertEquals("https://agent-lb:7443/path", datafeedIdRepository.readAgentBasePath().get());
  }

  @Test
  void startTestWithLoadBalancingAndValidStoredDatafeed() throws ApiException, AuthUnauthorizedException {
    datafeedIdRepository.write("persisted-id", "persisted-agent-path");

    LoadBalancedApiClient loadBalancedApiClient = mock(LoadBalancedApiClient.class);
    doNothing().when(loadBalancedApiClient).rotate();
    doNothing().when(loadBalancedApiClient).setBasePath(any());
    when(loadBalancedApiClient.getBasePath()).thenReturn("https://agent-lb:7443/path/agent");
    this.datafeedApiClient = loadBalancedApiClient;

    initializeDatafeedApi();
    initializeDatafeedService();

    when(datafeedApi.v4DatafeedIdReadGet("persisted-id", "1234", "1234", null))
        .thenReturn(getMessageSentEvent());

    this.datafeedService.start();

    verify(datafeedApi, times(0)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("persisted-id", "1234", "1234", null);
    verify(loadBalancedApiClient, times(1)).setBasePath("persisted-agent-path");
  }

  @Test
  void startTestInvalidStoredDatafeedIsRecreated() throws ApiException, AuthUnauthorizedException {
    datafeedIdRepository.write("persisted-id");
    initializeDatafeedService(); // datafeedId is read from repository in constructor

    when(datafeedApi.v4DatafeedIdReadGet("persisted-id", "1234", "1234", null))
        .thenThrow(new ApiException(400, "expired DF id"));
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenReturn(getMessageSentEvent());

    this.datafeedService.start();

    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("persisted-id", "1234", "1234", null);
    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);
    verify(datafeedApiClient, times(1)).rotate();

    assertTrue(datafeedIdRepository.read().isPresent());
    assertEquals("test-id", datafeedIdRepository.read().get());
    assertTrue(datafeedIdRepository.readAgentBasePath().isPresent());
    assertEquals(DEFAULT_AGENT_BASE_PATH, datafeedIdRepository.readAgentBasePath().get());
  }

  @Test
  void startTestRecreateDatafeedError() throws ApiException {
    datafeedIdRepository.write("persisted-id");
    initializeDatafeedService(); // datafeedId is read from repository in constructor

    when(datafeedApi.v4DatafeedIdReadGet("persisted-id", "1234", "1234", null))
        .thenThrow(new ApiException(400, "expired DF id"));
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234"))
        .thenThrow(new ApiException(404, "unhandled exception"));

    assertThrows(NestedRetryException.class, () -> this.datafeedService.start());

    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("persisted-id", "1234", "1234", null);
    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void startTestWithRetryCreate() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenThrow(new ApiException(401, "test_unauthorized"));
    doNothing().when(authSession).refresh();

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(2)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void startTestWithRetryClientRead() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenThrow(new ApiException(400, "test_client_error"));
    doNothing().when(authSession).refresh();

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(3)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(2)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void startTestFailedAuthCreate() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenThrow(new ApiException(401, "test_unauthorized"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void startTestClientErrorCreate() throws ApiException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenThrow(new ApiException(400, "test_client_error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void startTestFailedAuthRead() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenThrow(new ApiException(401, "test_client_error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    verify(datafeedApi, times(1)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void startTestFailedSocketTimeoutRead() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenThrow(new ProcessingException(new SocketTimeoutException()));

    this.datafeedService.start();
    verify(datafeedApi, times(2)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void startServiceAlreadyStarted() throws ApiException, AuthUnauthorizedException {
    AtomicInteger signal = new AtomicInteger(0);
    when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
    when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
        .thenReturn(getMessageSentEvent());

    this.datafeedService.unsubscribe(this.listener);
    this.datafeedService.subscribe(new RealTimeEventListener() {
      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        try {
          datafeedService.start();
        } catch (AuthUnauthorizedException | ApiException ignored) {

        } catch (IllegalStateException e) {
          signal.incrementAndGet();
        } finally {
          datafeedService.stop();
        }
      }
    });
    this.datafeedService.start();
    assertEquals(signal.get(), 1);
  }

  @Test
  void retrieveDatafeedIdFromDatafeedDir(@TempDir Path tempDir) throws IOException {
    InputStream inputStream = DatafeedLoopV1Test.class.getResourceAsStream("/datafeed/datafeedId");
    Path datafeedFile = tempDir.resolve("datafeed.id");
    Files.copy(inputStream, datafeedFile);

    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setIdFilePath(tempDir.toString());
    bdkConfig.setDatafeed(datafeedConfig);

    Optional<String> datafeedId = new DatafeedLoopV1(this.datafeedApi, this.authSession, this.bdkConfig, this.botInfo)
        .retrieveDatafeed();

    assertTrue(datafeedId.isPresent());
    assertEquals(datafeedId.get(), "8e7c8672-220");
  }

  @Test
  void retrieveDatafeedIdFromDatafeedFile(@TempDir Path tempDir) throws IOException {
    InputStream inputStream = DatafeedLoopV1Test.class.getResourceAsStream("/datafeed/datafeedId");
    Path datafeedFile = tempDir.resolve("datafeed.id");
    Files.copy(inputStream, datafeedFile);

    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setIdFilePath(datafeedFile.toString());
    bdkConfig.setDatafeed(datafeedConfig);

    Optional<String> datafeedId =
        new DatafeedLoopV1(this.datafeedApi, this.authSession, this.bdkConfig, this.botInfo).retrieveDatafeed();
    assertTrue(datafeedId.isPresent());
    assertEquals(datafeedId.get(), "8e7c8672-220");
  }

  @Test
  void retrieveDatafeedIdFromInvalidDatafeedFile(@TempDir Path tempDir) throws IOException {
    Path datafeedFile = tempDir.resolve("datafeed.id");
    FileUtils.writeStringToFile(new File(String.valueOf(datafeedFile)), "8e7c8672-220", StandardCharsets.UTF_8);
    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setIdFilePath(datafeedFile.toString());
    bdkConfig.setDatafeed(datafeedConfig);

    Optional<String> datafeedId = this.datafeedService.retrieveDatafeed();
    assertFalse(datafeedId.isPresent());
  }

  @Test
  void retrieveDatafeedIdFromUnknownPath() {
    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setIdFilePath("unknown_path");
    bdkConfig.setDatafeed(datafeedConfig);

    Optional<String> datafeedId = this.datafeedService.retrieveDatafeed();
    assertFalse(datafeedId.isPresent());
  }

  @Test
  void retrieveDatafeedIdFromEmptyFile(@TempDir Path tempDir) {
    Path datafeedFile = tempDir.resolve("datafeed.id");
    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setIdFilePath(datafeedFile.toString());

    Optional<String> datafeedId = this.datafeedService.retrieveDatafeed();
    assertFalse(datafeedId.isPresent());
  }

  @Test
  void handleV4EventTest() {

    final String traceId = UUID.randomUUID().toString();

    final List<V4Event> events = new ArrayList<>();
    events.add(null);

    final RealTimeEventType[] types = RealTimeEventType.values();
    final V4Payload payload = new V4Payload()
        .messageSent(new V4MessageSent())
        .messageSuppressed(new V4MessageSuppressed())
        .symphonyElementsAction(new V4SymphonyElementsAction())
        .sharedPost(new V4SharedPost())
        .instantMessageCreated(new V4InstantMessageCreated())
        .roomCreated(new V4RoomCreated())
        .roomUpdated(new V4RoomUpdated())
        .roomDeactivated(new V4RoomDeactivated())
        .roomReactivated(new V4RoomReactivated())
        .connectionRequested(new V4ConnectionRequested())
        .connectionAccepted(new V4ConnectionAccepted())
        .roomMemberDemotedFromOwner(new V4RoomMemberDemotedFromOwner())
        .roomMemberPromotedToOwner(new V4RoomMemberPromotedToOwner())
        .userLeftRoom(new V4UserLeftRoom())
        .userJoinedRoom(new V4UserJoinedRoom())
        .userRequestedToJoinRoom(new V4UserRequestedToJoinRoom());

    final V4Initiator initiator = new V4Initiator().user(new V4User().username("username").userId(123456789L));
    for (RealTimeEventType type : types) {
      final V4Event event = new V4Event().type(type.name());
      event.id(traceId).payload(payload).initiator(initiator);
      events.add(event);
    }
    events.add(new V4Event().type("unknown-type").payload(payload));
    events.add(new V4Event().type(null));
    events.add(new V4Event().type(types[0].name()).initiator(
        new V4Initiator().user(
            new V4User().username(this.bdkConfig.getBot().getUsername()).userId(123456789L))
        )
    );

    this.datafeedService.unsubscribe(this.listener);

    final RealTimeEventListener listener = new RealTimeEventListener() {

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        assertThat(DistributedTracingContext.getTraceId()).isEqualTo(traceId);
        throw new RuntimeException("Let's try to break the DF loop!");
      }
    };

    final RealTimeEventListener spiedListener = Mockito.spy(listener);
    this.datafeedService.subscribe(spiedListener);
    this.datafeedService.handleV4EventList(events);

    verify(spiedListener).onMessageSent(initiator, payload.getMessageSent());
    verify(spiedListener).onMessageSuppressed(initiator, payload.getMessageSuppressed());
    verify(spiedListener).onSymphonyElementsAction(initiator, payload.getSymphonyElementsAction());
    verify(spiedListener).onSharedPost(initiator, payload.getSharedPost());
    verify(spiedListener).onInstantMessageCreated(initiator, payload.getInstantMessageCreated());
    verify(spiedListener).onRoomCreated(initiator, payload.getRoomCreated());
    verify(spiedListener).onRoomUpdated(initiator, payload.getRoomUpdated());
    verify(spiedListener).onRoomDeactivated(initiator, payload.getRoomDeactivated());
    verify(spiedListener).onRoomReactivated(initiator, payload.getRoomReactivated());
    verify(spiedListener).onConnectionRequested(initiator, payload.getConnectionRequested());
    verify(spiedListener).onConnectionAccepted(initiator, payload.getConnectionAccepted());
    verify(spiedListener).onRoomMemberDemotedFromOwner(initiator, payload.getRoomMemberDemotedFromOwner());
    verify(spiedListener).onRoomMemberPromotedToOwner(initiator, payload.getRoomMemberPromotedToOwner());
    verify(spiedListener).onUserLeftRoom(initiator, payload.getUserLeftRoom());
    verify(spiedListener).onUserJoinedRoom(initiator, payload.getUserJoinedRoom());
    verify(spiedListener).onUserRequestedToJoinRoom(initiator, payload.getUserRequestedToJoinRoom());
  }
}
