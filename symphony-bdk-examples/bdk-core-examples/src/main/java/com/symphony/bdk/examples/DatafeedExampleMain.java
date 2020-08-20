package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.api.invoker.ApiException;
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
import java.util.concurrent.TimeUnit;

@Slf4j
public class DatafeedExampleMain {

    public static void main(String[] args) throws ApiException, BdkConfigException, AuthInitializationException, AuthUnauthorizedException, InterruptedException {

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
        });
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                ds.start();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        Thread.sleep(10000);
        ds.stop();
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
