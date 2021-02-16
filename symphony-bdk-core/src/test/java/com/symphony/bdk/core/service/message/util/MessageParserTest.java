package com.symphony.bdk.core.service.message.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.service.message.exception.MessageParserException;
import com.symphony.bdk.gen.api.model.V4Message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class MessageParserTest {

  private V4Message message;

  @BeforeEach
  void setUp() {
    message = mock(V4Message.class);
    when(message.getData()).thenReturn(getEntityData());
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
  public void testJsonProcessingException() {
    when(message.getData()).thenReturn("Unparsable json");
    try {
      MessageParser.getMentions(message);
      fail("Should have throw a MessageParserException");
    } catch (Exception e) {
      assertTrue(e instanceof MessageParserException);
    }
  }

  private String getEntityData() {
    return "{\n"
        + "  \"0\": {\n"
        + "    \"id\": [\n"
        + "      {\n"
        + "        \"type\": \"com.symphony.user.userId\",\n"
        + "        \"value\": \"13056700580915\"\n"
        + "      }\n"
        + "    ],\n"
        + "    \"type\": \"com.symphony.user.mention\"\n"
        + "  },\n"
        + "  \"1\": {\n"
        + "    \"id\": [\n"
        + "      {\n"
        + "        \"type\": \"org.symphonyoss.taxonomy.hashtag\",\n"
        + "        \"value\": \"bot\"\n"
        + "      }\n"
        + "    ],\n"
        + "    \"type\": \"org.symphonyoss.taxonomy\",\n"
        + "    \"version\": \"1.0\"\n"
        + "  },\n"
        + "  \"2\": {\n"
        + "    \"id\": [\n"
        + "      {\n"
        + "        \"type\": \"org.symphonyoss.fin.security.id.ticker\",\n"
        + "        \"value\": \"hello\"\n"
        + "      }\n"
        + "    ],\n"
        + "    \"type\": \"org.symphonyoss.fin.security\",\n"
        + "    \"version\": \"1.0\"\n"
        + "  },\n"
        + "  \"3\": {\n"
        + "    \"type\": \"com.symphony.emoji\",\n"
        + "    \"version\": \"1.0\",\n"
        + "    \"data\": {\n"
        + "      \"annotation\": \"grinning\",\n"
        + "      \"family\": \"\",\n"
        + "      \"size\": \"normal\",\n"
        + "      \"unicode\": \"\uD83D\uDE00\"\n"
        + "    }\n"
        + "  },\n"
        + "  \"4\": {\n"
        + "    \"id\": [\n"
        + "      {\n"
        + "        \"type\": \"com.symphony.user.userId\",\n"
        + "        \"value\": \"1305690252351\"\n"
        + "      }\n"
        + "    ],\n"
        + "    \"type\": \"com.symphony.user.mention\"\n"
        + "  }\n"
        + "}";
  }
}
