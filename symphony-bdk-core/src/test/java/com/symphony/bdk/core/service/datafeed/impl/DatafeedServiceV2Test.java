package com.symphony.bdk.core.service.datafeed.impl;

import static com.symphony.bdk.core.test.BdkRetryConfigTestHelper.ofMinimalInterval;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
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
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.AckId;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Payload;
import com.symphony.bdk.gen.api.model.V5Datafeed;
import com.symphony.bdk.gen.api.model.V5EventList;
import com.symphony.bdk.http.api.ApiException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ProcessingException;

public class DatafeedServiceV2Test {

    private DatafeedServiceV2 datafeedService;
    private DatafeedApi datafeedApi;
    private AuthSession authSession;
    private RealTimeEventListener listener;

    @BeforeEach
    void setUp() throws BdkConfigException {
        BdkConfig bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setVersion("v2");
        bdkConfig.setDatafeed(datafeedConfig);
        bdkConfig.setRetry(ofMinimalInterval(2));

        this.authSession = Mockito.mock(AuthSessionRsaImpl.class);
        when(this.authSession.getSessionToken()).thenReturn("1234");
        when(this.authSession.getKeyManagerToken()).thenReturn("1234");

        this.datafeedService = new DatafeedServiceV2(
                null,
                this.authSession,
                bdkConfig
        );
        this.listener = new RealTimeEventListener() {
            @Override
            public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
                datafeedService.stop();
            }
        };
        this.datafeedService.subscribe(listener);

