package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)

public class StreamListItem {

    private String id;
    private Boolean crossPod;
    private Boolean active;
    private TypeObject streamType;
    private StreamAttributes streamAttributes;
    private RoomName roomAttributes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getCrossPod() {
        return crossPod;
    }

    public void setCrossPod(Boolean crossPod) {
        this.crossPod = crossPod;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public TypeObject getStreamType() {
        return streamType;
    }

    public void setStreamType(TypeObject streamType) {
        this.streamType = streamType;
    }

    public StreamAttributes getStreamAttributes() {
        return streamAttributes;
    }

    public void setStreamAttributes(StreamAttributes streamAttributes) {
        this.streamAttributes = streamAttributes;
    }

    public RoomName getRoomAttributes() {
        return roomAttributes;
    }

    public void setRoomAttributes(RoomName roomAttributes) {
        this.roomAttributes = roomAttributes;
    }

    public String getType(){
        return streamType.getType();
    }
}
