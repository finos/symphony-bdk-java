package model.events;

import model.User;

public class ConnectionAccepted {

    private User fromUser;

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }
}
