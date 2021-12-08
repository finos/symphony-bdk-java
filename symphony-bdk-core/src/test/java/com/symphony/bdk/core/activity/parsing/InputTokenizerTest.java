package com.symphony.bdk.core.activity.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;

class InputTokenizerTest {

  @Test
  void emptyContent() {
    assertTrue(getTokens("").isEmpty());
  }

  @Test
  void oneWord() {
    final List<InputToken> tokens = getTokens("hello");

    assertEquals(1, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
  }

  @Test
  void oneWordWithSpaces() {
    final List<InputToken> tokens = getTokens(" hello   ");

    assertEquals(1, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
  }

  @Test
  void twoWords() {
    final List<InputToken> tokens = getTokens("hello world");

    assertEquals(2, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertEquals("world", tokens.get(1).getContent());
  }

  @Test
  void twoWordsWithSpaces() {
    final List<InputToken> tokens = getTokens("  hello   world  ");

    assertEquals(2, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertEquals("world", tokens.get(1).getContent());
  }

  @Test
  void threeWordsWithSpaces() {
    final List<InputToken> tokens = getTokens("  hello   world  azxcds ");

    assertEquals(3, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertEquals("world", tokens.get(1).getContent());
    assertEquals("azxcds", tokens.get(2).getContent());
  }

  @SneakyThrows
  @Test
  void testSimple() {
//    final V4Message message = buildMessage(
//        "<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p><span class=\"entity\" data-entity-id=\"0\">@jane-doe</span></p></div>",
//        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103694\"}],\"type\":\"com.symphony.user.mention\"}}");
//
//    final List<Long> mentions = MessageParser.getMentions(message);
//    assertEquals(1, mentions.size());
//    assertEquals("@jane-doe", PresentationMLParser.getTextContent(message.getMessage()));

    final String presentationML = buildMessageContent("ttt<p>ddd</p> edc rtf");
    assertEquals(InputTokenizer.getTextContent(presentationML), PresentationMLParser.getTextContent(presentationML));
  }

  @SneakyThrows
  @Test
  void oneMention() {
    String presentationML = buildMessageContent("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>");

    assertEquals(InputTokenizer.getTextContent(presentationML), PresentationMLParser.getTextContent(presentationML));

    final List<InputToken> tokenize = InputTokenizer.getTokens(new V4Message().message(presentationML));
    assertEquals(1, tokenize.size());
    assertEquals("@jane-doe", tokenize.get(0).getContent());
    assertTrue(tokenize.get(0).isMention());
  }

  private List<InputToken> getTokens(String textContent) {
    return InputTokenizer.getTokens(buildMessage(textContent));
  }

  private V4Message buildMessage(String textContent) {
    return new V4Message().message(buildMessageContent(textContent));
  }

  private String buildMessageContent(String textContent) {
    return
        "<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p>" + textContent + "</p></div>";
  }

  private V4Message buildMessage(String textContent, String data) {
    return buildMessage(textContent).data(data);
  }
}
