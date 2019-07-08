package listeners;

import model.User;

public interface ConnectionListener extends DatafeedListener {
    void onConnectionAccepted(User user);
    void onConnectionRequested(User user);
}
