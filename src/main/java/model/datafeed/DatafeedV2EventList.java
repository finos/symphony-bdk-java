package model.datafeed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.DatafeedEvent;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatafeedV2EventList {

    private String ackId;
    private List<DatafeedEvent> events;

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public List<DatafeedEvent> getEvents() {
        return events;
    }

    public void setEvents(List<DatafeedEvent> events) {
        this.events = events;
    }
}
