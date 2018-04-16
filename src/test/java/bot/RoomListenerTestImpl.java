package bot;

import listeners.RoomListener;
import model.InboundMessage;
import model.Stream;
import model.events.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoomListenerTestImpl implements RoomListener {

    private final Logger logger = LoggerFactory.getLogger(RoomListenerTestImpl.class);
    @Override
    public void onRoomMessage(InboundMessage message) {
        logger.info(message.getMessage());
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

    }

    @Override
    public void onUserLeftRoom(UserLeftRoom userLeftRoom) {

    }
}
