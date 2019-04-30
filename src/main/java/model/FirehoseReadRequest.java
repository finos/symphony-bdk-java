package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FirehoseReadRequest {
    private String ackId;
    private int maxMsgs;
    private int timeout;

    public FirehoseReadRequest(String ackId) {
        this.ackId = ackId;
        this.maxMsgs = 100;
        this.timeout = 30;
    }

    public FirehoseReadRequest(String ackId, int maxMsgs, int timeout) {
        this.ackId = ackId;
        this.maxMsgs = maxMsgs;
        this.timeout = timeout;
    }

    public String getAckId() {
        return ackId;
    }

    public void setAckId(String ackId) {
        this.ackId = ackId;
    }

    public int getMaxMsgs() {
        return maxMsgs;
    }

    public void setMaxMsgs(int maxMsgs) {
        this.maxMsgs = maxMsgs;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
