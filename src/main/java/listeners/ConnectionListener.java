package listeners;

import model.User;
import model.events.ConnectionRequested;

public interface ConnectionListener {

    void onConnectionAccepted(User user);

    void onConnectionRequested(User user);
}
