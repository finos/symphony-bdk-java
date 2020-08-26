package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkRetryConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.util.BdkExponentialFunction;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.V4Event;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for implementing the datafeed services. A datafeed services can help a bot subscribe or unsubscribe
 * a {@link RealTimeEventListener} and handle the received event by the subscribed listeners.
 */
@Slf4j
abstract class AbstractDatafeedService implements DatafeedService {

    protected final AuthSession authSession;
    protected final BdkConfig bdkConfig;
    protected final List<RealTimeEventListener> listeners;
    protected final RetryConfig retryConfig;
    protected DatafeedApi datafeedApi;

    public AbstractDatafeedService(ApiClient agentClient, AuthSession authSession, BdkConfig config) {
        this.datafeedApi = new DatafeedApi(agentClient);
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
                        return apiException.isServerError() || apiException.isUnauthorized();
                    }
                    return false;
                })
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(RealTimeEventListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(RealTimeEventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Handle a received listener by using the subscribed {@link RealTimeEventListener}.
     *
     * @param events List of Datafeed events to be handled
     *
     */
    protected void handleV4EventList(List<V4Event> events) {
        for (V4Event event : events) {
            if (event == null || event.getType() == null) {
                continue;
            }
            if (this.isSelfGeneratedEvent(event)) {
                continue;
            }
            for (RealTimeEventListener listener : listeners) {
                switch (event.getType()) {
                    case DatafeedEventConstant.MESSAGESENT:
                        listener.onMessageSent(event);
                        if (event.getPayload() != null) {
                            listener.onMessageSent(event.getInitiator(), event.getPayload().getMessageSent());
                        }
                        break;

                    case DatafeedEventConstant.MESSAGESUPPRESSED:
                        listener.onMessageSuppressed(event);
                        if (event.getPayload() != null) {
                            listener.onMessageSuppressed(event.getInitiator(), event.getPayload().getMessageSuppressed());
                        }
                        break;

                    case DatafeedEventConstant.INSTANTMESSAGECREATED:
                        listener.onInstantMessageCreated(event);
                        if (event.getPayload() != null) {
                            listener.onInstantMessageCreated(event.getInitiator(), event.getPayload().getInstantMessageCreated());
                        }
                        break;

                    case DatafeedEventConstant.SHAREDPOST:
                        listener.onSharedPost(event);
                        if (event.getPayload() != null) {
                            listener.onSharedPost(event.getInitiator(), event.getPayload().getSharedPost());
                        }
                        break;

                    case DatafeedEventConstant.ROOMCREATED:
                        listener.onRoomCreated(event);
                        if (event.getPayload() != null) {
                            listener.onRoomCreated(event.getInitiator(), event.getPayload().getRoomCreated());
                        }
                        break;

                    case DatafeedEventConstant.ROOMUPDATED:
                        listener.onRoomUpdated(event);
                        if (event.getPayload() != null) {
                            listener.onRoomUpdated(event.getInitiator(), event.getPayload().getRoomUpdated());
                        }
                        break;

                    case DatafeedEventConstant.ROOMDEACTIVATED:
                        listener.onRoomDeactivated(event);
                        if (event.getPayload() != null) {
                            listener.onRoomDeactivated(event.getInitiator(), event.getPayload().getRoomDeactivated());
                        }
                        break;

                    case DatafeedEventConstant.ROOMREACTIVATED:
                        listener.onRoomReactivated(event);
                        if (event.getPayload() != null) {
                            listener.onRoomReactivated(event.getInitiator(), event.getPayload().getRoomReactivated());
                        }
                        break;

                    case DatafeedEventConstant.USERREQUESTEDTOJOINROOM:
                        listener.onUserRequestedToJoinRoom(event);
                        if (event.getPayload() != null) {
                            listener.onUserRequestedToJoinRoom(event.getInitiator(), event.getPayload().getUserRequestedToJoinRoom());
                        }
                        break;

                    case DatafeedEventConstant.USERJOINEDROOM:
                        listener.onUserJoinedRoom(event);
                        if (event.getPayload() != null) {
                            listener.onUserJoinedRoom(event.getInitiator(), event.getPayload().getUserJoinedRoom());
                        }
                        break;

                    case DatafeedEventConstant.USERLEFTROOM:
                        listener.onUserLeftRoom(event);
                        if (event.getPayload() != null) {
                            listener.onUserLeftRoom(event.getInitiator(), event.getPayload().getUserLeftRoom());
                        }
                        break;

                    case DatafeedEventConstant.ROOMMEMBERPROMOTEDTOOWNER:
                        listener.onRoomMemberPromotedToOwner(event);
                        if (event.getPayload() != null) {
                            listener.onRoomMemberPromotedToOwner(event.getInitiator(), event.getPayload().getRoomMemberPromotedToOwner());
                        }
                        break;

                    case DatafeedEventConstant.ROOMMEMBERDEMOTEDFROMOWNER:
                        listener.onRoomMemberDemotedFromOwner(event);
                        if (event.getPayload() != null) {
                            listener.onRoomMemberDemotedFromOwner(event.getInitiator(), event.getPayload().getRoomMemberDemotedFromOwner());
                        }
                        break;

                    case DatafeedEventConstant.CONNECTIONACCEPTED:
                        listener.onConnectionAccepted(event);
                        if (event.getPayload() != null) {
                            listener.onConnectionAccepted(event.getInitiator(), event.getPayload().getConnectionAccepted());
                        }
                        break;

                    case DatafeedEventConstant.CONNECTIONREQUESTED:
                        listener.onConnectionRequested(event);
                        if (event.getPayload() != null) {
                            listener.onConnectionRequested(event.getInitiator(), event.getPayload().getConnectionRequested());
                        }
                        break;

                    case DatafeedEventConstant.SYMPHONYELEMENTSACTION:
                        listener.onSymphonyElementsAction(event);
                        if (event.getPayload() != null) {
                            listener.onSymphonyElementsAction(event.getInitiator(), event.getPayload().getSymphonyElementsAction());
                        }
                        break;
                }
            }
        }
    }

    private boolean isSelfGeneratedEvent(V4Event event) {
        return event.getInitiator() != null && event.getInitiator().getUser() != null
                && event.getInitiator().getUser().getUsername() != null
                && event.getInitiator().getUser().getUsername().equals(this.bdkConfig.getBot().getUsername());
    }

    protected Retry getRetryInstance(String name, RetryConfig... config) {
        Retry retry = config.length == 0 ? Retry.of(name, this.retryConfig) : Retry.of(name, config[0]);
        retry.getEventPublisher().onRetry(event -> {
            long intervalInMillis = event.getWaitInterval().toMillis();
            double interval = intervalInMillis / 1000.0;
            log.info("Retry in {} secs...", interval);
        });
        return retry;
    }

    protected void setDatafeedApi(DatafeedApi datafeedApi) {
        this.datafeedApi = datafeedApi;
    }

}
