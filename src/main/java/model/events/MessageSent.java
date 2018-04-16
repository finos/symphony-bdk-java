package model.events;

import model.InboundMessage;


public class MessageSent {

    private InboundMessage message;



    public InboundMessage getMessage() {
        return message;
    }

    public void setMessage(InboundMessage message) {
        this.message = message;
    }


}
