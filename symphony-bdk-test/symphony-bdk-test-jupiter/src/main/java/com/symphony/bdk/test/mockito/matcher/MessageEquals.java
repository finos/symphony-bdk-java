package com.symphony.bdk.test.mockito.matcher;

import com.symphony.bdk.core.service.message.model.Message;

import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.util.Objects;

@RequiredArgsConstructor
public class MessageEquals implements ArgumentMatcher<Message> {
  private final Message message;

  @Override
  public boolean matches(Message argument) {
    return Objects.equals(message.getContent(), argument.getContent()) && Objects.equals(message.getData(),
        argument.getData());
  }

  @Override
  public Class<?> type() {
    return Message.class;
  }

  @Override
  public String toString() {
    return "message does not match the expected.";
  }
}
