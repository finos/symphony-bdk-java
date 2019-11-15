package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.MessagesClient;
import configuration.SymConfig;
import java.util.HashMap;
import java.util.List;
import model.FileAttachment;
import model.InboundMessage;
import model.InboundMessageList;
import model.InboundShare;
import model.MessageStatus;
import model.OutboundMessage;
import model.OutboundShare;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class MessagesClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void MessagesClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new MessagesClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void forwardMessageTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    InboundMessage message = new InboundMessage();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.forwardMessage(streamId, message);
  }

  @Test
  public void getAttachmentTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    String attachmentId = "aaaka";
    String messageId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.getAttachment(streamId, attachmentId, messageId);
  }

  @Test
  public void getMessageAttachmentsTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    InboundMessage message = new InboundMessage();

    // Act
    List<FileAttachment> actual = messagesClient.getMessageAttachments(message);

    // Assert
    assertEquals(0, actual.size());
  }

  @Test
  public void getMessageStatusTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String messageId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.getMessageStatus(messageId);
  }

  @Test
  public void getMessagesFromStreamTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    long since = 1L;
    int skip = 2561;
    int limit = 1;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.getMessagesFromStream(streamId, since, skip, limit);
  }

  @Test
  public void messageSearchTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    HashMap<String, String> hashMap = new HashMap<String, String>();
    hashMap.put("aaaaa", "kaaaa");
    int skip = 1;
    int limit = 1;
    boolean orderAscending = true;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.messageSearch(hashMap, skip, limit, orderAscending);
  }

  @Test
  public void sendMessageTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    OutboundMessage message = new OutboundMessage();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.sendMessage(streamId, message);
  }

  @Test
  public void sendTaggedMessageTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    OutboundMessage message = new OutboundMessage();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.sendTaggedMessage(streamId, message);
  }

  @Test
  public void shareContentTest() throws Exception {
    // Arrange
    MessagesClient messagesClient = new MessagesClient(new SymOBOClient(new SymConfig(), null));
    String streamId = "aaaaa";
    OutboundShare shareContent = new OutboundShare();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    messagesClient.shareContent(streamId, shareContent);
  }
}
