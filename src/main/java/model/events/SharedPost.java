package model.events;

import model.Message;

public class SharedPost {

    private Message message;
    private Message sharedMessage;

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public Message getSharedMessage() {
        return sharedMessage;
    }

    public void setSharedMessage(Message sharedMessage) {
        this.sharedMessage = sharedMessage;
    }
}
