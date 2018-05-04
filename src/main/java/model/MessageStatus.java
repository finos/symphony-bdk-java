package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties

public class MessageStatus {

    List<MessageStatusUser> read;
    List<MessageStatusUser> delivered;
    List<MessageStatusUser> sent;

    public List<MessageStatusUser> getRead() {
        return read;
    }

    public void setRead(List<MessageStatusUser> read) {
        this.read = read;
    }

    public List<MessageStatusUser> getDelivered() {
        return delivered;
    }

    public void setDelivered(List<MessageStatusUser> delivered) {
        this.delivered = delivered;
    }

    public List<MessageStatusUser> getSent() {
        return sent;
    }

    public void setSent(List<MessageStatusUser> sent) {
        this.sent = sent;
    }
}
