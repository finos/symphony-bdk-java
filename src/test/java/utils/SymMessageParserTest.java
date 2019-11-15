package utils;

import static org.junit.Assert.assertEquals;
import clients.SymBotClient;
import java.util.List;
import model.InboundMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;
import utils.SymMessageParser;

public class SymMessageParserTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void createInstanceTest() throws Exception {
    // Arrange
    SymBotClient botClient = null;

    // Act
    SymMessageParser.createInstance(botClient);
  }

  @Test
  public void getCashtagsTest() throws Exception {
    // Arrange
    InboundMessage message = new InboundMessage();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymMessageParser.getCashtags(message);
  }

  @Test
  public void getHashtagsTest() throws Exception {
    // Arrange
    InboundMessage message = new InboundMessage();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymMessageParser.getHashtags(message);
  }

  @Test
  public void getMentionsTest() throws Exception {
    // Arrange
    InboundMessage message = new InboundMessage();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    SymMessageParser.getMentions(message);
  }
}
