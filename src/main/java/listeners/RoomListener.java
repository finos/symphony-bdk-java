package listeners;

import model.InboundMessage;
import model.Initiator;
import model.Stream;
import model.events.*;

public interface RoomListener extends DatafeedListener {
    void onRoomMessage(InboundMessage message, Initiator initiator);
    void onRoomCreated(RoomCreated roomCreated, Initiator initiator);
    void onRoomDeactivated(RoomDeactivated roomDeactivated, Initiator initiator);
    void onRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner, Initiator initiator);
    void onRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner, Initiator initiator);
    void onRoomReactivated(Stream stream, Initiator initiator);
    void onRoomUpdated(RoomUpdated roomUpdated, Initiator initiator);
    void onUserJoinedRoom(UserJoinedRoom userJoinedRoom, Initiator initiator);
    void onUserLeftRoom(UserLeftRoom userLeftRoom, Initiator initiator);
}
