package com.symphony.bdk.test.mockito.matcher;

import com.symphony.bdk.core.service.message.model.Message;

import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

@RequiredArgsConstructor
public class MessageContains implements ArgumentMatcher<Message> {
  private final String message;

  @Override
  public boolean matches(Message argument) {
    return argument.getContent().contains(message);
  }

  @Override
  public String toString() {
    return String.format("message does not contain '%s'.", message);
  }
}
