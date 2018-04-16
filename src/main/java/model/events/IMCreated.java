package model.events;

import model.Stream;

public class IMCreated {
    private Stream stream;

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
