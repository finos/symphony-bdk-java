package model.events;

import model.Stream;
import model.User;

public class RoomMemberPromotedToOwner {
    private Stream stream;
    private User affectedUser;

    public Stream getStream() {
        return stream;
    }

    public void setStream(Stream stream) {
        this.stream = stream;
    }

    public User getAffectedUser() {
        return affectedUser;
    }

    public void setAffectedUser(User affectedUser) {
        this.affectedUser = affectedUser;
    }
}