        this.datafeedApi = mock(DatafeedApi.class);
        this.datafeedService.setDatafeedApi(datafeedApi);
    }

    @Test
    void testStart() throws ApiException, AuthUnauthorizedException {
        List<V5Datafeed> datafeeds = new ArrayList<>();
        datafeeds.add(new V5Datafeed().id("test-id"));
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(datafeeds);
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
                .thenReturn(new V5EventList().addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

        this.datafeedService.start();

        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartEmptyListDatafeed() throws ApiException, AuthUnauthorizedException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.emptyList());
        when(datafeedApi.createDatafeed("1234", "1234")).thenReturn(new V5Datafeed().id("test-id"));
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
                .thenReturn(new V5EventList().addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

        this.datafeedService.start();

        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).createDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartServiceAlreadyStarted() throws ApiException, AuthUnauthorizedException {
        AtomicInteger signal = new AtomicInteger(0);
        List<V5Datafeed> datafeeds = new ArrayList<>();
        datafeeds.add(new V5Datafeed().id("test-id"));
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(datafeeds);
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
                .thenReturn(new V5EventList()
                        .addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload()))
                        .ackId("ack-id"));

        this.datafeedService.unsubscribe(this.listener);
        this.datafeedService.subscribe(new RealTimeEventListener() {
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
        assertEquals(signal.get(), 1);
    }

    @Test
    void testStartClientErrorListDatafeed() throws ApiException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenThrow(new ApiException(400, "client-error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
    }

    @Test
    void testStartAuthRefreshListDatafeed() throws ApiException, AuthUnauthorizedException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenThrow(new ApiException(401, "unauthorized-error"));
        doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

        assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(authSession, times(1)).refresh();
    }

    @Test
    void testStartServerErrorListDatafeed() throws ApiException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenThrow(new ApiException(502, "server-error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(2)).listDatafeed("1234", "1234");
    }

    @Test
    void testStartErrorListDatafeedThenRetrySuccess() throws ApiException, AuthUnauthorizedException {
        AtomicInteger count = new AtomicInteger(0);
        when(datafeedApi.listDatafeed("1234", "1234"))
            .thenThrow(new ApiException(502, "server-error"))
            .thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));

        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId))
                .thenReturn(new V5EventList().addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

        this.datafeedService.start();

        verify(datafeedApi, times(2)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartAuthErrorCreateDatafeed() throws ApiException, AuthUnauthorizedException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.emptyList());
        when(datafeedApi.createDatafeed("1234", "1234")).thenThrow(new ApiException(401, "unauthorized-error"));
        doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

        assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).createDatafeed("1234", "1234");
    }

    @Test
    void testStartClientErrorCreateDatafeed() throws ApiException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.emptyList());
        when(datafeedApi.createDatafeed("1234", "1234")).thenThrow(new ApiException(400, "client-error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).createDatafeed("1234", "1234");
    }

    @Test
    void testStartServerErrorCreateDatafeed() throws ApiException {
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.emptyList());
        when(datafeedApi.createDatafeed("1234", "1234")).thenThrow(new ApiException(502, "server-error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(2)).createDatafeed("1234", "1234");
    }

    @Test
    void testStartClientErrorReadDatafeed() throws ApiException, AuthUnauthorizedException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.createDatafeed("1234", "1234")).thenReturn(new V5Datafeed().id("recreate-df-id"));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
        when(datafeedApi.readDatafeed("recreate-df-id", "1234", "1234", ackId))
                .thenReturn(new V5EventList().addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));

        this.datafeedService.start();
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
        verify(datafeedApi, times(1)).readDatafeed("recreate-df-id", "1234", "1234", ackId);
        verify(datafeedApi, times(1)).createDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).deleteDatafeed("test-id", "1234", "1234");
    }

    @Test
    void testStartSocketTimeoutReadDatafeed() throws ApiException, AuthUnauthorizedException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ProcessingException(new SocketTimeoutException()));

        this.datafeedService.start();
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(2)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartAuthErrorReadDatafeed() throws ApiException, AuthUnauthorizedException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(401, "client-error"));
        doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

        assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
        verify(authSession, times(1)).refresh();
    }

    @Test
    void testStartServerErrorReadDatafeed() throws ApiException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(502, "client-error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(2)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartInternalServerErrorReadDatafeedShouldNotBeRetried() throws ApiException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(500, "client-error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartTooManyRequestsReadDatafeedShouldBeRetried() throws ApiException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(429, "too-many-requests"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(2)).readDatafeed("test-id", "1234", "1234", ackId);
    }

    @Test
    void testStartClientErrorDeleteDatafeed() throws ApiException, AuthUnauthorizedException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.createDatafeed("1234", "1234")).thenReturn(new V5Datafeed().id("recreate-df-id"));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
        when(datafeedApi.readDatafeed("recreate-df-id", "1234", "1234", ackId))
                .thenReturn(new V5EventList().addEventsItem(new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload())).ackId("ack-id"));
        when(datafeedApi.deleteDatafeed("test-id", "1234", "1234")).thenThrow(new ApiException(400, "client-error"));

        this.datafeedService.start();
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
        verify(datafeedApi, times(1)).readDatafeed("recreate-df-id", "1234", "1234", ackId);
        verify(datafeedApi, times(1)).createDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).deleteDatafeed("test-id", "1234", "1234");

    }

    @Test
    void testStartServerErrorDeleteDatafeed() throws ApiException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.createDatafeed("1234", "1234")).thenReturn(new V5Datafeed().id("recreate-df-id"));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
        when(datafeedApi.deleteDatafeed("test-id", "1234", "1234")).thenThrow(new ApiException(502, "client-error"));

        assertThrows(NestedRetryException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
        verify(datafeedApi, times(2)).deleteDatafeed("test-id", "1234", "1234");
    }

    @Test
    void testStartAuthErrorDeleteDatafeed() throws ApiException, AuthUnauthorizedException {
        AckId ackId = datafeedService.getAckId();
        when(datafeedApi.listDatafeed("1234", "1234")).thenReturn(Collections.singletonList(new V5Datafeed().id("test-id")));
        when(datafeedApi.createDatafeed("1234", "1234")).thenReturn(new V5Datafeed().id("recreate-df-id"));
        when(datafeedApi.readDatafeed("test-id", "1234", "1234", ackId)).thenThrow(new ApiException(400, "client-error"));
        when(datafeedApi.deleteDatafeed("test-id", "1234", "1234")).thenThrow(new ApiException(401, "client-error"));
        doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

        assertThrows(NestedRetryException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).listDatafeed("1234", "1234");
        verify(datafeedApi, times(1)).readDatafeed("test-id", "1234", "1234", ackId);
        verify(datafeedApi, times(1)).deleteDatafeed("test-id", "1234", "1234");
    }
}
