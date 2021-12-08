package com.symphony.bdk.core.activity.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    assertFalse(tokens.get(0).isMention());
  }

  @Test
  void oneWordWithSpaces() {
    final List<InputToken> tokens = getTokens(" hello   ");

    assertEquals(1, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertFalse(tokens.get(0).isMention());
  }

  @Test
  void twoWords() {
    final List<InputToken> tokens = getTokens("hello world");

    assertEquals(2, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertFalse(tokens.get(0).isMention());
    assertEquals("world", tokens.get(1).getContent());
    assertFalse(tokens.get(1).isMention());
  }

  @Test
  void twoWordsWithSpaces() {
    final List<InputToken> tokens = getTokens("  hello   world  ");

    assertEquals(2, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertFalse(tokens.get(0).isMention());
    assertEquals("world", tokens.get(1).getContent());
    assertFalse(tokens.get(1).isMention());
  }

  @Test
  void threeWordsWithSpaces() {
    final List<InputToken> tokens = getTokens("  hello   world  azxcds ");

    assertEquals(3, tokens.size());
    assertEquals("hello", tokens.get(0).getContent());
    assertFalse(tokens.get(0).isMention());
    assertEquals("world", tokens.get(1).getContent());
    assertFalse(tokens.get(1).isMention());
    assertEquals("azxcds", tokens.get(2).getContent());
    assertFalse(tokens.get(2).isMention());
  }

  @SneakyThrows
  @Test
  void testWithInsideTags() {
    final List<InputToken> tokens = getTokens("ttt<p>ddd</p> edc rtf");
    assertEquals("tttddd", tokens.get(0).getContent());
    assertFalse(tokens.get(0).isMention());
    assertEquals("edc", tokens.get(1).getContent());
    assertFalse(tokens.get(1).isMention());
    assertEquals("rtf", tokens.get(2).getContent());
    assertFalse(tokens.get(2).isMention());
  }

  @SneakyThrows
  @Test
  void oneMention() {
    final List<InputToken> tokenize = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>");

    assertEquals(1, tokenize.size());
    assertEquals("@jane-doe", tokenize.get(0).getContent());
    assertTrue(tokenize.get(0).isMention());
  }

  @SneakyThrows
  @Test
  void textAndOneMention() {
    final List<InputToken> tokenize = getTokens("lorem<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>");

    assertEquals(2, tokenize.size());
    assertEquals("lorem", tokenize.get(0).getContent());
    assertFalse(tokenize.get(0).isMention());
    assertEquals("@jane-doe", tokenize.get(1).getContent());
    assertTrue(tokenize.get(1).isMention());
  }

  @SneakyThrows
  @Test
  void twoMentionsWithSpace() {
    final List<InputToken> tokenize = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span> <span class=\"entity\" data-entity-id=\"0\">@John Doe</span>");

    assertEquals(2, tokenize.size());
    assertEquals("@jane-doe", tokenize.get(0).getContent());
    assertTrue(tokenize.get(0).isMention());
    assertEquals("@John Doe", tokenize.get(1).getContent());
    assertTrue(tokenize.get(1).isMention());
  }

  @SneakyThrows
  @Test
  void twoMentionsWithoutSpace() {
    final List<InputToken> tokenize = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span><span class=\"entity\" data-entity-id=\"0\">@John Doe</span>");

    assertEquals(2, tokenize.size());
    assertEquals("@jane-doe", tokenize.get(0).getContent());
    assertTrue(tokenize.get(0).isMention());
    assertEquals("@John Doe", tokenize.get(1).getContent());
    assertTrue(tokenize.get(1).isMention());
  }

  @SneakyThrows
  @Test
  void twoMentionsAndText() {
    final List<InputToken> tokenize = getTokens("Hello <span class=\"entity\" data-entity-id=\"0\">@jane-doe</span> and <span class=\"entity\" data-entity-id=\"0\">@John Doe</span>");

    assertEquals(4, tokenize.size());
    assertEquals("Hello", tokenize.get(0).getContent());
    assertFalse(tokenize.get(0).isMention());
    assertEquals("@jane-doe", tokenize.get(1).getContent());
    assertTrue(tokenize.get(1).isMention());
    assertEquals("and", tokenize.get(2).getContent());
    assertFalse(tokenize.get(2).isMention());
    assertEquals("@John Doe", tokenize.get(3).getContent());
    assertTrue(tokenize.get(3).isMention());
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
}
