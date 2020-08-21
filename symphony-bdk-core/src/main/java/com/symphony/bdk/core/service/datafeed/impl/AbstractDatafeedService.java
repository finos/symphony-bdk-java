package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.service.BotInfoService;
import com.symphony.bdk.core.service.datafeed.DatafeedEventListener;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.util.BdkExponentialFunction;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.V4Event;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
abstract class AbstractDatafeedService implements DatafeedService {

    protected final AuthSession authSession;
    protected final BdkConfig bdkConfig;
    protected final BotInfoService sessionInfoService;
    protected final List<DatafeedEventListener> listeners;
    protected final RetryConfig retryConfig;
    protected DatafeedApi datafeedApi;
    protected SessionApi sessionApi;

    public AbstractDatafeedService(ApiClient agentClient, ApiClient podClient, AuthSession authSession, BdkConfig config) {
        this.datafeedApi = new DatafeedApi(agentClient);
        this.sessionApi = new SessionApi(podClient);
        this.sessionInfoService = new BotInfoService(podClient, authSession);
        this.listeners = new ArrayList<>();
        this.authSession = authSession;
        this.bdkConfig = config;
        BdkRetryConfig bdkRetryConfig = this.bdkConfig.getDatafeed().getRetry() == null ? this.bdkConfig.getRetry() : this.bdkConfig.getDatafeed().getRetry();
        this.retryConfig = RetryConfig.custom()
                .maxAttempts(bdkRetryConfig.getMaxAttempts())
                .intervalFunction(BdkExponentialFunction.ofExponentialBackoff(bdkRetryConfig))
                .retryOnException(e -> {
                    if (e instanceof ApiException) {
                        ApiException apiException = (ApiException) e;
                        return apiException.isServerError() || apiException.isUnauthorized() || apiException.isClientError();
                    }
                    return false;
                })
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(DatafeedEventListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(DatafeedEventListener listener) {
        listeners.remove(listener);
    }

    protected void handleV4EventList(List<V4Event> events) throws ApiException {
        for (V4Event event : events) {
            if (event == null || event.getType() == null) {
                continue;
            }
            if (this.isSelfGeneratedEvent(event)) {
                continue;
            }
            for (DatafeedEventListener listener : listeners) {
                switch (event.getType()) {
                    case DatafeedEventConstant.MESSAGESENT:
                        listener.onMessageSent(event);
                        break;

                    case DatafeedEventConstant.MESSAGESUPPRESSED:
                        listener.onMessageSuppressed(event);
                        break;

                    case DatafeedEventConstant.INSTANTMESSAGECREATED:
                        listener.onInstantMessageCreated(event);
                        break;

                    case DatafeedEventConstant.SHAREDPOST:
                        listener.onSharedPost(event);
                        break;

                    case DatafeedEventConstant.ROOMCREATED:
                        listener.onRoomCreated(event);
                        break;

                    case DatafeedEventConstant.ROOMUPDATED:
                        listener.onRoomUpdated(event);
                        break;

                    case DatafeedEventConstant.ROOMDEACTIVATED:
                        listener.onRoomDeactivated(event);
                        break;

                    case DatafeedEventConstant.ROOMREACTIVATED:
                        listener.onRoomReactivated(event);
                        break;

                    case DatafeedEventConstant.USERREQUESTEDTOJOINROOM:
                        listener.onUserRequestedToJoinRoom(event);
                        break;

                    case DatafeedEventConstant.USERJOINEDROOM:
                        listener.onUserJoinedRoom(event);
                        break;

                    case DatafeedEventConstant.USERLEFTROOM:
                        listener.onUserLeftRoom(event);
                        break;

                    case DatafeedEventConstant.ROOMMEMBERPROMOTEDTOOWNER:
                        listener.onRoomMemberPromotedToOwner(event);
                        break;

                    case DatafeedEventConstant.ROOMMEMBERDEMOTEDFROMOWNER:
                        listener.onRoomMemberDemotedFromOwner(event);
                        break;

                    case DatafeedEventConstant.CONNECTIONACCEPTED:
                        listener.onConnectionAccepted(event);
                        break;

                    case DatafeedEventConstant.CONNECTIONREQUESTED:
                        listener.onConnectionRequested(event);
                        break;

                    case DatafeedEventConstant.SYMPHONYELEMENTSACTION:
                        listener.onSymphonyElementsAction(event);
                        break;
                }
            }
        }
    }

    private boolean isSelfGeneratedEvent(V4Event event) throws ApiException {
        return event.getInitiator() != null && event.getInitiator().getUser() != null
                && event.getInitiator().getUser().getUserId() != null
                && event.getInitiator().getUser().getUserId().equals(sessionInfoService.getBotInfo().getId());
    }

    protected void setDatafeedApi(DatafeedApi datafeedApi) {
        this.datafeedApi = datafeedApi;
    }

}
