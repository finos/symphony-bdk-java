package example;

import clients.SymBotClient;
import exceptions.UnauthorizedException;
import listeners.RoomListener;
import model.InboundMessage;
import model.OutboundMessage;
import model.Stream;
import model.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.SymMessageParser;

public class RoomListenerTestImpl implements RoomListener {

    private SymBotClient botClient;

    public RoomListenerTestImpl(SymBotClient botClient) {
        this.botClient = botClient;
    }

    private final Logger logger = LoggerFactory.getLogger(RoomListenerTestImpl.class);
    @Override
    public void onRoomMessage(InboundMessage message) {
        OutboundMessage messageOut = new OutboundMessage();
        messageOut.setMessage("<messageML>Hi "+message.getUser().getFirstName()+"!</messageML>");
        try {
            this.botClient.getMessagesClient().sendMessage(message.getStream().getStreamId(), messageOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRoomCreated(RoomCreated roomCreated) {

    }

    @Override
    public void onRoomDeactivated(RoomDeactivated roomDeactivated) {

    }

    @Override
    public void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {

    }

    @Override
    public void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner) {

    }

    @Override
    public void onRoomReactivated(Stream stream) {

    }

    @Override
    public void onRoomUpdated(RoomUpdated roomUpdated) {

    }

    @Override
    public void onUserJoinedRoom(UserJoinedRoom userJoinedRoom) {
        OutboundMessage messageOut = new OutboundMessage();
        messageOut.setMessage("<messageML>Welcome "+userJoinedRoom.getAffectedUser().getFirstName()+"!</messageML>");
        try {
            this.botClient.getMessagesClient().sendMessage(userJoinedRoom.getStream().getStreamId(), messageOut);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUserLeftRoom(UserLeftRoom userLeftRoom) {

    }
}
