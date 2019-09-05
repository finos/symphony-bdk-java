package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Stream;
import model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserJoinedRoom {
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
