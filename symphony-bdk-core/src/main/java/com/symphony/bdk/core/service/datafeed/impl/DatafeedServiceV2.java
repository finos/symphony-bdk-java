package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;

import java.util.concurrent.atomic.AtomicBoolean;

public class DatafeedServiceV2 extends AbstractDatafeedService {

    private final AtomicBoolean started = new AtomicBoolean();

    public DatafeedServiceV2(ApiClient agentClient, ApiClient podClient, AuthSession authSession, BdkConfig config) {
        super(agentClient, podClient, authSession, config);
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
