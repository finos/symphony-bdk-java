package services;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;
import exceptions.SymClientException;
import listeners.ConnectionListener;
import listeners.DatafeedListener;
import listeners.IMListener;
import listeners.RoomListener;
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
    private String datafeedId = null;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();
    private final int THREADPOOL_SIZE;
    private final int TIMEOUT_NO_OF_SECS;

    public DatafeedEventsService(SymBotClient client) {
        this.botClient = client;
        this.THREADPOOL_SIZE = client.getConfig().getDatafeedEventsThreadpoolSize() != 0
            ? client.getConfig().getDatafeedEventsThreadpoolSize() : 5;
        this.TIMEOUT_NO_OF_SECS = client.getConfig().getDatafeedEventsErrorTimeout() != 0
            ? client.getConfig().getDatafeedEventsErrorTimeout() : 30;
        roomListeners = new ArrayList<>();
        imListeners = new ArrayList<>();
        connectionListeners = new ArrayList<>();
        datafeedClient = this.botClient.getDatafeedClient();

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
                roomListeners.add((RoomListener) listener);
            } else if (listener instanceof IMListener) {
                imListeners.add((IMListener) listener);
            } else if (listener instanceof ConnectionListener) {
                connectionListeners.add((ConnectionListener) listener);
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
        logger.error("HandlerError error", e);
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
            if (event == null || event.getInitiator().getUser().getUserId().equals(botClient.getBotUserInfo().getId()))
                continue;

            switch (event.getType()) {
                case "MESSAGESENT":
                    MessageSent messageSent = event.getPayload().getMessageSent();

                    if (messageSent.getMessage().getStream().getStreamType().equals("ROOM")) {
                        for (RoomListener listener : roomListeners) {
                            listener.onRoomMessage(messageSent.getMessage());
                        }
                    } else {
                        for (IMListener listener : imListeners) {
                            listener.onIMMessage(messageSent.getMessage());
                        }
                    }
                    break;

                case "INSTANTMESSAGECREATED":
                    for (IMListener listeners : imListeners) {
                        listeners.onIMCreated(event.getPayload().getInstantMessageCreated().getStream());
                    }
                    break;

                case "ROOMCREATED":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomCreated(event.getPayload().getRoomCreated());
                    }
                    break;

                case "ROOMUPDATED":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomUpdated(event.getPayload().getRoomUpdated());
                    }
                    break;

                case "ROOMDEACTIVATED":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomDeactivated(event.getPayload().getRoomDeactivated());
                    }
                    break;

                case "ROOMREACTIVATED":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomReactivated(event.getPayload().getRoomReactivated().getStream());
                    }
                    break;

                case "USERJOINEDROOM":
                    for (RoomListener listener : roomListeners) {
                        listener.onUserJoinedRoom(event.getPayload().getUserJoinedRoom());
                    }
                    break;

                case "USERLEFTROOM":
                    for (RoomListener listener : roomListeners) {
                        listener.onUserLeftRoom(event.getPayload().getUserLeftRoom());
                    }
                    break;

                case "ROOMMEMBERPROMOTEDTOOWNER":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomMemberPromotedToOwner(event.getPayload().getRoomMemberPromotedToOwner());
                    }
                    break;

                case "ROOMMEMBERDEMOTEDFROMOWNER":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomMemberDemotedFromOwner(event.getPayload().getRoomMemberDemotedFromOwner());
                    }
                    break;

                case "CONNECTIONACCEPTED":
                    for (ConnectionListener listener : connectionListeners) {
                        listener.onConnectionAccepted(event.getInitiator().getUser());
                    }
                    break;

                case "CONNECTIONREQUESTED":
                    for (ConnectionListener listener : connectionListeners) {
                        listener.onConnectionRequested(event.getInitiator().getUser());
                    }
                    break;

                default:
                    break;
            }
        }
    }
}
