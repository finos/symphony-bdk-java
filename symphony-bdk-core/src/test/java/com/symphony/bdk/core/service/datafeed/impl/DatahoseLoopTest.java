package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthSessionImpl;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatahoseConfig;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V4RoomCreated;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.gen.api.model.V5EventsReadBody;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

class DatahoseLoopTest {

  private DatahoseLoopImpl datahoseLoop;
  private ApiClient apiClient;
  private DatafeedApi datafeedApi;
  private AuthSession authSession;
  private BdkConfig bdkConfig;
  private UserV2 botInfo;
  private RealTimeEventListener listener;
  private String tag;
  private List<String> eventTypes;

  @BeforeEach
  void setUp() throws BdkConfigException {

    this.eventTypes = Arrays.asList("TYPE_A", "TYPE_B");
    this.tag = "mytag";

    this.botInfo = Mockito.mock(UserV2.class);
    this.authSession = Mockito.mock(AuthSessionImpl.class);
    when(this.authSession.getSessionToken()).thenReturn("1234");
    when(this.authSession.getKeyManagerToken()).thenReturn("1234");

    this.apiClient = mock(ApiClient.class);
    when(this.apiClient.getBasePath()).thenReturn("/agent/");

    this.datafeedApi = mock(DatafeedApi.class);
    when(this.datafeedApi.getApiClient()).thenReturn(this.apiClient);

    this.bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    this.bdkConfig.setRetry(ofMinimalInterval(2));

    BdkDatahoseConfig datahoseConfig = bdkConfig.getDatahose();
    datahoseConfig.setEventTypes(this.eventTypes);
    datahoseConfig.setTag(this.tag);
    datahoseConfig.setRetry(ofMinimalInterval(2));

    this.datahoseLoop = new DatahoseLoopImpl(
        this.datafeedApi,
        this.authSession,
        this.bdkConfig,
        this.botInfo
    );
    this.listener = new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        datahoseLoop.stop();
      }
    };
    this.datahoseLoop.subscribe(listener);
  }

  @Test
  void testStart() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.readEvents(any(), any(), any())).thenReturn(
        buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(1)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getValue(), tag, "");
  }

  @Test
  void testDefaultTagValue() throws ApiException, AuthUnauthorizedException {
    tag = "";
    bdkConfig.getDatahose().setTag(tag);
    when(botInfo.getUsername()).thenReturn("username");

    datahoseLoop = new DatahoseLoopImpl(
        datafeedApi,
        authSession,
        bdkConfig,
        botInfo
    );
    datahoseLoop.subscribe(listener);

    when(datafeedApi.readEvents(any(), any(), any())).thenReturn(
        buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(1)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getValue(), "datahose-username", "");
  }

  @Test
  void testTagIsUsed() throws ApiException, AuthUnauthorizedException {
    tag = StringUtils.repeat('a', 80);
    bdkConfig.getDatahose().setTag(tag);

    datahoseLoop = new DatahoseLoopImpl(
        datafeedApi,
        authSession,
        bdkConfig,
        botInfo
    );
    datahoseLoop.subscribe(listener);

    when(datafeedApi.readEvents(any(), any(), any())).thenReturn(
        buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(1)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getValue(), tag, "");
  }

  @Test
  void testLongTagIsTruncated() throws ApiException, AuthUnauthorizedException {
    tag = StringUtils.repeat('a', 100);
    bdkConfig.getDatahose().setTag(tag);

    datahoseLoop = new DatahoseLoopImpl(
        datafeedApi,
        authSession,
        bdkConfig,
        botInfo
    );
    datahoseLoop.subscribe(listener);

    when(datafeedApi.readEvents(any(), any(), any())).thenReturn(
        buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(1)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getValue(), tag.substring(0, 80), "");
  }

  @Test
  void testAckIdIsReused() throws ApiException, AuthUnauthorizedException {
    final String ackId = "ack-id";
    when(datafeedApi.readEvents(any(), any(), any()))
        .thenReturn(buildV5Events(RealTimeEventType.ROOMCREATED, ackId))
        .thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id-2"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(2)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getAllValues().get(0), tag, "");
    assertEventsReadBody(bodyCaptor.getAllValues().get(1), tag, ackId);
  }

  @Test
  void testExceptionInListenerIsIgnored() throws ApiException, AuthUnauthorizedException {
    datahoseLoop.unsubscribe(listener);
    this.datahoseLoop.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onRoomCreated(V4Initiator initiator, V4RoomCreated event) throws EventException {
        throw new RuntimeException();
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        datahoseLoop.stop();
      }
    });

    final String ackId = "ack-id";
    when(datafeedApi.readEvents(any(), any(), any()))
        .thenReturn(buildV5Events(RealTimeEventType.ROOMCREATED, ackId))
        .thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id-2"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(2)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getAllValues().get(0), tag, "");
    assertEventsReadBody(bodyCaptor.getAllValues().get(1), tag, ackId);
  }

  @Test
  void testEventExceptionLeadsToAckIdNotUpdated() throws ApiException, AuthUnauthorizedException {
    datahoseLoop.unsubscribe(listener);
    this.datahoseLoop.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onRoomCreated(V4Initiator initiator, V4RoomCreated event) throws EventException {
        throw new EventException("exception");
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        datahoseLoop.stop();
      }
    });

    when(datafeedApi.readEvents(any(), any(), any()))
        .thenReturn(buildV5Events(RealTimeEventType.ROOMCREATED, "ack-id"))
        .thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ack-id-2"));

    datahoseLoop.start();

    ArgumentCaptor<V5EventsReadBody> bodyCaptor = ArgumentCaptor.forClass(V5EventsReadBody.class);
    verify(datafeedApi, times(2)).readEvents(any(), any(), bodyCaptor.capture());

    assertEventsReadBody(bodyCaptor.getAllValues().get(0), tag, "");
    assertEventsReadBody(bodyCaptor.getAllValues().get(1), tag, "");
  }

  @ParameterizedTest
  @ValueSource(ints = {400, 404})
  void testErrorIsNotRetried(int statusCode) throws ApiException {
    when(datafeedApi.readEvents(any(), any(), any()))
        .thenThrow(new ApiException(statusCode, ""));

    assertThrows(ApiException.class, () -> datahoseLoop.start());

    verify(datafeedApi, times(1)).readEvents(any(), any(), any());
  }

  @Test
  void testUnauthorizedTriggersRefresh() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.readEvents(any(), any(), any()))
        .thenThrow(new ApiException(401, ""))
        .thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ackId"));
    doNothing().when(authSession).refresh();

    datahoseLoop.start();

    verify(datafeedApi, times(2)).readEvents(any(), any(), any());
    verify(authSession, times(1)).refresh();
  }

  @ParameterizedTest
  @ValueSource(ints = {429, 500, 501, 502, 503})
  void testHttpErrorIsRetried(int statusCode) throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.readEvents(any(), any(), any()))
        .thenThrow(new ApiException(statusCode, ""))
        .thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ackId"));

    datahoseLoop.start();

    verify(datafeedApi, times(2)).readEvents(any(), any(), any());
  }

  @ParameterizedTest
  @ValueSource(classes = {SocketException.class, ConnectException.class, SocketTimeoutException.class,
      UnknownHostException.class})
  void testNetworkErrorsAreRetried(Class<? extends Exception> clazz)
      throws ApiException, AuthUnauthorizedException, InstantiationException, IllegalAccessException {
    when(datafeedApi.readEvents(any(), any(), any()))
        .thenThrow(new RuntimeException(clazz.newInstance()))
        .thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ackId"));
    doNothing().when(authSession).refresh();

    datahoseLoop.start();

    verify(datafeedApi, times(2)).readEvents(any(), any(), any());
  }

  @Test
  void testLoopAlreadyStarted() throws ApiException, AuthUnauthorizedException {
    when(datafeedApi.readEvents(any(), any(), any())).thenReturn(buildV5Events(RealTimeEventType.MESSAGESENT, "ackId"));

    AtomicBoolean isIllegalExceptionThrown = new AtomicBoolean(false);
    this.datahoseLoop.subscribe(new RealTimeEventListener() {
      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) {
        return true;
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        try {
          datahoseLoop.start();
        } catch (AuthUnauthorizedException | ApiException e) {
          e.printStackTrace();
        } catch (IllegalStateException e) {
          isIllegalExceptionThrown.set(true);
        } finally {
          datahoseLoop.stop();
        }
      }
    });
    datahoseLoop.unsubscribe(listener);

    datahoseLoop.start();
    assertTrue(isIllegalExceptionThrown.get());
  }

  private V5EventList buildV5Events(RealTimeEventType eventType, String ackId) {
    return new V5EventList().ackId(ackId).addEventsItem(new V4Event().type(eventType.name()).payload(new V4Payload()));
  }

  private void assertEventsReadBody(V5EventsReadBody actualBody, String expectedTag, String expectedAckId) {
    assertEquals(eventTypes, actualBody.getEventTypes());
    assertEquals(expectedTag, actualBody.getTag());
    assertEquals(expectedAckId, actualBody.getAckId());
  }
}
