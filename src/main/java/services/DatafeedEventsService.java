package services;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import configuration.LoadBalancingMethod;
import configuration.SymConfig;
import configuration.SymLoadBalancedConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import listeners.*;
import model.DatafeedEvent;
import model.Initiator;
import model.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatafeedEventsService {

    private final Logger logger = LoggerFactory.getLogger(DatafeedEventsService.class);

    private static final int MAX_BACKOFF_TIME = 5 * 60; // five minutes

    private SymBotClient botClient;
    private DatafeedClient datafeedClient;
    private List<RoomListener> roomListeners;
    private List<IMListener> imListeners;
    private List<ConnectionListener> connectionListeners;
    private List<ElementsListener> elementsListeners;
    private String datafeedId = null;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();
    private static int THREADPOOL_SIZE;
    private static int TIMEOUT_NO_OF_SECS;

    public DatafeedEventsService(SymBotClient client) {
        this.roomListeners = new ArrayList<>();
        this.imListeners = new ArrayList<>();
        this.connectionListeners = new ArrayList<>();
        this.elementsListeners = new ArrayList<>();

        this.botClient = client;
        this.datafeedClient = this.botClient.getDatafeedClient();

        int threadPoolSize = client.getConfig().getDatafeedEventsThreadpoolSize();
        THREADPOOL_SIZE = threadPoolSize > 0 ? threadPoolSize : 5;
        resetTimeout();

        // if the reuseDatafeedID config isn't set (null), we assume its default value as true
        if (botClient.getConfig().getReuseDatafeedID() == null || this.botClient.getConfig().getReuseDatafeedID()) {
            try {
                File file = botClient.getDatafeedIdFile();
                Path datafeedIdPath = Paths.get(file.getPath());
                String[] persistedDatafeed = Files.readAllLines(datafeedIdPath).get(0).split("@");
                datafeedId = persistedDatafeed[0];

                if (client.getConfig() instanceof SymLoadBalancedConfig) {
                    SymLoadBalancedConfig lbConfig = (SymLoadBalancedConfig) client.getConfig();
                    String[] agentHostPort = persistedDatafeed[1].split(":");
                    if (lbConfig.getLoadBalancing().getMethod() == LoadBalancingMethod.external) {
                        lbConfig.setActualAgentHost(agentHostPort[0]);
                    } else {
                        int previousIndex = lbConfig.getAgentServers().indexOf(agentHostPort[0]);
                        lbConfig.setCurrentAgentIndex(previousIndex);
                    }
                }

                logger.info("Using previous datafeed id: {}", datafeedId);
            } catch (IOException e) {
                logger.info("No previous datafeed id file");
            }
        }

        while (datafeedId == null) {
            try {
                datafeedId = datafeedClient.createDatafeed();
                resetTimeout();
            } catch (Exception e) {
                handleError(e);
            }
        }
        readDatafeed();
        stop.set(false);
    }

    private void resetTimeout() {
        int errorTimeout = this.botClient.getConfig().getDatafeedEventsErrorTimeout();
        TIMEOUT_NO_OF_SECS = errorTimeout > 0 ? errorTimeout : 30;
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
                            List<DatafeedEvent> events = datafeedClient.readDatafeed(datafeedId);
                            resetTimeout();
                            return events;
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
        if (!stop.get()) {
            stop.set(true);
        }
    }

    public void restartDatafeedService() {
        if (stop.get()) {
            stop.set(false);
        }
        datafeedId = datafeedClient.createDatafeed();

        readDatafeed();
    }

    private void handleError(Throwable e) {
        String errMsg = e.getMessage();
        if (errMsg.endsWith("SocketTimeoutException: Read timed out")) {
            int connectionTimeoutSeconds = botClient.getConfig().getConnectionTimeout() / 1000;
            logger.error("Connection timed out after {} seconds", connectionTimeoutSeconds);
        } else if (errMsg.endsWith("Origin Error") || errMsg.endsWith("Service Unavailable") || errMsg.endsWith("Bad Gateway")) {
            logger.error("Pod is unavailable");
        } else if (errMsg.contains("Could not find a datafeed with the")) {
            logger.error(errMsg);
        } else {
            logger.error("An unknown error happened, type : " + e.getClass(), e);
        }

        sleep();

        try {
            SymConfig config = botClient.getConfig();
            if (config instanceof SymLoadBalancedConfig) {
                ((SymLoadBalancedConfig) config).rotateAgent();
            }
            datafeedId = datafeedClient.createDatafeed();
            resetTimeout();
        } catch (Exception e1) {
            sleep();
            handleError(e);
        }
    }

    private void sleep() {
        try {
            logger.info("Sleeping for {} seconds before retrying..", TIMEOUT_NO_OF_SECS);
            TimeUnit.SECONDS.sleep(TIMEOUT_NO_OF_SECS);

            // exponential backoff until we reach the MAX_BACKOFF_TIME (5 minutes)
            if (TIMEOUT_NO_OF_SECS * 2 <= MAX_BACKOFF_TIME) {
                TIMEOUT_NO_OF_SECS *= 2;
            } else {
                TIMEOUT_NO_OF_SECS = MAX_BACKOFF_TIME;
            }
        } catch (InterruptedException ie) {
            logger.error("Error trying to sleep ", ie);
        }
    }

    private void handleEvents(List<DatafeedEvent> datafeedEvents) {
        for (DatafeedEvent event : datafeedEvents) {
            Initiator initiator = event.getInitiator();
            if (event == null || initiator.getUser().getUserId().equals(botClient.getBotUserId())) {
                continue;
            }

            switch (event.getType()) {
                case "MESSAGESENT":
                    MessageSent messageSent = event.getPayload().getMessageSent();
                    messageSent.setInitiator(initiator);

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
                    RoomCreated roomCreated = event.getPayload().getRoomCreated();
                    roomCreated.setInitiator(initiator);

                    for (RoomListener listener : roomListeners) {
                        listener.onRoomCreated(roomCreated);
                    }
                    break;

                case "ROOMUPDATED":
                    RoomUpdated roomUpdated = event.getPayload().getRoomUpdated();
                    roomUpdated.setInitiator(initiator);

                    for (RoomListener listener : roomListeners) {
                        listener.onRoomUpdated(roomUpdated);
                    }
                    break;

                case "ROOMDEACTIVATED":
                    RoomDeactivated roomDeactivated = event.getPayload().getRoomDeactivated();
                    roomDeactivated.setInitiator(initiator);

                    for (RoomListener listener : roomListeners) {
                        listener.onRoomDeactivated(roomDeactivated);
                    }
                    break;

                case "ROOMREACTIVATED":
                    for (RoomListener listener : roomListeners) {
                        listener.onRoomReactivated(event.getPayload().getRoomReactivated().getStream());
                    }
                    break;

                case "USERJOINEDROOM":
                    UserJoinedRoom userJoinedRoom = event.getPayload().getUserJoinedRoom();
                    userJoinedRoom.setInitiator(initiator);

                    for (RoomListener listener : roomListeners) {
                        listener.onUserJoinedRoom(userJoinedRoom);
                    }
                    break;

                case "USERLEFTROOM":
                    UserLeftRoom userLeftRoom = event.getPayload().getUserLeftRoom();
                    userLeftRoom.setInitiator(initiator);

                    for (RoomListener listener : roomListeners) {
                        listener.onUserLeftRoom(userLeftRoom);
                    }
                    break;

                case "ROOMMEMBERPROMOTEDTOOWNER":
                    RoomMemberPromotedToOwner roomMemberPromotedToOwner = event.getPayload().getRoomMemberPromotedToOwner();
                    roomMemberPromotedToOwner.setInitiator(initiator);

                    for (RoomListener listener : roomListeners) {
                        listener.onRoomMemberPromotedToOwner(roomMemberPromotedToOwner);
                    }
                    break;

                case "ROOMMEMBERDEMOTEDFROMOWNER":
                    RoomMemberDemotedFromOwner roomMemberDemotedFromOwner = event.getPayload().getRoomMemberDemotedFromOwner();
                    roomMemberDemotedFromOwner.setInitiator(initiator);

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

                case "SYMPHONYELEMENTSACTION":
                    for (ElementsListener listener : elementsListeners) {
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
}
