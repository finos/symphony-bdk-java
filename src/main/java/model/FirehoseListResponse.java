package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FirehoseListResponse {
    private long creationDate;
    private String firehoseId;

    public FirehoseListResponse() {}

    public FirehoseListResponse(long creationDate, String firehoseId) {
        this.creationDate = creationDate;
        this.firehoseId = firehoseId;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getFirehoseId() {
        return firehoseId;
    }

    public void setFirehoseId(String firehoseId) {
        this.firehoseId = firehoseId;
    }
}
