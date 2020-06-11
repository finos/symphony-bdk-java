package model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import model.Initiator;
import model.Stream;
import model.User;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLeftRoom {
    private Stream stream;
    private User affectedUser;
    private Initiator initiator;

    public Initiator getInitiator() {
        return initiator;
    }

    public void setInitiator(Initiator initiator) {
        this.initiator = initiator;
    }

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
