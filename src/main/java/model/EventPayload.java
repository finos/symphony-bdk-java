package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.events.*;

@JsonIgnoreProperties
public class EventPayload {

    private MessageSent messageSent;


    private SharedPost sharedPost;


    private IMCreated instantMessageCreated;


    private RoomCreated roomCreated;


    private RoomUpdated roomUpdated;


    private RoomDeactivated roomDeactivated;


    private RoomReactivated roomReactivated;


    private UserJoinedRoom userJoinedRoom;


    private UserLeftRoom userLeftRoom;


    private RoomMemberPromotedToOwner roomMemberPromotedToOwner;


    private RoomMemberDemotedFromOwner roomMemberDemotedFromOwner;


    private ConnectionRequested connectionRequested;


    private ConnectionAccepted connectionAccepted;


    private MessageSuppressed messageSuppressed;

    public MessageSent getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(MessageSent messageSent) {
        this.messageSent = messageSent;
    }

    public SharedPost getSharedPost() {
        return sharedPost;
    }

    public void setSharedPost(SharedPost sharedPost) {
        this.sharedPost = sharedPost;
    }

    public IMCreated getInstantMessageCreated() {
        return instantMessageCreated;
    }

    public void setInstantMessageCreated(IMCreated instantMessageCreated) {
        this.instantMessageCreated = instantMessageCreated;
    }

    public RoomCreated getRoomCreated() {
        return roomCreated;
    }

    public void setRoomCreated(RoomCreated roomCreated) {
        this.roomCreated = roomCreated;
    }

    public RoomUpdated getRoomUpdated() {
        return roomUpdated;
    }

    public void setRoomUpdated(RoomUpdated roomUpdated) {
        this.roomUpdated = roomUpdated;
    }

    public RoomDeactivated getRoomDeactivated() {
        return roomDeactivated;
    }

    public void setRoomDeactivated(RoomDeactivated roomDeactivated) {
        this.roomDeactivated = roomDeactivated;
    }

    public RoomReactivated getRoomReactivated() {
        return roomReactivated;
    }

    public void setRoomReactivated(RoomReactivated roomReactivated) {
        this.roomReactivated = roomReactivated;
    }

    public UserJoinedRoom getUserJoinedRoom() {
        return userJoinedRoom;
    }

    public void setUserJoinedRoom(UserJoinedRoom userJoinedRoom) {
        this.userJoinedRoom = userJoinedRoom;
    }

    public UserLeftRoom getUserLeftRoom() {
        return userLeftRoom;
    }

    public void setUserLeftRoom(UserLeftRoom userLeftRoom) {
        this.userLeftRoom = userLeftRoom;
    }

    public RoomMemberPromotedToOwner getRoomMemberPromotedToOwner() {
        return roomMemberPromotedToOwner;
    }

    public void setRoomMemberPromotedToOwner(RoomMemberPromotedToOwner roomMemberPromotedToOwner) {
        this.roomMemberPromotedToOwner = roomMemberPromotedToOwner;
    }

    public RoomMemberDemotedFromOwner getRoomMemberDemotedFromOwner() {
        return roomMemberDemotedFromOwner;
    }

    public void setRoomMemberDemotedFromOwner(RoomMemberDemotedFromOwner roomMemberDemotedFromOwner) {
        this.roomMemberDemotedFromOwner = roomMemberDemotedFromOwner;
    }

    public ConnectionRequested getConnectionRequested() {
        return connectionRequested;
    }

    public void setConnectionRequested(ConnectionRequested connectionRequested) {
        this.connectionRequested = connectionRequested;
    }

    public ConnectionAccepted getConnectionAccepted() {
        return connectionAccepted;
    }

    public void setConnectionAccepted(ConnectionAccepted connectionAccepted) {
        this.connectionAccepted = connectionAccepted;
    }

    public MessageSuppressed getMessageSuppressed() {
        return messageSuppressed;
    }

    public void setMessageSuppressed(MessageSuppressed messageSuppressed) {
        this.messageSuppressed = messageSuppressed;
    }
}
