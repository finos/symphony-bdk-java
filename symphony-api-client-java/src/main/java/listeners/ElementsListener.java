package listeners;

import model.User;
import model.events.SymphonyElementsAction;

public interface ElementsListener extends DatafeedListener {
    void onElementsAction(User initiator, SymphonyElementsAction symphonyElementsAction);
}
