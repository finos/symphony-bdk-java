package utils;

import clients.SymBotClient;
import com.github.tomakehurst.wiremock.common.InvalidInputException;
import configuration.SymConfig;
import it.commons.BotTest;
import model.InboundMessage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.symphony.messageml.exceptions.ProcessingException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;


public class SymMessageParserTest {

  private static InboundMessage mMessage;
  private static List<Long> mentions;
  private static List<String> hashtags;
  private static List<String> cashtags;
  private static Map<String, String> emojis;

  @BeforeClass
  public static void setUp() {
    mMessage = new InboundMessage();
  }

  // messageToText
  @Test
  public void testNormalMessageToText() {
    final SymBotClient botClient = mock(SymBotClient.class);
    when(botClient.getConfig()).thenReturn(new SymConfig());
    final SymMessageParser instance = SymMessageParser.createInstance(botClient);
    final String text = instance.messageToText("<messageML>Hello, World!</messageML>", "{}");
    assertEquals("Hello, World!", text);
  }

  @Test
  public void testError() {
    final SymBotClient botClient = mock(SymBotClient.class);
    when(botClient.getConfig()).thenReturn(new SymConfig());
    final SymMessageParser instance = SymMessageParser.createInstance(botClient);

    instance.messageToText("<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\">"
        + "<p>/abc&nbsp;abc <span class=\"entity\" data-entity-id=\"0\">@Hualiang Luo (SUP)</span> </p></div>", "{}");
  }

  @Test
  public void testInvalidInputMessageToText() {
    final SymBotClient botClient = mock(SymBotClient.class);
    when(botClient.getConfig()).thenReturn(new SymConfig());
    final SymMessageParser instance = SymMessageParser.createInstance(botClient);
    final String text = instance.messageToText("<messageMLHello, World!</messageML", "{}");
    assertNull(text);
  }
  // End messageToText

  // Mentions
  @Test
  public void testNormalGetMentions() {
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(1, mentions.size());
    assertEquals(12987981103609L, mentions.get(0).longValue());
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"},"
        + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103610\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(2, mentions.size());
    assertEquals(12987981103609L, mentions.get(0).longValue());
    assertEquals(12987981103610L, mentions.get(1).longValue());
  }

  @Test
  public void testGetMentionsEmptyData() {
    mMessage.setData("");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(0, mentions.size());
  }

  @Test
  public void testGetMentionsNullData() {
    mMessage.setData(null);
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(0, mentions.size());
  }

  @Test
  public void testGetMentionsBadFormatData() {
    mMessage.setData("{\"0\":{\"id\":[{\"bad-key-type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(0, mentions.size());
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"bad-key-value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(0, mentions.size());
    mMessage.setData("{\"0\":{\"id\":[{\"bad-key-type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"},"
        + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103610\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(1, mentions.size());
    assertEquals(12987981103610L, mentions.get(0).longValue());
  }

  @Test
  public void testGetMentionsUnmatchedType() {
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"unknown-type\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(0, mentions.size());
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"unknown-type\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"},"
        + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103610\"}],\"type\":\"com.symphony.user.mention\"}}");
    mentions = SymMessageParser.getMentions(mMessage);
    assertEquals(1, mentions.size());
    assertEquals(12987981103610L, mentions.get(0).longValue());
  }
  // End Mentions

  // Hashtags
  @Test
  public void testNormalGetHashtags() {
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
    hashtags = SymMessageParser.getHashtags(mMessage);
    assertEquals(0, hashtags.size());

    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.taxonomy.hashtag\",\"value\":\"#hashtag\"}],\"type\":\"org.symphonyoss.taxonomy.hashtag\"}}");
    hashtags = SymMessageParser.getHashtags(mMessage);
    assertEquals(1, hashtags.size());

  }

  @Test
  public void testGetHashtagsEmptyData() {
    mMessage.setData("");
    hashtags = SymMessageParser.getHashtags(mMessage);
    assertEquals(0, hashtags.size());
  }

  @Test
  public void testGetHashtagsNullData() {
    mMessage.setData(null);
    hashtags = SymMessageParser.getHashtags(mMessage);
    assertEquals(0, hashtags.size());
  }
  // End Hashtags

  // Cashtags
  @Test
  public void testNormalGetCashtags() {
    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12987981103609\"}],\"type\":\"com.symphony.user.mention\"}}");
    cashtags = SymMessageParser.getCashtags(mMessage);
    assertEquals(0, cashtags.size());

    mMessage.setData("{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.taxonomy.hashtag\",\"value\":\"$chashtag\"}],\"type\":\"org.symphonyoss.taxonomy.hashtag\"}}");
    cashtags = SymMessageParser.getHashtags(mMessage);
    assertEquals(1, cashtags.size());
  }

  @Test
  public void testGetCashtagsEmptyData() {
    mMessage.setData("");
    cashtags = SymMessageParser.getCashtags(mMessage);
    assertEquals(0, cashtags.size());
  }

  @Test
  public void testGetCashtagsNullData() {
    mMessage.setData(null);
    cashtags = SymMessageParser.getCashtags(mMessage);
    assertEquals(0, cashtags.size());
  }
  // End Cashtags

  // Emoji
  @Test
  public void testNormalGetEmojis() {
    mMessage.setData("{\"0\": {\"type\": \"com.symphony.emoji\", \"data\": {\"annotation\": \":100:\", \"unicode\": \":100:\"}}}");
    emojis = SymMessageParser.getEmojis(mMessage);
    assertEquals(1, emojis.size());
  }

  @Test
  public void testGetEmojisEmpty() {
    mMessage.setData("");
    emojis = SymMessageParser.getEmojis(mMessage);
    assertEquals(0, emojis.size());
  }

  @Test
  public void testGetEmojisNullData() {
    mMessage.setData(null);
    emojis = SymMessageParser.getEmojis(mMessage);
    assertEquals(0, emojis.size());
  }
  // End Emoji
}
