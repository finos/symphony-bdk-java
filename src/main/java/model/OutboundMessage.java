package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.File;
@JsonIgnoreProperties(ignoreUnknown = true)

public class OutboundMessage {
    String message;
    String data;
    File[] attachment;

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

    public File[] getAttachment() {
        return attachment;
    }

    public void setAttachment(File[] attachment) {
        this.attachment = attachment;
    }
}
