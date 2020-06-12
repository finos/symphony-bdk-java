package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.InboundMessage;
import model.Initiator;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MessageSent {

    private InboundMessage message;

    private Initiator initiator;

    public Initiator getInitiator() {
        return initiator;
    }

    public void setInitiator(Initiator initiator) {
        this.initiator = initiator;
    }

    public InboundMessage getMessage() {
        return message;
    }

    public void setMessage(InboundMessage message) {
        this.message = message;
    }


}
