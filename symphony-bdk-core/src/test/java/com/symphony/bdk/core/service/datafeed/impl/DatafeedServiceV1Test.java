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
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.Datafeed;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatafeedServiceV1Test {

    private DatafeedServiceV1 datafeedService;
    private BdkConfig bdkConfig;
    private DatafeedApi datafeedApi;
    private SessionApi sessionApi;
    private AuthSession authSession;
    private DatafeedEventListener listener;

    @BeforeEach
    void init(@TempDir Path tempDir) throws BdkConfigException, IOException, ApiException {
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
                null,
                null,
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
        this.sessionApi = mock(SessionApi.class);
        UserV2 botInfo = new UserV2().id(7696581394433L);
        when(this.sessionApi.v2SessioninfoGet("1234")).thenReturn(botInfo);
        this.datafeedService.setSessionApi(this.sessionApi);
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
    void retrieveDatafeedIdFromEmptyFile(@TempDir Path tempDir) {
        Path datafeedFile = tempDir.resolve("datafeed.id");
        BdkDatafeedConfig datafeedConfig = bdkConfig.getDatafeed();
        datafeedConfig.setIdFilePath(datafeedFile.toString());

        String datafeedId = this.datafeedService.retrieveDatafeedIdFromDisk();
        assertNull(datafeedId);
    }

    @Test
    void handleV4EventTest() throws ApiException {
        List<V4Event> events = new ArrayList<>();
        events.add(null);
        Field[] fields = DatafeedEventConstant.class.getDeclaredFields();
        for (Field f : fields) {
            if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())) {
                try {
                    V4Event event = new V4Event().type((String) f.get(String.class));
                    events.add(event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        this.datafeedService.unsubscribe(this.listener);
        DatafeedEventListener listener = new DatafeedEventListener() {};
        DatafeedEventListener spiedListener = Mockito.spy(listener);
        this.datafeedService.subscribe(spiedListener);
        this.datafeedService.handleV4EventList(events);

        verify(spiedListener).onMessageSent(getEventByType(events, DatafeedEventConstant.MESSAGESENT));
        verify(spiedListener).onMessageSuppressed(getEventByType(events, DatafeedEventConstant.MESSAGESUPPRESSED));
        verify(spiedListener).onSymphonyElementsAction(getEventByType(events, DatafeedEventConstant.SYMPHONYELEMENTSACTION));
        verify(spiedListener).onSharedPost(getEventByType(events, DatafeedEventConstant.SHAREDPOST));
        verify(spiedListener).onInstantMessageCreated(getEventByType(events, DatafeedEventConstant.INSTANTMESSAGECREATED));
        verify(spiedListener).onRoomCreated(getEventByType(events, DatafeedEventConstant.ROOMCREATED));
        verify(spiedListener).onRoomUpdated(getEventByType(events, DatafeedEventConstant.ROOMUPDATED));
        verify(spiedListener).onRoomDeactivated(getEventByType(events, DatafeedEventConstant.ROOMDEACTIVATED));
        verify(spiedListener).onRoomReactivated(getEventByType(events, DatafeedEventConstant.ROOMREACTIVATED));
        verify(spiedListener).onConnectionRequested(getEventByType(events, DatafeedEventConstant.CONNECTIONREQUESTED));
        verify(spiedListener).onConnectionAccepted(getEventByType(events, DatafeedEventConstant.CONNECTIONACCEPTED));
        verify(spiedListener).onRoomMemberDemotedFromOwner(getEventByType(events, DatafeedEventConstant.ROOMMEMBERDEMOTEDFROMOWNER));
        verify(spiedListener).onRoomMemberPromotedToOwner(getEventByType(events, DatafeedEventConstant.ROOMMEMBERPROMOTEDTOOWNER));
        verify(spiedListener).onUserLeftRoom(getEventByType(events, DatafeedEventConstant.USERLEFTROOM));
        verify(spiedListener).onUserJoinedRoom(getEventByType(events, DatafeedEventConstant.USERJOINEDROOM));
        verify(spiedListener).onUserRequestedToJoinRoom(getEventByType(events, DatafeedEventConstant.USERREQUESTEDTOJOINROOM));
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
