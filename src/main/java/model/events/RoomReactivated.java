package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomReactivated {
    private Stream stream;

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
