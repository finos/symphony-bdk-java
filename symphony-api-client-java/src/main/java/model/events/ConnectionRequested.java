package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConnectionRequested {

    private User toUser;

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}
