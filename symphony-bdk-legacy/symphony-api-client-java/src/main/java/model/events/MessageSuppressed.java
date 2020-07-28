package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Stream;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageSuppressed {
    private String messageId;
    private Stream stream;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }
}
