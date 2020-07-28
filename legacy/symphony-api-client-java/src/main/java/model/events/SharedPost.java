package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.InboundMessage;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SharedPost {
    private InboundMessage message;
    private InboundMessage sharedMessage;

    public InboundMessage getMessage() {
        return message;
    }

    public void setMessage(InboundMessage message) {
        this.message = message;
    }

    public InboundMessage getSharedMessage() {
        return sharedMessage;
    }

    public void setSharedMessage(InboundMessage sharedMessage) {
        this.sharedMessage = sharedMessage;
    }
}
