package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Initiator;
import model.RoomProperties;
import model.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomCreated {
    private Stream stream;
    private RoomProperties roomProperties;
    private Initiator initiator;

    public Initiator getInitiator() {
        return initiator;
    }

    public void setInitiator(Initiator initiator) {
        this.initiator = initiator;
    }

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
