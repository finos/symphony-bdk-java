package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthSessionRsaImpl;
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
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
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
    bdkConfig.setDatafeed(datafeedConfig);
    bdkConfig.setRetry(ofMinimalInterval(2));

    this.botInfo = Mockito.mock(UserV2.class);
    this.authSession = Mockito.mock(AuthSessionRsaImpl.class);
    when(this.authSession.getSessionToken()).thenReturn("1234");
    when(this.authSession.getKeyManagerToken()).thenReturn("1234");

    this.datafeedApiClient = mock(ApiClient.class);
    doNothing().when(this.datafeedApiClient).rotate();

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
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id("test-id"));
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(datafeeds);
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(0)).rotate();
  }

  @Test
  void testStartMultiThreaded() throws ApiException, InterruptedException,
      ExecutionException {
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id("test-id"));
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(datafeeds);
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId(""))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"))
        .thenReturn(null); // the first df loop run should not fail
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId("ack-id"))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id2"));
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId("ack-id2"))))
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
          if (datafeedService.getAckId().getAckId().equals("ack-id2")) {
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
    assertEquals("ack-id2", datafeedService.getAckId().getAckId());

    executorService.shutdown();
  }

  @Test
  void testStartListenerFails() throws ApiException, AuthUnauthorizedException {
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id("test-id"));
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(datafeeds);
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"),
        argThat(eqAckId(""))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"),
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

    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    // the ack id will still change because exception is silently caught
    verify(datafeedApi, times(1)).readDatafeed(eq("test-id"), eq("1234"), eq("1234"),
        argThat(eqAckId("")));
    verify(datafeedApi, times(1)).readDatafeed(eq("test-id"), eq("1234"), eq("1234"),
        argThat(eqAckId("ack-id")));
    verify(datafeedApiClient, times(0)).rotate();
    assertEquals("ack-id2", datafeedService.getAckId().getAckId());
  }

  @Test
  void testStartListenerFails_requeueEvent() throws ApiException, AuthUnauthorizedException {
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id("test-id"));
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(datafeeds);
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId(""))))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
    when(datafeedApi.readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId("ack-id"))))
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

    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    // the ack id should stay the same since we did not process the first event
    verify(datafeedApi, times(2)).readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId("")));
    verify(datafeedApi, never()).readDatafeed(eq("test-id"), eq("1234"), eq("1234"), argThat(eqAckId("ack-id2")));
    verify(datafeedApiClient, times(0)).rotate();
    assertEquals("ack-id", datafeedService.getAckId().getAckId());
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
    datafeeds.add(new V5Datafeed().id("test-id"));
    when(datafeedApi.listDatafeed(anyString(), anyString(), anyString())).thenReturn(datafeeds);
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    customConfigService.start();

    verify(datafeedApi, times(1)).listDatafeed(anyString(), anyString(), eq(StringUtils.repeat('a', 100)));
  }

  @Test
  void testStartEmptyListDatafeed() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenReturn(
        new V5Datafeed().id("test-id"));
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();

    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"));
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(0)).rotate();
  }

  @Test
  void testStartServiceAlreadyStarted() throws ApiException, AuthUnauthorizedException {
    AtomicInteger signal = new AtomicInteger(0);
    List<V5Datafeed> datafeeds = new ArrayList<>();
    datafeeds.add(new V5Datafeed().id("test-id"));
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(datafeeds);
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
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
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenThrow(new ApiException(400, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartAuthRefreshListDatafeed() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenThrow(new ApiException(401, "unauthorized-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(authSession, times(1)).refresh();
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartServerErrorListDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenThrow(new ApiException(502, "server-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(2)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void testStartErrorListDatafeedThenRetrySuccess() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot"))
        .thenThrow(new ApiException(502, "server-error"))
        .thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));

    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();

    verify(datafeedApi, times(2)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartAuthErrorCreateDatafeed() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenThrow(
        new ApiException(401, "unauthorized-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"));
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartClientErrorCreateDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenThrow(
        new ApiException(400, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"));
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartServerErrorCreateDatafeed() throws ApiException {
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(Collections.emptyList());
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenThrow(
        new ApiException(502, "server-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(2)).createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"));
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void testStartClientErrorReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.readDatafeed("recreate-df-id", "1234", "1234", ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApi, times(1)).readDatafeed("recreate-df-id", "1234", "1234", ackId);
    verify(datafeedApi, times(1)).createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"));
    verify(datafeedApi, times(1)).deleteDatafeed("test-id", "1234", "1234");
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartSocketTimeoutReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(
        new ProcessingException(new SocketTimeoutException()));

    ApiClient client = mock(ApiClient.class);
    when(datafeedApi.getApiClient()).thenReturn(client);
    when(client.getBasePath()).thenReturn("path/to/the/agent");

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(2)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void testStartAuthErrorReadDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(401, "client-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(authSession, times(1)).refresh();
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartServerErrorReadDatafeed() throws ApiException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(502, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(2)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void testStartInternalServerErrorReadDatafeedShouldNotBeRetried() throws ApiException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(404, "client-error"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartTooManyRequestsReadDatafeedShouldBeRetried() throws ApiException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(
        new ApiException(429, "too-many-requests"));

    assertThrows(ApiException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(2)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApiClient, times(2)).rotate();
  }

  @Test
  void testStartClientErrorDeleteDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.readDatafeed("recreate-df-id", "1234", "1234", ackId))
        .thenReturn(new V5EventList().addEventsItem(
            new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
    when(datafeedApi.deleteDatafeed("test-id", "1234", "1234")).thenThrow(new ApiException(400, "client-error"));

    this.datafeedService.start();
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApi, times(1)).readDatafeed("recreate-df-id", "1234", "1234", ackId);
    verify(datafeedApi, times(1)).createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"));
    verify(datafeedApi, times(1)).deleteDatafeed("test-id", "1234", "1234");
    verify(datafeedApiClient, times(1)).rotate();
  }

  @Test
  void testStartServerErrorDeleteDatafeed() throws ApiException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.deleteDatafeed("test-id", "1234", "1234")).thenThrow(new ApiException(502, "client-error"));

    assertThrows(NestedRetryException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApi, times(2)).deleteDatafeed("test-id", "1234", "1234");
    verify(datafeedApiClient, times(3)).rotate();
  }

  @Test
  void testStartAuthErrorDeleteDatafeed() throws ApiException, AuthUnauthorizedException {
    AckId ackId = datafeedService.getAckId();
    when(datafeedApi.listDatafeed("1234", "1234", "tibot")).thenReturn(
        Collections.singletonList(new V5Datafeed().id("test-id")));
    when(datafeedApi.createDatafeed("1234", "1234", new V5DatafeedCreateBody().tag("tibot"))).thenReturn(
        new V5Datafeed().id("recreate-df-id"));
    when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
    when(datafeedApi.deleteDatafeed("test-id", "1234", "1234")).thenThrow(new ApiException(401, "client-error"));
    doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

    assertThrows(NestedRetryException.class, this.datafeedService::start);
    verify(datafeedApi, times(1)).listDatafeed("1234", "1234", "tibot");
    verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    verify(datafeedApi, times(1)).deleteDatafeed("test-id", "1234", "1234");
    verify(datafeedApiClient, times(2)).rotate();
  }
}
