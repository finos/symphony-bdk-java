package com.symphony.bdk.core.service.message.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.core.service.message.exception.MessageCreationException;

import org.junit.jupiter.api.Test;

class MessageTest {

  @Test
  void cannotCreateMessageWithNoContent() {
    assertThrows(MessageCreationException.class, Message.builder()::build);
  }

  @Test
  void checkDefaultVersion() {
    assertEquals("2.0", Message.builder().content("foobar").build().getVersion());
  }

  @Test
  void checkMessageMLAppendedToContentIfNotSet() {
    assertEquals("<messageML>hello</messageML>", Message.builder().content("hello").build().getContent());
  }

  @Test
  void checkMessageMLNotAppendedToContentIfSet() {
    assertEquals("<messageML>hello</messageML>", Message.builder().content("<messageML>hello</messageML>").build().getContent());
  }

  @Test
  void checkMessageSilentValueIfSet() {
    assertEquals(Boolean.FALSE, Message.builder().content("<messageML>hello</messageML>").silent(Boolean.FALSE).build().getSilent());
  }

  @Test
  void checkMessageSilentDefaultValue() {
    assertEquals(Boolean.TRUE, Message.builder().content("<messageML>hello</messageML>").build().getSilent());
  }
}
