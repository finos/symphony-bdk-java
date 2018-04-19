package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties
public class RoomInfo {

    private Room roomAttributes;

    private RoomSystemInfo roomSystemInfo;

    public Room getRoomAttributes() {
        return roomAttributes;
    }

    public void setRoomAttributes(Room roomAttributes) {
        this.roomAttributes = roomAttributes;
    }

    public RoomSystemInfo getRoomSystemInfo() {
        return roomSystemInfo;
    }

    public void setRoomSystemInfo(RoomSystemInfo roomSystemInfo) {
        this.roomSystemInfo = roomSystemInfo;
    }
}
