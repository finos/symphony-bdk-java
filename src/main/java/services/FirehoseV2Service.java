package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseV2Client;
import exceptions.SymClientException;
import listeners.FirehoseListener;
import model.DatafeedEvent;
import model.events.MessageSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class FirehoseV2Service {
    private final Logger logger = LoggerFactory.getLogger(FirehoseV2Service.class);
    private static SymBotClient botClient;
    private FirehoseV2Client firehoseClient;
    private List<FirehoseListener> listeners;
    private String firehoseId;
    private String ackId = null;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();
    private int THREADPOOL_SIZE;
    private int TIMEOUT_NO_OF_SECS;

    public FirehoseV2Service(SymBotClient client) {
        int poolSize = client.getConfig().getDatafeedEventsThreadpoolSize();
        int timeout = client.getConfig().getDatafeedEventsErrorTimeout();

        botClient = client;
        this.THREADPOOL_SIZE = (poolSize != 0) ? poolSize : 5;
        this.TIMEOUT_NO_OF_SECS = (timeout != 0) ? timeout : 30;
        listeners = new ArrayList<>();
        firehoseClient = botClient.getFirehoseV2Client();

        firehoseClient.listFirehose().forEach(fh -> {
            logger.info("Got Old Firehose: {}", fh.getFirehoseId());
            firehoseClient.deleteFirehose(fh.getFirehoseId());
            logger.info("Deleted Old Firehose: {}", fh.getFirehoseId());
        });

        firehoseId = firehoseClient.createFirehose();
        logger.info("Created Firehose: {}", firehoseId);

        readFirehose();
        stop.set(false);
    }

    public FirehoseV2Service(SymBotClient client, String firehoseId) {
        botClient = client;
        listeners = new ArrayList<>();
        firehoseClient = botClient.getFirehoseV2Client();
        this.firehoseId = firehoseId;

        readFirehose();
        stop.set(false);
    }

    public void addListener(FirehoseListener listener) {
        listeners.add(listener);
    }

    public void removeListener(FirehoseListener listener) {
        listeners.remove(listener);
    }

    public void readFirehose() {
        if (pool != null) {
            pool.shutdown();
        }
        pool = Executors.newFixedThreadPool(THREADPOOL_SIZE);
        CompletableFuture.supplyAsync(() -> {
            while (!stop.get()) {
                CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return firehoseClient.readFirehose(firehoseId, ackId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, pool)
                    .exceptionally((ex) -> {
                        handleError(ex);
                        return null;
                    })
                    .thenApply(response -> {
                        if (response.getEvents() != null && !response.getEvents().isEmpty()) {
                            handleEvents(response.getEvents(), listeners);
                            ackId = response.getAckId();
                        }
                        return null;
                    });
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    logger.error("Error trying to read firehose ", e);
                }
            }
            return null;
        }, pool);
    }

    public void stopDatafeedService() {
        if (!stop.get()) stop.set(true);
    }

    public void restartDatafeedService() {
        if (stop.get()) stop.set(false);
        firehoseId = firehoseClient.createFirehose();

        readFirehose();
    }

    private void handleError(Throwable e) {
        logger.error("HandlerError error", e);
        logger.info("Sleeping for {} seconds before retrying..", TIMEOUT_NO_OF_SECS);
        sleep();
        try {
            firehoseId = firehoseClient.createFirehose();
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

    static void handleEvents(List<DatafeedEvent> firehoseEvents, List<FirehoseListener> listeners) {
        for (DatafeedEvent event : firehoseEvents) {
            if (!event.getInitiator().getUser().getUserId().equals(botClient.getBotUserInfo().getId())) {
                switch (event.getType()) {
                    case "MESSAGESENT":
                        MessageSent messageSent = event.getPayload().getMessageSent();
                        if (messageSent.getMessage().getStream().getStreamType().equals("ROOM")) {
                            for (FirehoseListener listener : listeners) {
                                listener.onRoomMessage(messageSent.getMessage());
                            }
                        } else {
                            for (FirehoseListener listener : listeners) {
                                listener.onIMMessage(messageSent.getMessage());
                            }
                        }
                        break;

                    case "INSTANTMESSAGECREATED":
                        for (FirehoseListener listener : listeners) {
                            listener.onIMCreated(event.getPayload().getInstantMessageCreated().getStream());
                        }
                        break;

                    case "ROOMCREATED":
                        for (FirehoseListener listener : listeners) {
                            listener.onRoomCreated(event.getPayload().getRoomCreated());
                        }
                        break;

                    case "ROOMUPDATED":
                        for (FirehoseListener listener : listeners) {
                            listener.onRoomUpdated(event.getPayload().getRoomUpdated());
                        }
                        break;

                    case "ROOMDEACTIVATED":
                        for (FirehoseListener listener : listeners) {
                            listener.onRoomDeactivated(event.getPayload().getRoomDeactivated());
                        }
                        break;

                    case "ROOMREACTIVATED":
                        for (FirehoseListener listener : listeners) {
                            listener.onRoomReactivated(event.getPayload().getRoomReactivated().getStream());
                        }
                        break;

                    case "USERJOINEDROOM":
                        for (FirehoseListener listener : listeners) {
                            listener.onUserJoinedRoom(event.getPayload().getUserJoinedRoom());
                        }
                        break;

                    case "USERLEFTROOM":
                        for (FirehoseListener listener : listeners) {
                            listener.onUserLeftRoom(event.getPayload().getUserLeftRoom());
                        }
                        break;

                    case "ROOMMEMBERPROMOTEDTOOWNER":
                        for (FirehoseListener listener : listeners) {
                            listener.onRoomMemberPromotedToOwner(event.getPayload().getRoomMemberPromotedToOwner());
                        }
                        break;

                    case "ROOMMEMBERDEMOTEDFROMOWNER":
                        for (FirehoseListener listener : listeners) {
                            listener.onRoomMemberDemotedFromOwner(event.getPayload().getRoomMemberDemotedFromOwner());
                        }
                        break;

                    case "CONNECTIONACCEPTED":
                        for (FirehoseListener listener : listeners) {
                            listener.onConnectionAccepted(event.getPayload().getConnectionAccepted().getFromUser());
                        }
                        break;

                    case "CONNECTIONREQUESTED":
                        for (FirehoseListener listener : listeners) {
                            listener.onConnectionRequested(event.getPayload().getConnectionRequested().getToUser());
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }
}
