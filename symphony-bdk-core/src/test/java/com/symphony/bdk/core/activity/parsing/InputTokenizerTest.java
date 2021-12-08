package com.symphony.bdk.core.activity.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    final List<InputToken<?>> tokens = getTokens("hello");

    assertEquals(1, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
  }

  @Test
  void oneWordWithSpaces() {
    final List<InputToken<?>> tokens = getTokens(" hello   ");

    assertEquals(1, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
  }

  @Test
  void twoWords() {
    final List<InputToken<?>> tokens = getTokens("hello world");

    assertEquals(2, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
    assertIsStringToken("world", tokens.get(1));
  }

  @Test
  void twoWordsWithSpaces() {
    final List<InputToken<?>> tokens = getTokens("  hello   world  ");

    assertEquals(2, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
    assertIsStringToken("world", tokens.get(1));
  }

  @Test
  void threeWordsWithSpaces() {
    final List<InputToken<?>> tokens = getTokens("  hello   world  azxcds ");

    assertEquals(3, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
    assertIsStringToken("world", tokens.get(1));
    assertIsStringToken("azxcds", tokens.get(2));
  }

  @SneakyThrows
  @Test
  void testWithInsideTags() {
    final List<InputToken<?>> tokens = getTokens("ttt<p>ddd</p> edc rtf");
    assertIsStringToken("tttddd", tokens.get(0));
    assertIsStringToken("edc", tokens.get(1));
    assertIsStringToken("rtf", tokens.get(2));
  }

  @SneakyThrows
  @Test
  void oneMention() {
    final List<InputToken<?>> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103694\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(1, tokens.size());
    assertIsMentionToken("@jane-doe", 12987981103694L, tokens.get(0));
  }

  @SneakyThrows
  @Test
  void textAndOneMention() {
    final List<InputToken<?>> tokens = getTokens("lorem<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103694\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(2, tokens.size());
    assertIsStringToken("lorem", tokens.get(0));
    assertIsMentionToken("@jane-doe", 12987981103694L, tokens.get(1));
  }

  @SneakyThrows
  @Test
  void twoMentionsWithSpace() {
    final List<InputToken<?>> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span> <span class=\"entity\" data-entity-id=\"1\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103694\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103693\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(2, tokens.size());
    assertIsMentionToken("@jane-doe", 12987981103694L, tokens.get(0));
    assertIsMentionToken("@John Doe", 12987981103693L, tokens.get(1));
  }

  @SneakyThrows
  @Test
  void twoMentionsWithoutSpace() {
    final List<InputToken<?>> tokenize = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span><span class=\"entity\" data-entity-id=\"1\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103694\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103693\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(2, tokenize.size());
    assertIsMentionToken("@jane-doe", 12987981103694L, tokenize.get(0));
    assertIsMentionToken("@John Doe", 12987981103693L, tokenize.get(1));
  }

  @SneakyThrows
  @Test
  void twoMentionsAndText() {
    final List<InputToken<?>> tokenize = getTokens("Hello <span class=\"entity\" data-entity-id=\"0\">@jane-doe</span> and <span class=\"entity\" data-entity-id=\"1\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103694\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103693\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(4, tokenize.size());
    assertIsStringToken("Hello", tokenize.get(0));
    assertIsMentionToken("@jane-doe", 12987981103694L, tokenize.get(1));
    assertIsStringToken("and", tokenize.get(2));
    assertIsMentionToken("@John Doe", 12987981103693L, tokenize.get(3));
  }

  private List<InputToken<?>> getTokens(String textContent) {
    return new InputTokenizer(buildMessage(textContent)).getTokens();
  }

  private List<InputToken<?>> getTokens(String textContent, String data) {
    return new InputTokenizer(buildMessage(textContent).data(data)).getTokens();
  }

  private V4Message buildMessage(String textContent) {
    return new V4Message()
        .message("<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p>" + textContent + "</p></div>");
  }

  private void assertIsStringToken(String expectedText, InputToken<?> actualInputToken) {
    assertEquals(expectedText, actualInputToken.getContentAsString());
    assertTrue(actualInputToken.getContent() instanceof String);
  }

  private void assertIsMentionToken(String expectedText, Long expectedUserId, InputToken<?> actualInputToken) {
    assertTrue(actualInputToken.getContent() instanceof Mention);
    Mention mention = (Mention) actualInputToken.getContent();
    assertEquals(expectedText, mention.getMentionText());
    assertEquals(expectedUserId, mention.getUserId());
  }
}
