package model.events;

import model.InboundMessage;

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
