package listeners;

import model.InboundMessage;
import model.Stream;

public interface IMListener extends DatafeedListener {
    void onIMMessage(InboundMessage message);
    void onIMCreated (Stream stream);
}
