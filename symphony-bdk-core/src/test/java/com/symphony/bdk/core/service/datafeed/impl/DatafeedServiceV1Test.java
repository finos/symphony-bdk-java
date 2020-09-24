package com.symphony.bdk.core.service.datafeed.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthSessionRsaImpl;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.BdkConfigLoaderTest;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.Datafeed;
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

import io.github.resilience4j.retry.Retry;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.ProcessingException;

public class DatafeedServiceV1Test {

    private DatafeedServiceV1 datafeedService;
    private BdkConfig bdkConfig;
    private DatafeedApi datafeedApi;
    private AuthSession authSession;
    private RealTimeEventListener listener;

    @BeforeEach
    void init(@TempDir Path tempDir) throws BdkConfigException {
        this.authSession = Mockito.mock(AuthSessionRsaImpl.class);
        when(this.authSession.getSessionToken()).thenReturn("1234");
        when(this.authSession.getKeyManagerToken()).thenReturn("1234");
        this.bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");

        BdkDatafeedConfig datafeedConfig = this.bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(tempDir.toString());
        this.bdkConfig.setDatafeed(datafeedConfig);

        BdkRetryConfig retryConfig = new BdkRetryConfig();
        retryConfig.setInitialIntervalMillis(50);
        retryConfig.setMultiplier(1);
        retryConfig.setMaxAttempts(2);
        retryConfig.setMaxIntervalMillis(90);
        this.bdkConfig.setRetry(retryConfig);

        this.datafeedService = new DatafeedServiceV1(
                null,
                this.authSession,
                this.bdkConfig
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
    void startTest() throws ApiException, AuthUnauthorizedException {
        List<V4Event> events = new ArrayList<>();
        V4Event event = new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload());
        events.add(event);
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
        when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
                .thenReturn(events);

        this.datafeedService.start();

        verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
        verify(datafeedApi, times(1)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);
    }

    @Test
    void startTestWithRetryCreate() throws ApiException, AuthUnauthorizedException {
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenThrow(new ApiException(401, "test_unauthorized"));
        doNothing().when(authSession).refresh();

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(2)).v4DatafeedCreatePost("1234", "1234");
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
    }

    @Test
    void startTestFailedAuthCreate() throws ApiException, AuthUnauthorizedException {
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenThrow(new ApiException(401, "test_unauthorized"));
        doThrow(AuthUnauthorizedException.class).when(authSession).refresh();

        assertThrows(AuthUnauthorizedException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
    }

    @Test
    void startTestClientErrorCreate() throws ApiException {
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenThrow(new ApiException(400, "test_client_error"));

        assertThrows(ApiException.class, this.datafeedService::start);
        verify(datafeedApi, times(1)).v4DatafeedCreatePost("1234", "1234");
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
    }

    @Test
    void startTestFailedSocketTimeoutRead() throws ApiException, AuthUnauthorizedException {
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
        when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
            .thenThrow(new ProcessingException(new SocketTimeoutException()));

        this.datafeedService.start();
        verify(datafeedApi, times(2)).v4DatafeedIdReadGet("test-id", "1234", "1234", null);
    }

    @Test
    void startServiceAlreadyStarted() throws ApiException, AuthUnauthorizedException {
        AtomicInteger signal = new AtomicInteger(0);
        List<V4Event> events = new ArrayList<>();
        V4Event event = new V4Event().type(RealTimeEventType.MESSAGESENT.name()).payload(new V4Payload());
        events.add(event);
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
        when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
                .thenReturn(events);

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
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/datafeed/datafeedId");
        Path datafeedFile = tempDir.resolve("datafeed.id");
        Files.copy(inputStream, datafeedFile);
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(tempDir.toString());
        bdkConfig.setDatafeed(datafeedConfig);

        Optional<String> datafeedId = this.datafeedService.retrieveDatafeed();
        assertTrue(datafeedId.isPresent());
        assertEquals(datafeedId.get(), "8e7c8672-220");
    }

    @Test
    void retrieveDatafeedIdFromDatafeedFile(@TempDir Path tempDir) throws IOException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/datafeed/datafeedId");
        Path datafeedFile = tempDir.resolve("datafeed.id");
        Files.copy(inputStream, datafeedFile);
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(datafeedFile.toString());
        bdkConfig.setDatafeed(datafeedConfig);

        Optional<String> datafeedId = this.datafeedService.retrieveDatafeed();
        assertTrue(datafeedId.isPresent());
        assertEquals(datafeedId.get(), "8e7c8672-220");
    }

    @Test
    void retrieveDatafeedIdFromInvalidDatafeedFile(@TempDir Path tempDir) throws IOException {
        Path datafeedFile = tempDir.resolve("datafeed.id");
        FileUtils.writeStringToFile(new File(String.valueOf(datafeedFile)), "8e7c8672-220", StandardCharsets.UTF_8 );
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
        List<V4Event> events = new ArrayList<>();
        events.add(null);
        RealTimeEventType[] types = RealTimeEventType.values();
        V4Payload payload = new V4Payload()
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
        V4Initiator initiator = new V4Initiator();
        for (RealTimeEventType type : types) {
            V4Event event = new V4Event().type(type.name());
            event.payload(payload).initiator(initiator);
            events.add(event);
        }
        events.add(new V4Event().type("unknown-type").payload(payload));
        events.add(new V4Event().type(types[0].name()).initiator(new V4Initiator().user(new V4User().username(bdkConfig.getBot().getUsername()))));
        this.datafeedService.unsubscribe(this.listener);
        RealTimeEventListener listener = new RealTimeEventListener() {};
        RealTimeEventListener spiedListener = Mockito.spy(listener);
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
