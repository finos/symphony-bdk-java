package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Initiator;
import model.RoomProperties;
import model.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomUpdated {
    private Stream stream;
    private RoomProperties newRoomProperties;
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

    public RoomProperties getNewRoomProperties() {
        return newRoomProperties;
    }

    public void setNewRoomProperties(RoomProperties newRoomProperties) {
        this.newRoomProperties = newRoomProperties;
    }
}
