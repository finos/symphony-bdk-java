package model.events;

import model.Stream;

public class RoomDeactivated {

    private Stream stream;

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
