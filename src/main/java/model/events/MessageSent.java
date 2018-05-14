package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.InboundMessage;

@JsonIgnoreProperties(ignoreUnknown = true)

public class MessageSent {

    private InboundMessage message;



    public InboundMessage getMessage() {
        return message;
    }

    public void setMessage(InboundMessage message) {
        this.message = message;
    }


}
