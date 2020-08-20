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
import com.symphony.bdk.core.service.datafeed.DatafeedEventListener;
import com.symphony.bdk.core.test.BdkMockServer;
import com.symphony.bdk.core.test.BdkMockServerExtension;
import com.symphony.bdk.core.test.ResResponseHelper;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.Datafeed;
import com.symphony.bdk.gen.api.model.V4Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BdkMockServerExtension.class)
public class DatafeedServiceV1Test {

    private DatafeedServiceV1 datafeedService;
    private BdkConfig bdkConfig;
    private DatafeedApi datafeedApi;
    private AuthSession authSession;
    private DatafeedEventListener listener;

    @BeforeEach
    void init(@TempDir Path tempDir, final BdkMockServer mockServer) throws BdkConfigException {
        mockServer.onGet("/pod/v2/sessioninfo",
                res -> res.withBody(ResResponseHelper.readResResponseFromClasspath("bot_info.json")));
        this.authSession = Mockito.mock(AuthSessionImpl.class);
        when(this.authSession.getSessionToken()).thenReturn("1234");
        when(this.authSession.getKeyManagerToken()).thenReturn("1234");
        this.bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");

        BdkDatafeedConfig datafeedConfig = this.bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(tempDir.toString());
        this.bdkConfig.setDatafeed(datafeedConfig);

        BdkRetryConfig retryConfig = new BdkRetryConfig();
        retryConfig.setInitialIntervalMillis(500);
        retryConfig.setMultiplier(1);
        retryConfig.setMaxAttempts(2);
        retryConfig.setMaxIntervalMillis(900);
        this.bdkConfig.setRetry(retryConfig);

        this.datafeedService = new DatafeedServiceV1(
                mockServer.newApiClient("/agent"),
                mockServer.newApiClient("/pod"),
                this.authSession,
                this.bdkConfig
        );
        this.listener = new DatafeedEventListener() {
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
        V4Event event = new V4Event();
        event.setType(DatafeedEventConstant.MESSAGESENT);
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
    void handleV4EventTest() throws ApiException {
        List<V4Event> events = new ArrayList<>();
        events.add(null);
        V4Event messageSent = new V4Event();
        messageSent.setType(DatafeedEventConstant.MESSAGESENT);
        events.add(messageSent);
        V4Event connectionAccepted = new V4Event();
        connectionAccepted.setType(DatafeedEventConstant.CONNECTIONACCEPTED);
        events.add(connectionAccepted);
        V4Event messageSuppressed = new V4Event();
        messageSuppressed.setType(DatafeedEventConstant.MESSAGESUPPRESSED);
        events.add(messageSuppressed);
        V4Event symphonyElementsAction = new V4Event();
        symphonyElementsAction.setType(DatafeedEventConstant.SYMPHONYELEMENTSACTION);
        events.add(symphonyElementsAction);
        V4Event sharedPost = new V4Event();
        sharedPost.setType(DatafeedEventConstant.SHAREDPOST);
        events.add(sharedPost);
        V4Event instantMessageCreated = new V4Event();
        instantMessageCreated.setType(DatafeedEventConstant.INSTANTMESSAGECREATED);
        events.add(instantMessageCreated);
        V4Event roomCreated = new V4Event();
        roomCreated.setType(DatafeedEventConstant.ROOMCREATED);
        events.add(roomCreated);
        V4Event roomUpdated = new V4Event();
        roomUpdated.setType(DatafeedEventConstant.ROOMUPDATED);
        events.add(roomUpdated);
        V4Event roomDeactivated = new V4Event();
        roomDeactivated.setType(DatafeedEventConstant.ROOMDEACTIVATED);
        events.add(roomDeactivated);
        V4Event roomReactivated = new V4Event();
        roomReactivated.setType(DatafeedEventConstant.ROOMREACTIVATED);
        events.add(roomReactivated);
        V4Event userLeftRoom = new V4Event();
        userLeftRoom.setType(DatafeedEventConstant.USERLEFTROOM);
        events.add(userLeftRoom);
        V4Event userJoinedRoom = new V4Event();
        userJoinedRoom.setType(DatafeedEventConstant.USERJOINEDROOM);
        events.add(userJoinedRoom);
        V4Event userRequestedToJoinRoom = new V4Event();
        userRequestedToJoinRoom.setType(DatafeedEventConstant.USERREQUESTEDTOJOINROOM);
        events.add(userRequestedToJoinRoom);
        V4Event memberPromotedToOwner = new V4Event();
        memberPromotedToOwner.setType(DatafeedEventConstant.ROOMMEMBERPROMOTEDTOOWNER);
        events.add(memberPromotedToOwner);
        V4Event memberDemotedFromOwner = new V4Event();
        memberDemotedFromOwner.setType(DatafeedEventConstant.ROOMMEMBERDEMOTEDFROMOWNER);
        events.add(memberDemotedFromOwner);
        V4Event connectionRequested = new V4Event();
        connectionRequested.setType(DatafeedEventConstant.CONNECTIONREQUESTED);
        events.add(connectionRequested);
        this.datafeedService.unsubscribe(this.listener);
        DatafeedEventListener listener = new DatafeedEventListener() {};
        DatafeedEventListener spiedListener = Mockito.spy(listener);
        this.datafeedService.subscribe(spiedListener);
        this.datafeedService.handleV4EventList(events);

        verify(spiedListener).onMessageSent(messageSent);
        verify(spiedListener).onMessageSuppressed(messageSuppressed);
        verify(spiedListener).onSymphonyElementsAction(symphonyElementsAction);
        verify(spiedListener).onSharedPost(sharedPost);
        verify(spiedListener).onInstantMessageCreated(instantMessageCreated);
        verify(spiedListener).onRoomCreated(roomCreated);
        verify(spiedListener).onRoomUpdated(roomUpdated);
        verify(spiedListener).onRoomDeactivated(roomDeactivated);
        verify(spiedListener).onRoomReactivated(roomReactivated);
        verify(spiedListener).onConnectionRequested(connectionRequested);
        verify(spiedListener).onConnectionAccepted(connectionAccepted);
        verify(spiedListener).onRoomMemberDemotedFromOwner(memberDemotedFromOwner);
        verify(spiedListener).onRoomMemberPromotedToOwner(memberPromotedToOwner);
        verify(spiedListener).onUserLeftRoom(userLeftRoom);
        verify(spiedListener).onUserJoinedRoom(userJoinedRoom);
        verify(spiedListener).onUserRequestedToJoinRoom(userRequestedToJoinRoom);
    }

}
