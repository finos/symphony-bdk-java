package com.symphony.bdk.core.service.message.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
  void checkMessageMLNotAppendedToContentIfSetWithSpaces() {
    assertEquals("<messageML>hello</messageML>", Message.builder().content(" <messageML>hello</messageML> ").build().getContent());
  }

  @Test
  void checkMessageMLWithoutClosingTagThrowsException() {
    MessageCreationException exception = assertThrows(MessageCreationException.class, Message.builder().content("<messageML>hello")::build);
    assertEquals("Malformed <messageML> tag. Missing closing tag", exception.getMessage());
  }

  @Test
  void checkMessageMLWithoutOpeningTagThrowsException() {
    MessageCreationException exception = assertThrows(MessageCreationException.class, Message.builder().content("hello</messageML>")::build);
    assertEquals("Malformed <messageML> tag. Missing opening tag", exception.getMessage());
  }

  @Test
  void checkMessageSilentValueIfSet() {
    assertEquals(Boolean.FALSE, Message.builder().content("<messageML>hello</messageML>").silent(Boolean.FALSE).build().getSilent());
  }

  @Test
  void checkMessageSilentDefaultValue() {
    assertEquals(Boolean.TRUE, Message.builder().content("<messageML>hello</messageML>").build().getSilent());
  }

  @Test
  void checkBetaModeNotAddedByDefault() {
    Message message = Message.builder().content("hello").build();
    assertEquals("<messageML>hello</messageML>", message.getContent());
    assertNull(message.getBeta());
  }

  @Test
  void checkBetaModeAddedWhenTrue() {
    Message message = Message.builder().content("hello").beta(true).build();
    assertEquals("<messageML beta=\"true\">hello</messageML>", message.getContent());
    assertEquals(Boolean.TRUE, message.getBeta());
  }

  @Test
  void checkBetaModeNotAddedWhenFalse() {
    Message message = Message.builder().content("hello").beta(false).build();
    assertEquals("<messageML>hello</messageML>", message.getContent());
    assertEquals(Boolean.FALSE, message.getBeta());
  }

  @Test
  void checkBetaTrueWithPreWrappedContentThrowsException() {
    MessageCreationException exception = assertThrows(
        MessageCreationException.class,
        () -> Message.builder().content("<messageML>hello</messageML>").beta(true).build()
    );
    assertTrue(exception.getMessage().contains("Cannot set beta=true when content is already wrapped"));
  }

  @Test
  void checkBetaTrueWithPreWrappedBetaContentAllowed() {
    Message message = Message.builder()
        .content("<messageML beta=\"true\">hello</messageML>")
        .beta(true)
        .build();
    assertEquals("<messageML beta=\"true\">hello</messageML>", message.getContent());
  }
}
