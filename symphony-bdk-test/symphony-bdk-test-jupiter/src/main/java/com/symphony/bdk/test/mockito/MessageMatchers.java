package com.symphony.bdk.test.mockito;

import static org.mockito.ArgumentMatchers.argThat;

import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.test.mockito.matcher.MessageContains;
import com.symphony.bdk.test.mockito.matcher.MessageEquals;

public class MessageMatchers {

  public static Message hasContentAndData(Message message) {
    return argThat(new MessageEquals(message));
  }

  public static Message containsContent(String message) {
    return argThat(new MessageContains(message));
  }
}
