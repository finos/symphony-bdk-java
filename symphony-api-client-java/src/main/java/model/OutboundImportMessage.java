package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OutboundImportMessage {
    private String message;
    private String data;
    private long intendedMessageTimestamp;
    private long intendedMessageFromUserId;
    private String originatingSystemId;
    private String originalMessageId;
    private String streamId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getIntendedMessageTimestamp() {
        return intendedMessageTimestamp;
    }

    public void setIntendedMessageTimestamp(long intendedMessageTimestamp) {
        this.intendedMessageTimestamp = intendedMessageTimestamp;
    }

    public long getIntendedMessageFromUserId() {
        return intendedMessageFromUserId;
    }

    public void setIntendedMessageFromUserId(long intendedMessageFromUserId) {
        this.intendedMessageFromUserId = intendedMessageFromUserId;
    }

    public String getOriginatingSystemId() {
        return originatingSystemId;
    }

    public void setOriginatingSystemId(String originatingSystemId) {
        this.originatingSystemId = originatingSystemId;
    }

    public String getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(String originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public String getStreamId() {
        return streamId;
    }

    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}
