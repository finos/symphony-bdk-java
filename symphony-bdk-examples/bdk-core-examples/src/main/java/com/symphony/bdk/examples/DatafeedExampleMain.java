package com.symphony.bdk.examples;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatafeedExampleMain {

    public static void main(String[] args) throws ApiException, BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

        // load configuration from classpath
        final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.yaml");

        SymphonyBdk bdk = new SymphonyBdk(config);
        log.info("DatafeedV1: Start");
        DatafeedService ds = bdk.datafeed();

        ds.subscribe(new RealTimeEventListener() {
            @Override
            public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
                if (event.getMessage() != null) {
                    log.info("Message with id {} successfully received !", event.getMessage().getMessageId());
                }
            }
        });
        try {
            ds.start();
        } finally {
            ds.stop();
        }
    }
}
