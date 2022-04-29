package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthSessionImpl;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.AckId;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V5Datafeed;
import com.symphony.bdk.gen.api.model.V5DatafeedCreateBody;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ProcessingException;

class DatafeedLoopV2Test {

  private static final String DATAFEED_ID = "abc_f_def";
  private static final String TOKEN = "1234";
  private static final String USERNAME = "tibot";

  private DatafeedLoopV2 datafeedService;
  private ApiClient datafeedApiClient;
  private DatafeedApi datafeedApi;
  private AuthSession authSession;
  private UserV2 botInfo;
  private RealTimeEventListener listener;

  @BeforeEach
  void setUp() throws BdkConfigException {
    BdkConfig bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setVersion("v2");
    datafeedConfig.setRetry(ofMinimalInterval(2));
    bdkConfig.setDatafeed(datafeedConfig);
    bdkConfig.setRetry(ofMinimalInterval(2));

    this.botInfo = Mockito.mock(UserV2.class);
    this.authSession = Mockito.mock(AuthSessionImpl.class);
    when(this.authSession.getSessionToken()).thenReturn(TOKEN);
    when(this.authSession.getKeyManagerToken()).thenReturn(TOKEN);

    this.datafeedApiClient = mock(ApiClient.class);
    when(this.datafeedApiClient.getBasePath()).thenReturn("/agent/");

    this.datafeedApi = mock(DatafeedApi.class);
    when(this.datafeedApi.getApiClient()).thenReturn(this.datafeedApiClient);

    this.datafeedService = new DatafeedLoopV2(
        this.datafeedApi,
        this.authSession,
        bdkConfig,
        botInfo
    );
    this.listener = new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        datafeedService.stop();
      }
    };
    this.datafeedService.subscribe(listener);
  }

  @Test
  void testStart() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @ParameterizedTest
  @ValueSource(strings = {"abc", "abc_def", "abc_p_def", "abc_f", "abc_f_"})
  void testStartInvalidExistingFeeds(String invalidExistingFeedId) throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME))
        .thenReturn(Collections.singletonList(new V5Datafeed().id(invalidExistingFeedId)));
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id(DATAFEED_ID));

    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).createDatafeed(eq(TOKEN), eq(TOKEN), any());
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStartMultiThreaded() throws ApiException, InterruptedException,
      ExecutionException {
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id(DATAFEED_ID));
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(datafeeds);
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId(""))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"))
        .thenReturn(null); // the first df loop run should not fail
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId("ack-id"))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id2"));
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId("ack-id2"))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id2"));

    // remove default listener that stops the DF loop
    datafeedService.unsubscribe(listener);

    CountDownLatch blockDispatchingToListeners = new CountDownLatch(1);
    CountDownLatch blockNewListenerIsAdded = new CountDownLatch(1);
    datafeedService.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        blockDispatchingToListeners.countDown();
        try {
          blockNewListenerIsAdded.await();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        return false;
      }
    });

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Future<Void> datafeedLoop = executorService.submit(() -> {
      datafeedService.start();
      return null;
    });

    // we wait until the DF loop is blocked in event dispatching
    blockDispatchingToListeners.await();

    // we try to add a new listener that will cause the DF loop to run twice and then to end
    Future<Void> addANewListener = executorService.submit(() -> {
      datafeedService.subscribe(new RealTimeEventListener() {
        @Override
        public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
          // once the listener is added it will stop the DF loop
          if (datafeedService.getAckId().equals("ack-id2")) {
            datafeedService.stop();
          }
          return false;
        }
      });
      return null;
    });

    // we want to make sure the addANewListener is started and blocking before unblocking the first listener
    Thread.sleep(100);
    blockNewListenerIsAdded.countDown();

    // make sure DF loop ends without error (otherwise get() throws an exception)
    datafeedLoop.get();
    addANewListener.get();

    // make sure we finish with the proper ack id
    assertEquals("ack-id2", datafeedService.getAckId());

    executorService.shutdown();
  }

  @Test
  void testStartListenerFails() throws ApiException, AuthUnauthorizedException {
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id(DATAFEED_ID));
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(datafeeds);
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN),
        argThat(eqAckId(""))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN),
        argThat(eqAckId("ack-id"))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id2"));

    this.datafeedService.unsubscribe(listener);
    AtomicBoolean firstCall = new AtomicBoolean(true);
    this.datafeedService.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        if (firstCall.get()) {
          firstCall.set(false);
          // will still update ack id
          throw new RuntimeException("failure");
        } else {
          datafeedService.stop();
        }
      }
    });
    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    // the ack id will still change because exception is silently caught
    verify(datafeedApi, times(1)).readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN),
        argThat(eqAckId("")));
    verify(datafeedApi, times(1)).readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN),
        argThat(eqAckId("ack-id")));
    assertEquals("ack-id2", datafeedService.getAckId());
  }

  @Test
  void testStartListenerFails_requeueEvent() throws ApiException, AuthUnauthorizedException {
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id(DATAFEED_ID));
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(datafeeds);
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId(""))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
    when(datafeedApi.readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId("ack-id"))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id2"));

    this.datafeedService.unsubscribe(listener);
    AtomicBoolean firstCall = new AtomicBoolean(true);
    this.datafeedService.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        if (firstCall.get()) {
          firstCall.set(false);
          // will not update ack id
          throw new EventException("failure");
        } else {
          datafeedService.stop();
        }
      }
    });
    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    // the ack id should stay the same since we did not process the first event
    verify(datafeedApi, times(2)).readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId("")));
    verify(datafeedApi, never()).readDatafeed(eq(DATAFEED_ID), eq(TOKEN), eq(TOKEN), argThat(eqAckId("ack-id2")));
    assertEquals("ack-id", datafeedService.getAckId());
  }

  private ArgumentMatcher<AckId> eqAckId(String ackId) {
    return argument -> argument.getAckId().equals(ackId);
  }

  @Test
  void testStartTagIsNotTooLong() throws ApiException, AuthUnauthorizedException, BdkConfigException {
    BdkConfig bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
    datafeedConfig.setVersion("v2");
    bdkConfig.setDatafeed(datafeedConfig);
    bdkConfig.setRetry(ofMinimalInterval(2));
    // set a super long bot's username, tag should be shorter
    bdkConfig.getBot().setUsername(StringUtils.repeat('a', 200));

    DatafeedLoopV2 customConfigService = new DatafeedLoopV2(
        this.datafeedApi,
        this.authSession,
        bdkConfig,
        botInfo
    );
    customConfigService.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        customConfigService.stop();
      }
    });

    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id(DATAFEED_ID));
    when(datafeedApi.listDatafeed(anyString(), anyString(), anyString())).thenReturn(datafeeds);
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    customConfigService.start();

    verify(datafeedApi, times(1)).listDatafeed(anyString(), anyString(), eq(StringUtils.repeat('a', 100)));
  }

  @Test
  void testStartEmptyListDatafeed() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id(DATAFEED_ID));
    AckId initialAckId = new AckId().ackId("");
    final String secondAckId = "ack-id";
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, initialAckId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId(secondAckId));

    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME));
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, initialAckId);
    assertEquals(secondAckId, datafeedService.getAckId());
  }

  @Test
  void testClientErrorTriggersDatafeedRecreation() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));

    AckId initialAckId = new AckId().ackId("");
    String secondDatafeedId = "qwe_f_tyu";
    String secondAckId = "ack-id";

    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, initialAckId))
        .thenReturn(new V5EventList().addEventsItem(new V4Event().type(RealTimeEventType.ROOMCREATED.name())
            .payload(new V4Payload())).ackId(secondAckId));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, new AckId().ackId(secondAckId)))
        .thenThrow(new ApiException(400, ""));
    when(datafeedApi.deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN)).thenReturn(null);

    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id(secondDatafeedId));
    when(datafeedApi.readDatafeed(secondDatafeedId, TOKEN, TOKEN, initialAckId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id-2"));

    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, initialAckId);
    verify(datafeedApi, times(1)).deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN);
    verify(datafeedApi, times(1)).createDatafeed(eq(TOKEN), eq(TOKEN), any());
    verify(datafeedApi, times(1)).readDatafeed(secondDatafeedId, TOKEN, TOKEN, initialAckId);
  }

  @Test
  void testStartServiceAlreadyStarted() throws ApiException, AuthUnauthorizedException {
    AtomicInteger signal = new AtomicInteger(0);
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id(DATAFEED_ID));
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(datafeeds);
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList()
            .addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload()))
            .ackId("ack-id"));

    this.datafeedService.unsubscribe(this.listener);
    this.datafeedService.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        try {
          datafeedService.start();
        } catch (AuthUnauthorizedException | ApiException e) {
          e.printStackTrace();
        } catch (IllegalStateException e) {
          signal.incrementAndGet();
        } finally {
          datafeedService.stop();
        }
      }
    });
    this.datafeedService.start();
    assertEquals(1, signal.get());
  }

  @Test
  void testStartClientErrorListDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenThrow(new ApiException(400, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
  }

  @Test
  void testStartAuthRefreshListDatafeed() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenThrow(new ApiException(401, "unauthorized-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(authSession, times(1)).refresh();
  }

  @Test
  void testStartServerErrorListDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenThrow(new ApiException(502, "server-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(2)).listDatafeed(TOKEN, TOKEN, USERNAME);
  }

  @Test
  void testStartErrorListDatafeedThenRetrySuccess() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME))
        .thenThrow(new ApiException(502, "server-error"))
        .thenReturn(Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));

    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();

    verify(datafeedApi, times(2)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStartAuthErrorCreateDatafeed() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenThrow(
        new ApiException(401, "unauthorized-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME));
  }

  @Test
  void testStartClientErrorCreateDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenThrow(
        new ApiException(400, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME));
  }

  @Test
  void testStartServerErrorCreateDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenThrow(
        new ApiException(502, "server-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(2)).createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME));
  }

  @Test
  void testStartClientErrorReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.readDatafeed("recreate-df-id", TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
    verify(datafeedApi, times(1)).readDatafeed("recreate-df-id", TOKEN, TOKEN, ackId);
    verify(datafeedApi, times(1)).createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME));
    verify(datafeedApi, times(1)).deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN);
  }

  @Test
  void testStartSocketTimeoutReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(
        new ProcessingException(new SocketTimeoutException()));

    ApiClient client = mock(ApiClient.class);
    when(datafeedApi.getApiClient()).thenReturn(client);
    when(client.getBasePath()).thenReturn("path/to/the/agent");

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(2)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStartUnknownHostReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(
        new ProcessingException(new UnknownHostException()));

    ApiClient client = mock(ApiClient.class);
    when(datafeedApi.getApiClient()).thenReturn(client);
    when(client.getBasePath()).thenReturn("path/to/the/agent");

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(2)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStartAuthErrorReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(401, "client-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
    verify(authSession, times(1)).refresh();
  }

  @Test
  void testStartServerErrorReadDatafeed() throws ApiException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(502, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(2)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStart404ErrorReadDatafeedShouldNotBeRetried() throws ApiException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(404, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStartTooManyRequestsReadDatafeedShouldBeRetried() throws ApiException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(
        new ApiException(429, "too-many-requests"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(2)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
  }

  @Test
  void testStartClientErrorDeleteDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.readDatafeed("recreate-df-id", TOKEN, TOKEN, ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
    when(datafeedApi.deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN)).thenThrow(new ApiException(400, "client-error"));

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
    verify(datafeedApi, times(1)).readDatafeed("recreate-df-id", TOKEN, TOKEN, ackId);
    verify(datafeedApi, times(1)).createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME));
    verify(datafeedApi, times(1)).deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN);
  }

  @Test
  void testStartServerErrorDeleteDatafeed() throws ApiException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN)).thenThrow(new ApiException(502, "client-error"));

    assertThrows(NestedRetryException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
    verify(datafeedApi, times(2)).deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN);
  }

  @Test
  void testStartAuthErrorDeleteDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = new AckId().ackId(datafeedService.getAckId());
    when(datafeedApi.listDatafeed(TOKEN, TOKEN, USERNAME)).thenReturn(
        Collections.singletonList(new V5Datafeed().id(DATAFEED_ID)));
    when(datafeedApi.createDatafeed(TOKEN, TOKEN, new V5DatafeedCreateBody().tag(USERNAME))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN)).thenThrow(new ApiException(401, "client-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(NestedRetryException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed(TOKEN, TOKEN, USERNAME);
    verify(datafeedApi, times(1)).readDatafeed(DATAFEED_ID, TOKEN, TOKEN, ackId);
    verify(datafeedApi, times(1)).deleteDatafeed(DATAFEED_ID, TOKEN, TOKEN);
  }
}
