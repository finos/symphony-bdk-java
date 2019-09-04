package services;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;
import exceptions.SymClientException;
import listeners.*;
import model.DatafeedEvent;
import model.events.MessageSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.ws.rs.ProcessingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatafeedEventsService {
    private final Logger logger = LoggerFactory.getLogger(DatafeedEventsService.class);
    private SymBotClient botClient;
    private DatafeedClient datafeedClient;
    private List<RoomListener> roomListeners;
    private List<IMListener> imListeners;
    private List<ConnectionListener> connectionListeners;
    private List<ElementsListener> elementsListeners;
    private String datafeedId = null;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();
    private final int THREADPOOL_SIZE;
    private final int TIMEOUT_NO_OF_SECS;

    public DatafeedEventsService(SymBotClient client) {
        this.roomListeners = new ArrayList<>();
        this.imListeners = new ArrayList<>();
        this.connectionListeners = new ArrayList<>();
        this.elementsListeners = new ArrayList<>();

        this.botClient = client;
        this.datafeedClient = this.botClient.getDatafeedClient();

        int threadPoolSize = client.getConfig().getDatafeedEventsThreadpoolSize();
        this.THREADPOOL_SIZE = threadPoolSize > 0 ? threadPoolSize : 5;
        int errorTimeout = client.getConfig().getDatafeedEventsErrorTimeout();
        this.TIMEOUT_NO_OF_SECS = errorTimeout > 0 ? errorTimeout : 30;

        while (datafeedId == null) {
            try {
                datafeedId = datafeedClient.createDatafeed();
            } catch (ProcessingException e) {
                handleError(e);
            }
        }
        readDatafeed();
        stop.set(false);
    }

    public void addListeners(DatafeedListener... listeners) {
        for (DatafeedListener listener : listeners) {
            if (listener instanceof RoomListener) {
                addRoomListener((RoomListener) listener);
            } else if (listener instanceof IMListener) {
                addIMListener((IMListener) listener);
            } else if (listener instanceof ConnectionListener) {
                addConnectionsListener((ConnectionListener) listener);
            } else if (listener instanceof ElementsListener) {
                addElementsListener((ElementsListener) listener);
            }
        }
    }

    public void removeListeners(DatafeedListener... listeners) {
        for (DatafeedListener listener : listeners) {
            if (listener instanceof RoomListener) {
                removeRoomListener((RoomListener) listener);
            } else if (listener instanceof IMListener) {
                removeIMListener((IMListener) listener);
            } else if (listener instanceof ConnectionListener) {
                removeConnectionsListener((ConnectionListener) listener);
            } else if (listener instanceof ElementsListener) {
                removeElementsListener((ElementsListener) listener);
            }
        }
    }

    public void addRoomListener(RoomListener listener) {
        roomListeners.add(listener);
    }

    public void removeRoomListener(RoomListener listener) {
        roomListeners.remove(listener);
    }

    public void addIMListener(IMListener listener) {
        imListeners.add(listener);
    }

    public void removeIMListener(IMListener listener) {
        imListeners.remove(listener);
    }

    public void addConnectionsListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    public void removeConnectionsListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    public void addElementsListener(ElementsListener listener) {
        elementsListeners.add(listener);
    }

    public void removeElementsListener(ElementsListener listener) {
        elementsListeners.remove(listener);
    }

    public void readDatafeed() {
        if (pool != null) {
            pool.shutdown();
        }
        pool = Executors.newFixedThreadPool(THREADPOOL_SIZE);
        CompletableFuture.supplyAsync(() -> {
            while (!stop.get()) {
                CompletableFuture<Object> future = CompletableFuture
                    .supplyAsync(() -> {
                        try {
                            return datafeedClient.readDatafeed(datafeedId);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }, pool)
                    .exceptionally((ex) -> {
                        handleError(ex);
                        return Collections.emptyList();
                    })
                    .thenApply(events -> {
                        if (events != null && !events.isEmpty()) {
                            handleEvents(events);
                        }
                        return Collections.emptyList();
                    });

                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error trying to read datafeed", e);
                }
            }
            return Collections.emptyList();
        }, pool);
    }

    public void stopDatafeedService() {
        if (!stop.get()) stop.set(true);
    }

    public void restartDatafeedService() {
        if (stop.get()) stop.set(false);
        datafeedId = datafeedClient.createDatafeed();

        readDatafeed();
    }

    private void handleError(Throwable e) {
        if (e.getMessage().endsWith("SocketTimeoutException: Read timed out")) {
            int connectionTimeoutSeconds = botClient.getConfig().getConnectionTimeout() / 1000;
            logger.info("Connection timed out after {} seconds", connectionTimeoutSeconds);
        } else {
            logger.error("HandlerError error", e);
        }
        logger.info("Sleeping for {} seconds before retrying..", TIMEOUT_NO_OF_SECS);
        sleep();
        try {
            SymConfig config = botClient.getConfig();
            if (config instanceof SymLoadBalancedConfig) {
                ((SymLoadBalancedConfig) config).rotateAgent();
            }
            datafeedId = datafeedClient.createDatafeed();
        } catch (SymClientException e1) {
            sleep();
            handleError(e);
        }
    }

    private void sleep() {
        try {
            TimeUnit.SECONDS.sleep(TIMEOUT_NO_OF_SECS);
        } catch (InterruptedException ie) {
            logger.error("Error trying to sleep ", ie);
        }
    }

    private void handleEvents(List<DatafeedEvent> datafeedEvents) {
        for (DatafeedEvent event : datafeedEvents) {
            if (event == null)
                continue;

            switch (event.getType()) {
                case "MESSAGESENT":
                    MessageSent messageSent = event.getPayload().getMessageSent();
                    
                    if (messageSent.getMessage().getStream().getStreamType().equals("ROOM")) {
                        for (RoomListener listener : roomListeners) {
                            if(shouldHandleEvent(event, listener))
                                listener.onRoomMessage(messageSent.getMessage());
                        }
                    } else {
                        for (IMListener listener : imListeners) {
                            if(shouldHandleEvent(event, listener))
                                listener.onIMMessage(messageSent.getMessage());
                        }
                    }
                    break;

                case "INSTANTMESSAGECREATED":
                    for (IMListener listener : imListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onIMCreated(event.getPayload().getInstantMessageCreated().getStream());
                    }
                    break;

                case "ROOMCREATED":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onRoomCreated(event.getPayload().getRoomCreated());
                    }
                    break;

                case "ROOMUPDATED":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onRoomUpdated(event.getPayload().getRoomUpdated());
                    }
                    break;

                case "ROOMDEACTIVATED":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onRoomDeactivated(event.getPayload().getRoomDeactivated());
                    }
                    break;

                case "ROOMREACTIVATED":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onRoomReactivated(event.getPayload().getRoomReactivated().getStream());
                    }
                    break;

                case "USERJOINEDROOM":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onUserJoinedRoom(event.getPayload().getUserJoinedRoom());
                    }
                    break;

                case "USERLEFTROOM":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onUserLeftRoom(event.getPayload().getUserLeftRoom());
                    }
                    break;

                case "ROOMMEMBERPROMOTEDTOOWNER":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onRoomMemberPromotedToOwner(event.getPayload().getRoomMemberPromotedToOwner());
                    }
                    break;

                case "ROOMMEMBERDEMOTEDFROMOWNER":
                    for (RoomListener listener : roomListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onRoomMemberDemotedFromOwner(event.getPayload().getRoomMemberDemotedFromOwner());
                    }
                    break;

                case "CONNECTIONACCEPTED":
                    for (ConnectionListener listener : connectionListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onConnectionAccepted(event.getInitiator().getUser());
                    }
                    break;

                case "CONNECTIONREQUESTED":
                    for (ConnectionListener listener : connectionListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onConnectionRequested(event.getInitiator().getUser());
                    }
                    break;

                case "SYMPHONYELEMENTSACTION":
                    for (ElementsListener listener : elementsListeners) {
                        if(shouldHandleEvent(event, listener))
                            listener.onElementsAction(
                                event.getInitiator().getUser(),
                                event.getPayload().getSymphonyElementsAction()
                            );
                    }
                    break;

                default:
                    break;
            }
        }
    }
    
    private boolean shouldHandleEvent(DatafeedEvent event, DatafeedListener listener){
        return (!listener.ignoreSelf() || !event.getInitiator().getUser().getUserId().equals(botClient.getBotUserInfo().getId()));
    }
}
