package services;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import listeners.*;
import model.DatafeedEvent;
import model.Initiator;
import model.events.*;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractDatafeedEventsService class handling datafeed events.
 */
abstract class AbstractDatafeedEventsService implements IDatafeedEventsService {

    protected final SymBotClient botClient;
    protected final DatafeedClient datafeedClient;
    protected List<RoomListener> roomListeners;
    protected List<IMListener> imListeners;
    protected List<ConnectionListener> connectionListeners;
    protected List<ElementsListener> elementsListeners;

    public AbstractDatafeedEventsService(SymBotClient client) {
        this.roomListeners = new ArrayList<>();
        this.imListeners = new ArrayList<>();
        this.connectionListeners = new ArrayList<>();
        this.elementsListeners = new ArrayList<>();

        this.botClient = client;
        this.datafeedClient = this.botClient.getDatafeedClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRoomListener(RoomListener listener) {
        roomListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRoomListener(RoomListener listener) {
        roomListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addIMListener(IMListener listener) {
        imListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeIMListener(IMListener listener) {
        imListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addConnectionsListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeConnectionsListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addElementsListener(ElementsListener listener) {
        elementsListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeElementsListener(ElementsListener listener) {
        elementsListeners.remove(listener);
    }

    protected void handleEvents(List<DatafeedEvent> datafeedEvents) {
        for (DatafeedEvent event : datafeedEvents) {
            if (event == null || event.getInitiator().getUser().getUserId().equals(botClient.getBotUserId())) {
                continue;
            }
            Initiator initiator = event.getInitiator();

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
