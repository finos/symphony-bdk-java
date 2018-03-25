package model.events;

import model.RoomProperties;
import model.Stream;

public class RoomCreated {
    private Stream stream;
    private RoomProperties roomProperties;

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public RoomProperties getRoomProperties() {
        return roomProperties;
    }

    public void setRoomProperties(RoomProperties roomProperties) {
        this.roomProperties = roomProperties;
    }
}
