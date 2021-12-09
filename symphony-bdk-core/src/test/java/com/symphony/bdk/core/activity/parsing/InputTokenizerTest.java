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
    final List<Object> tokens = getTokens("hello");

    assertEquals(1, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
  }

  @Test
  void oneWordWithSpaces() {
    final List<Object> tokens = getTokens(" hello   ");

    assertEquals(1, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
  }

  @Test
  void twoWords() {
    final List<Object> tokens = getTokens("hello world");

    assertEquals(2, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
    assertIsStringToken("world", tokens.get(1));
  }

  @Test
  void twoWordsWithSpaces() {
    final List<Object> tokens = getTokens("  hello   world  ");

    assertEquals(2, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
    assertIsStringToken("world", tokens.get(1));
  }

  @Test
  void threeWordsWithSpaces() {
    final List<Object> tokens = getTokens("  hello   world  azxcds ");

    assertEquals(3, tokens.size());
    assertIsStringToken("hello", tokens.get(0));
    assertIsStringToken("world", tokens.get(1));
    assertIsStringToken("azxcds", tokens.get(2));
  }

  @SneakyThrows
  @Test
  void testWithInsideTags() {
    final List<Object> tokens = getTokens("ttt<p>ddd</p> edc rtf");
    assertIsStringToken("tttddd", tokens.get(0));
    assertIsStringToken("edc", tokens.get(1));
    assertIsStringToken("rtf", tokens.get(2));
  }

  @Test
  void oneMention() {
    final List<Object> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(1, tokens.size());
    assertIsMentionToken("@jane-doe", "jane-doe", 12345678L, tokens.get(0));
  }

  @Test
  void oneMentionWithoutMatchingJsonEntity() {
    final List<Object> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{}");

    assertEquals(1, tokens.size());
    assertIsStringToken("@jane-doe", tokens.get(0));
  }

  @Test
  void textAndOneMention() {
    final List<Object> tokens = getTokens("lorem<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(2, tokens.size());
    assertIsStringToken("lorem", tokens.get(0));
    assertIsMentionToken("@jane-doe", "jane-doe",12345678L, tokens.get(1));
  }

  @Test
  void twoMentionsWithSpace() {
    final List<Object> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span> <span class=\"entity\" data-entity-id=\"1\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345679\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(2, tokens.size());
    assertIsMentionToken("@jane-doe", "jane-doe",12345678L, tokens.get(0));
    assertIsMentionToken("@John Doe", "John Doe",12345679L, tokens.get(1));
  }

  @Test
  void twoMentionsWithoutSpace() {
    final List<Object> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span><span class=\"entity\" data-entity-id=\"1\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345679\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(2, tokens.size());
    assertIsMentionToken("@jane-doe", "jane-doe",12345678L, tokens.get(0));
    assertIsMentionToken("@John Doe", "John Doe",12345679L, tokens.get(1));
  }

  @Test
  void twoMentionsAndText() {
    final List<Object> tokens = getTokens("Hello <span class=\"entity\" data-entity-id=\"0\">@jane-doe</span> and <span class=\"entity\" data-entity-id=\"1\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"},\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345679\"}],\"type\":\"com.symphony.user.mention\"}}");

    assertEquals(4, tokens.size());
    assertIsStringToken("Hello", tokens.get(0));
    assertIsMentionToken("@jane-doe", "jane-doe",12345678L, tokens.get(1));
    assertIsStringToken("and", tokens.get(2));
    assertIsMentionToken("@John Doe", "John Doe",12345679L, tokens.get(3));
  }

  @Test
  void oneCashtag() {
    final List<Object> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">$mycashtag</span>",
        "{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.fin.security.id.ticker\",\"value\":\"mycashtag\"}],\"type\":\"org.symphonyoss.fin.security\"}}");

    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof Cashtag);

    Cashtag cashtag = (Cashtag) tokens.get(0);
    assertEquals("$mycashtag", cashtag.getText());
    assertEquals("mycashtag", cashtag.getValue());
  }

  @Test
  void oneHashtag() {
    final List<Object> tokens = getTokens("<span class=\"entity\" data-entity-id=\"0\">#myhashtag</span>",
        "{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.taxonomy.hashtag\",\"value\":\"myhashtag\"}],\"type\":\"org.symphonyoss.taxonomy\"}}");

    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof Hashtag);

    Hashtag cashtag = (Hashtag) tokens.get(0);
    assertEquals("#myhashtag", cashtag.getText());
    assertEquals("myhashtag", cashtag.getValue());
  }

  private List<Object> getTokens(String textContent) {
    return new InputTokenizer(buildMessage(textContent)).getTokens();
  }

  private List<Object> getTokens(String textContent, String data) {
    return new InputTokenizer(buildMessage(textContent).data(data)).getTokens();
  }

  private V4Message buildMessage(String textContent) {
    return new V4Message()
        .message("<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p>" + textContent + "</p></div>");
  }

  private void assertIsStringToken(String expectedText, Object actualInputToken) {
    assertTrue(actualInputToken instanceof String);
    assertEquals(expectedText, actualInputToken);
  }

  private void assertIsMentionToken(String expectedText, String expectedDisplayName, Long expectedUserId, Object actualInputToken) {
    assertTrue(actualInputToken instanceof Mention);
    Mention mention = (Mention) actualInputToken;
    assertEquals(expectedText, mention.getText());
    assertEquals(expectedDisplayName, mention.getUserDisplayName());
    assertEquals(expectedUserId, mention.getUserId());
  }
}
