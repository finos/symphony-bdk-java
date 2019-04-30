package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FirehoseReadResponse {
    private List<DatafeedEvent> events;
    private String ackId;

    public FirehoseReadResponse() {
        events = new ArrayList<>();
    }

    public FirehoseReadResponse(List<DatafeedEvent> events, String ackId) {
        this.events = events;
        this.ackId = ackId;
    }

    public List<DatafeedEvent> getEvents() {
        return events;
    }

    public void setEvents(List<DatafeedEvent> events) {
        this.events = events;
    }

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }
}
