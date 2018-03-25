package listeners;

import model.Message;
import model.Stream;

public interface IMListener {

    void onIMMessage(Message message);
    void onIMCreated (Stream stream);
}
