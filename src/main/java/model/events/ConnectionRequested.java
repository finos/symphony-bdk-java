package model.events;

import model.User;

public class ConnectionRequested {

    private User toUser;

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }
}
