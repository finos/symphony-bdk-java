package com.symphony.ms.songwriter.internal.message;

import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public interface MessageService {

  void sendMessage(String streamId, SymphonyMessage message);

}
