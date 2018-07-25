package services;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import exceptions.APIClientErrorException;
import exceptions.SymClientException;
import exceptions.UnauthorizedException;
import listeners.ConnectionListener;
import listeners.IMListener;
import listeners.RoomListener;
import model.DatafeedEvent;
import model.DatafeedEventsList;
import model.events.MessageSent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SymMessageParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class DatafeedEventsService {
    private final Logger logger = LoggerFactory.getLogger(DatafeedEventsService.class);
    private SymBotClient botClient;
    private DatafeedClient datafeedClient;
    private List<RoomListener> roomListeners;
    private List<IMListener> IMListeners;
    private List<ConnectionListener> connectionListeners;
    private String datafeedId;
    private ExecutorService pool;
    private AtomicBoolean stop = new AtomicBoolean();

    public DatafeedEventsService(SymBotClient client) {
        this.botClient = client;
        roomListeners = new ArrayList<>();
        IMListeners = new ArrayList<>();
        connectionListeners = new ArrayList<>();
        datafeedClient = this.botClient.getDatafeedClient();
        datafeedId = datafeedClient.createDatafeed();

        readDatafeed();
        stop.set(false);
    }

    public void addRoomListener(RoomListener listener){
        roomListeners.add(listener);
    }

    public void removeRoomListener(RoomListener listener){
        roomListeners.remove(listener);
    }

    public void addIMListener(IMListener listener){
        IMListeners.add(listener);
    }

    public void removeIMListener(IMListener listener){
        IMListeners.remove(listener);
    }

    public void addConnectionsListener(ConnectionListener listener){
        connectionListeners.add(listener);
    }

    public void removeConnectionsListener(ConnectionListener listener){
        connectionListeners.remove(listener);
    }

    public void readDatafeed(){
        if( pool!=null) {
            pool.shutdown();
        }
        pool = Executors.newFixedThreadPool(5);
        CompletableFuture.supplyAsync(() -> {
            while(!stop.get()) {
                CompletableFuture<Object> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        return datafeedClient.readDatafeed(datafeedId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }, pool)
                        .exceptionally((ex) -> {
                            handleError(ex);
                            return null;
                        })
                        .thenApply(events -> {
                            if (!events.isEmpty()) {
                                handleEvents(events);
                            }

                            return null;
                        });
                try {
                    future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
            return null;
        },pool);



    }

    public void stopDatafeedService(){
        if(!stop.get()) stop.set(true);
    }

    public void restartDatafeedService(){
        if(stop.get()) stop.set(false);
        datafeedId = datafeedClient.createDatafeed();

        readDatafeed();
    }

    private void handleError(Throwable e) {
        logger.error(e.getMessage());
        try {
            TimeUnit.SECONDS.sleep(30);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        try {
            datafeedId = datafeedClient.createDatafeed();
        } catch (SymClientException e1) {
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            handleError(e);
        }


    }

    private void handleEvents(List<DatafeedEvent> datafeedEvents) {
        for (DatafeedEvent event: datafeedEvents) {
            if (!event.getInitiator().getUser().getUserId().equals(botClient.getBotUserInfo().getId())) {
                switch (event.getType()) {
                    case "MESSAGESENT":

                        MessageSent messageSent = event.getPayload().getMessageSent();

                        if (messageSent.getMessage().getStream().getStreamType().equals("ROOM")) {
                            for (RoomListener listener : roomListeners) {
                                listener.onRoomMessage(messageSent.getMessage());
                            }
                        } else {
                            for (IMListener listener : IMListeners) {
                                listener.onIMMessage(messageSent.getMessage());
                            }
                        }
                        break;
                    case "INSTANTMESSAGECREATED":

                        for (IMListener listeners : IMListeners) {
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
                            listener.onConnectionAccepted(event.getPayload().getConnectionAccepted().getFromUser());
                        }
                        break;

                    case "CONNECTIONREQUESTED":

                        for (ConnectionListener listener : connectionListeners) {
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
