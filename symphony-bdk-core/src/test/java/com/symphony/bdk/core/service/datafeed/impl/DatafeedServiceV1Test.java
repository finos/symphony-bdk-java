package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.auth.impl.AuthSessionImpl;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.BdkConfigLoaderTest;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.*;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatafeedServiceV1Test {

    private DatafeedServiceV1 datafeedService;
    private BdkConfig bdkConfig;
    private DatafeedApi datafeedApi;
    private AuthSession authSession;
    private RealTimeEventListener listener;

    @BeforeEach
    void init(@TempDir Path tempDir) throws BdkConfigException {
        this.authSession = Mockito.mock(AuthSessionImpl.class);
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
            public void onMessageSent(V4Event event) {
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
        V4Event event = new V4Event().type(DatafeedEventConstant.MESSAGESENT);
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
    void startServiceAlreadyStarted() throws ApiException, AuthUnauthorizedException {
        AtomicInteger signal = new AtomicInteger(0);
        List<V4Event> events = new ArrayList<>();
        V4Event event = new V4Event().type(DatafeedEventConstant.MESSAGESENT);
        events.add(event);
        when(datafeedApi.v4DatafeedCreatePost("1234", "1234")).thenReturn(new Datafeed().id("test-id"));
        when(datafeedApi.v4DatafeedIdReadGet("test-id", "1234", "1234", null))
                .thenReturn(events);

        this.datafeedService.unsubscribe(this.listener);
        this.datafeedService.subscribe(new RealTimeEventListener() {
            @Override
            public void onMessageSent(V4Event event) {
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

        String datafeedId = this.datafeedService.retrieveDatafeedIdFromDisk();
        assertEquals(datafeedId, "8e7c8672-220");
    }

    @Test
    void retrieveDatafeedIdFromDatafeedFile(@TempDir Path tempDir) throws IOException {
        InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/datafeed/datafeedId");
        Path datafeedFile = tempDir.resolve("datafeed.id");
        Files.copy(inputStream, datafeedFile);
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(datafeedFile.toString());
        bdkConfig.setDatafeed(datafeedConfig);

        String datafeedId = this.datafeedService.retrieveDatafeedIdFromDisk();
        assertEquals(datafeedId, "8e7c8672-220");
    }

    @Test
    void retrieveDatafeedIdFromUnknownPath() {
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath("unknown_path");
        bdkConfig.setDatafeed(datafeedConfig);

        String datafeedId = this.datafeedService.retrieveDatafeedIdFromDisk();
        assertNull(datafeedId);
    }

    @Test
    void retrieveDatafeedIdFromEmptyFile(@TempDir Path tempDir) {
        Path datafeedFile = tempDir.resolve("datafeed.id");
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(datafeedFile.toString());

        String datafeedId = this.datafeedService.retrieveDatafeedIdFromDisk();
        assertNull(datafeedId);
    }

    @Test
    void getRetryInstanceTest() {
        Retry retry = this.datafeedService.getRetryInstance("Test retry");
        assertNotNull(retry);
        assertEquals("Test retry", retry.getName());
        assertEquals(2, retry.getRetryConfig().getMaxAttempts());
    }

    @Test
    void handleV4EventTest() {
        List<V4Event> events = new ArrayList<>();
        events.add(null);
        Field[] fields = DatafeedEventConstant.class.getDeclaredFields();
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
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
                try {
                    V4Event event = new V4Event().type((String) f.get(String.class));
                    event.payload(payload).initiator(initiator);
                    events.add(event);
                } catch (IllegalAccessException ignored) {

                }
            }
        }
        this.datafeedService.unsubscribe(this.listener);
        RealTimeEventListener listener = new RealTimeEventListener() {};
        RealTimeEventListener spiedListener = Mockito.spy(listener);
        this.datafeedService.subscribe(spiedListener);
        this.datafeedService.handleV4EventList(events);

        verify(spiedListener).onMessageSent(getEventByType(events, DatafeedEventConstant.MESSAGESENT));
        verify(spiedListener).onMessageSent(initiator, payload.getMessageSent());
        verify(spiedListener).onMessageSuppressed(getEventByType(events, DatafeedEventConstant.MESSAGESUPPRESSED));
        verify(spiedListener).onMessageSuppressed(initiator, payload.getMessageSuppressed());
        verify(spiedListener).onSymphonyElementsAction(getEventByType(events, DatafeedEventConstant.SYMPHONYELEMENTSACTION));
        verify(spiedListener).onSymphonyElementsAction(initiator, payload.getSymphonyElementsAction());
        verify(spiedListener).onSharedPost(getEventByType(events, DatafeedEventConstant.SHAREDPOST));
        verify(spiedListener).onSharedPost(initiator, payload.getSharedPost());
        verify(spiedListener).onInstantMessageCreated(getEventByType(events, DatafeedEventConstant.INSTANTMESSAGECREATED));
        verify(spiedListener).onInstantMessageCreated(initiator, payload.getInstantMessageCreated());
        verify(spiedListener).onRoomCreated(getEventByType(events, DatafeedEventConstant.ROOMCREATED));
        verify(spiedListener).onRoomCreated(initiator, payload.getRoomCreated());
        verify(spiedListener).onRoomUpdated(getEventByType(events, DatafeedEventConstant.ROOMUPDATED));
        verify(spiedListener).onRoomUpdated(initiator, payload.getRoomUpdated());
        verify(spiedListener).onRoomDeactivated(getEventByType(events, DatafeedEventConstant.ROOMDEACTIVATED));
        verify(spiedListener).onRoomDeactivated(initiator, payload.getRoomDeactivated());
        verify(spiedListener).onRoomReactivated(getEventByType(events, DatafeedEventConstant.ROOMREACTIVATED));
        verify(spiedListener).onRoomReactivated(initiator, payload.getRoomReactivated());
        verify(spiedListener).onConnectionRequested(getEventByType(events, DatafeedEventConstant.CONNECTIONREQUESTED));
        verify(spiedListener).onConnectionRequested(initiator, payload.getConnectionRequested());
        verify(spiedListener).onConnectionAccepted(getEventByType(events, DatafeedEventConstant.CONNECTIONACCEPTED));
        verify(spiedListener).onConnectionAccepted(initiator, payload.getConnectionAccepted());
        verify(spiedListener).onRoomMemberDemotedFromOwner(getEventByType(events, DatafeedEventConstant.ROOMMEMBERDEMOTEDFROMOWNER));
        verify(spiedListener).onRoomMemberDemotedFromOwner(initiator, payload.getRoomMemberDemotedFromOwner());
        verify(spiedListener).onRoomMemberPromotedToOwner(getEventByType(events, DatafeedEventConstant.ROOMMEMBERPROMOTEDTOOWNER));
        verify(spiedListener).onRoomMemberPromotedToOwner(initiator, payload.getRoomMemberPromotedToOwner());
        verify(spiedListener).onUserLeftRoom(getEventByType(events, DatafeedEventConstant.USERLEFTROOM));
        verify(spiedListener).onUserLeftRoom(initiator, payload.getUserLeftRoom());
        verify(spiedListener).onUserJoinedRoom(getEventByType(events, DatafeedEventConstant.USERJOINEDROOM));
        verify(spiedListener).onUserJoinedRoom(initiator, payload.getUserJoinedRoom());
        verify(spiedListener).onUserRequestedToJoinRoom(getEventByType(events, DatafeedEventConstant.USERREQUESTEDTOJOINROOM));
        verify(spiedListener).onUserRequestedToJoinRoom(initiator, payload.getUserRequestedToJoinRoom());
    }

    private V4Event getEventByType(List<V4Event> events, String type) {
        for (V4Event event : events) {
            if (event != null && event.getType() != null && event.getType().equals(type)) {
                return event;
            }
        }
        return null;
    }

}
