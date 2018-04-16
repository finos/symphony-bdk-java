package model;

public class StreamInfo {
    private String id;
    private Boolean crossPod;
    private String origin;
    private Boolean active;
    private Long lastMessageDate;
    private String streamType;
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

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Long getLastMessageDate() {
        return lastMessageDate;
    }

    public void setLastMessageDate(Long lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String getStreamType() {
        return streamType;
    }

    public void setStreamType(String streamType) {
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
}
