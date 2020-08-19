package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.BotInfoService;
import com.symphony.bdk.core.service.datafeed.DatafeedEventListener;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class DatafeedExampleMain {

    public static void main(String[] args) throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException, InterruptedException {

        // load configuration from classpath
        final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.yaml");

        SymphonyBdk bdk = new SymphonyBdk(config);

        log.info("Config: " + config.getPod().getBasePath());
        BotInfoService infoService = bdk.botInfo();
        UserV2 userInfo = infoService.getBotInfo();
        log.info("UserId: " + userInfo.getId());
        log.info("Second time: " + bdk.botInfo().getBotInfo().getCompany());

        log.info("DatafeedV1: Start");
        DatafeedService ds = bdk.datafeed();
        ds.subscribe(new DatafeedEventListener() {
            @Override
            public void onMessageSent(V4Event event) {
                log.info("Message Sent");
            }

            @Override
            public void onSharedPost(V4Event event) {

            }

            @Override
            public void onInstantMessageCreated(V4Event event) {
                log.info("IM Created");
            }

            @Override
            public void onRoomCreated(V4Event event) {

            }

            @Override
            public void onRoomUpdated(V4Event event) {

            }

            @Override
            public void onRoomDeactivated(V4Event event) {

            }

            @Override
            public void onRoomReactivated(V4Event event) {

            }

            @Override
            public void onUserRequestedToJoinRoom(V4Event event) {

            }

            @Override
            public void onUserJoinedRoom(V4Event event) {

            }

            @Override
            public void onUserLeftRoom(V4Event event) {

            }

            @Override
            public void onRoomMemberPromotedToOwner(V4Event event) {

            }

            @Override
            public void onRoomMemberDemotedFromOwner(V4Event event) {

            }

            @Override
            public void onConnectionRequested(V4Event event) {

            }

            @Override
            public void onConnectionAccepted(V4Event event) {

            }

            @Override
            public void onMessageSuppressed(V4Event event) {

            }

            @Override
            public void onSymphonyElementsAction(V4Event event) {

            }
        });
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                ds.start();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        Thread.sleep(100000);
    }
}
