package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Initiator;
import model.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomReactivated {
    private Stream stream;
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
}
