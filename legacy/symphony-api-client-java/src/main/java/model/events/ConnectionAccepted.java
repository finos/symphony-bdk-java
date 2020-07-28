package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.User;

@JsonIgnoreProperties(ignoreUnknown = true)

public class ConnectionAccepted {

    private User fromUser;

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
}
