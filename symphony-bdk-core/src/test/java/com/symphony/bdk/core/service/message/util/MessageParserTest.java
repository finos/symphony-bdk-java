package com.symphony.bdk.core.service.message.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.service.message.exception.MessageParserException;
import com.symphony.bdk.core.test.JsonHelper;
import com.symphony.bdk.gen.api.model.V4Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class MessageParserTest {

  private V4Message message;

  @BeforeEach
  void setUp() throws IOException {
    message = mock(V4Message.class);
    when(message.getData()).thenReturn(JsonHelper.readFromClasspath("/message/message_entity_data.json"));
  }

  @Test
  public void testGetMentions() throws MessageParserException {
    List<Long> mentions = MessageParser.getMentions(message);
    assertEquals(2, mentions.size());
    assertEquals(13056700580915L, mentions.get(0));
    assertEquals(1305690252351L, mentions.get(1));
  }

  @Test
  public void testGetHashtags() throws MessageParserException {
    List<String> hashtags = MessageParser.getHashtags(message);
    assertEquals(1, hashtags.size());
    assertEquals("bot", hashtags.get(0));
  }

  @Test
  public void testGetCashtags() throws MessageParserException {
    List<String> cashtags = MessageParser.getCashtags(message);
    assertEquals(1, cashtags.size());
    assertEquals("hello", cashtags.get(0));
  }

  @Test
  public void testGetEmojis() throws MessageParserException {
    Map<String, String> emojisMap = MessageParser.getEmojis(message);
    assertEquals(1, emojisMap.size());
    assertEquals("{grinning=\uD83D\uDE00}", emojisMap.toString());
  }

  @Test
  public void testEmptyData() throws MessageParserException {
    when(message.getData()).thenReturn("");
    assertTrue(MessageParser.getCashtags(message).isEmpty());
    assertTrue(MessageParser.getEmojis(message).isEmpty());
  }

  @Test
  public void testNullData() throws MessageParserException {
    when(message.getData()).thenReturn(null);
    assertTrue(MessageParser.getMentions(message).isEmpty());
    assertTrue(MessageParser.getEmojis(message).isEmpty());
  }

  @Test
  public void testGetMentionsWhenValueNotFound() throws MessageParserException {
    when(message.getData()).thenReturn("{\"0\": {\"id\": [], \"type\": \"com.symphony.user.mention\"}}");
    assertTrue(MessageParser.getMentions(message).isEmpty());
  }

  @Test
  public void testGetEmojisWhenValueNotFound() throws MessageParserException {
    when(message.getData()).thenReturn(
        "{\"0\": {\"type\": \"com.symphony.emoji\",\"version\": \"1.0\",\"data\": {\"annotation\": \"grinning\"}}}");
    assertTrue(MessageParser.getEmojis(message).isEmpty());
  }

  @Test
  public void testJsonProcessingException() {
    when(message.getData()).thenReturn("Unparsable json");
    assertThrows(MessageParserException.class, () -> MessageParser.getMentions(message));
  }
}
