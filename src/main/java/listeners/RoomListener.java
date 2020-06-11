package listeners;

import model.InboundMessage;
import model.Initiator;
import model.Stream;
import model.events.*;

public interface RoomListener extends DatafeedListener {
    void onRoomMessage(InboundMessage message);
    void onRoomCreated(RoomCreated roomCreated);
    void onRoomDeactivated(RoomDeactivated roomDeactivated);
    void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner);
    void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner);
    void onRoomReactivated(Stream stream);
    void onRoomUpdated(RoomUpdated roomUpdated);
    void onUserJoinedRoom(UserJoinedRoom userJoinedRoom);
    void onUserLeftRoom(UserLeftRoom userLeftRoom);
}
