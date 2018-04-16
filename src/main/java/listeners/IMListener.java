package listeners;

import model.InboundMessage;
import model.Stream;

public interface IMListener {

    void onIMMessage(InboundMessage message);
    void onIMCreated (Stream stream);
}
