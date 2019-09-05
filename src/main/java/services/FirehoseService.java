package services;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import exceptions.SymClientException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import listeners.FirehoseListener;
import model.DatafeedEvent;
import model.events.MessageSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirehoseService {
    private final Logger logger = LoggerFactory.getLogger(FirehoseService.class);
    private SymBotClient botClient;
    private FirehoseClient firehoseClient;
    private List<FirehoseListener> listeners;
    private String firehoseId;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();

    public FirehoseService(SymBotClient client) {
        this.botClient = client;
        listeners = new ArrayList<>();
        firehoseClient = this.botClient.getFirehoseClient();
        firehoseId = firehoseClient.createFirehose();

        readFirehose();
        stop.set(false);
    }

    public FirehoseService(SymBotClient client, String firehoseId) {
        this.botClient = client;
        listeners = new ArrayList<>();
        firehoseClient = this.botClient.getFirehoseClient();
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
        pool = Executors.newFixedThreadPool(5);
        CompletableFuture.supplyAsync(() -> {
            while (!stop.get()) {
                CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return firehoseClient.readFirehose(firehoseId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, pool)
                    .exceptionally((ex) -> {
                        handleError(ex);
                        return null;
                    })
                    .thenApply(events -> {
                        if (events != null || !events.isEmpty()) {
                            handleEvents(events);
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
        if (!stop.get()) {
            stop.set(true);
        }
    }

    public void restartDatafeedService() {
        if (stop.get()) {
            stop.set(false);
        }
        firehoseId = firehoseClient.createFirehose();

        readFirehose();
    }

    private void handleError(Throwable e) {
        logger.error(e.getMessage());
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException ie) {
            logger.error("Error trying to sleep ", ie);
        }
        try {
            firehoseId = firehoseClient.createFirehose();
        } catch (SymClientException e1) {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException ie) {
                logger.error("Error trying to sleep ", ie);
            }
            handleError(e);
        }
    }

    private void handleEvents(List<DatafeedEvent> firehoseEvents) {
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

                        for (FirehoseListener listeners : listeners) {
                            listeners.onIMCreated(event.getPayload().getInstantMessageCreated().getStream());
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
