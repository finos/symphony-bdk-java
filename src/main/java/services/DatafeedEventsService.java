package services;

import authentication.SymBotAuth;
import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatafeedEventsService {
    private final Logger logger = LoggerFactory.getLogger(DatafeedEventsService.class);
    private SymBotClient botClient;
    private DatafeedClient datafeedClient;
    private List<RoomListener> roomListeners;
    private List<IMListener> IMListeners;
    private List<ConnectionListener> connectionListeners;
    private String datafeedId;

    public DatafeedEventsService(SymBotClient client) {
        this.botClient = client;
        roomListeners = new ArrayList<RoomListener>();
        IMListeners = new ArrayList<IMListener>();
        connectionListeners = new ArrayList<ConnectionListener>();
        datafeedClient = this.botClient.getDatafeedClient();
        datafeedId = datafeedClient.createDatafeed();
        readDatafeed(datafeedId);
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

    public void readDatafeed(String id){

        ExecutorService pool = Executors.newFixedThreadPool(5);
        CompletableFuture.supplyAsync(() -> {
            try {
                return datafeedClient.readDatafeed(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        },pool)
            .exceptionally((ex)-> {
                handleError(ex);
                return null;
            })
            .thenApply(events -> {
                if(!events.isEmpty()){
                    handleEvents(events);
                }
                readDatafeed(id);

                return null;
            });

    }

    private void handleError(Throwable e) {
        if (e instanceof UnauthorizedException){
            botClient.getSymBotAuth().authenticate();
            datafeedId = datafeedClient.createDatafeed();
            readDatafeed(datafeedId);
        }
        logger.error(e.getMessage());
    }

    private void handleEvents(List<DatafeedEvent> datafeedEvents) {
        for (DatafeedEvent event: datafeedEvents) {
            if (!event.getInitiator().getUser().getUserId().equals(botClient.getBotUserInfo().getId())) {
                switch (event.getType()) {
                    case "MESSAGESENT":

                        MessageSent messageSent = event.getPayload().getMessageSent();
                        String messageContent = SymMessageParser.parseContent(messageSent.getMessage().getMessage());
                        messageSent.getMessage().setMessageText(messageContent);

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
