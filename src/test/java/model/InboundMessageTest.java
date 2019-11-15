package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.InboundMessage;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class InboundMessageTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void InboundMessageTest() throws Exception {
    // Arrange and Act
    InboundMessage inboundMessage = new InboundMessage();

    // Assert
    assertEquals(null, inboundMessage.getOriginalFormat());
  }

  @Test
  public void getAttachmentsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    List<Attachment> actual = inboundMessage.getAttachments();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCashtagsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    inboundMessage.getCashtags();
  }

  @Test
  public void getDataTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    String actual = inboundMessage.getData();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDiagnosticTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    String actual = inboundMessage.getDiagnostic();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getExternalRecipientsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    Boolean actual = inboundMessage.getExternalRecipients();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getHashtagsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    inboundMessage.getHashtags();
  }

  @Test
  public void getMentionsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act and Assert
    thrown.expect(IllegalArgumentException.class);
    inboundMessage.getMentions();
  }

  @Test
  public void getMessageIdTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    String actual = inboundMessage.getMessageId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    String actual = inboundMessage.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginalFormatTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    String actual = inboundMessage.getOriginalFormat();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    Stream actual = inboundMessage.getStream();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTimestampTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    Long actual = inboundMessage.getTimestamp();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserAgentTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    String actual = inboundMessage.getUserAgent();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();

    // Act
    User actual = inboundMessage.getUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAttachmentsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    ArrayList<Attachment> arrayList = new ArrayList<Attachment>();
    arrayList.add(new Attachment());

    // Act
    inboundMessage.setAttachments(arrayList);

    // Assert
    assertSame(arrayList, inboundMessage.getAttachments());
  }

  @Test
  public void setDataTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    String data = "aaaaa";

    // Act
    inboundMessage.setData(data);

    // Assert
    assertEquals("aaaaa", inboundMessage.getData());
  }

  @Test
  public void setDiagnosticTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    String diagnostic = "aaaaa";

    // Act
    inboundMessage.setDiagnostic(diagnostic);

    // Assert
    assertEquals("aaaaa", inboundMessage.getDiagnostic());
  }

  @Test
  public void setExternalRecipientsTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    Boolean externalRecipients = new Boolean(true);

    // Act
    inboundMessage.setExternalRecipients(externalRecipients);

    // Assert
    assertEquals(Boolean.valueOf(true), inboundMessage.getExternalRecipients());
  }

  @Test
  public void setMessageIdTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    String messageId = "aaaaa";

    // Act
    inboundMessage.setMessageId(messageId);

    // Assert
    assertEquals("aaaaa", inboundMessage.getMessageId());
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    String message = "aaaaa";

    // Act
    inboundMessage.setMessage(message);

    // Assert
    assertEquals("aaaaa", inboundMessage.getMessage());
  }

  @Test
  public void setOriginalFormatTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    String originalFormat = "aaaaa";

    // Act
    inboundMessage.setOriginalFormat(originalFormat);

    // Assert
    assertEquals("aaaaa", inboundMessage.getOriginalFormat());
  }

  @Test
  public void setStreamTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    Stream stream = new Stream();

    // Act
    inboundMessage.setStream(stream);

    // Assert
    assertSame(stream, inboundMessage.getStream());
  }

  @Test
  public void setTimestampTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    Long timestamp = new Long(1L);

    // Act
    inboundMessage.setTimestamp(timestamp);

    // Assert
    assertEquals(Long.valueOf(1L), inboundMessage.getTimestamp());
  }

  @Test
  public void setUserAgentTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    String userAgent = "aaaaa";

    // Act
    inboundMessage.setUserAgent(userAgent);

    // Assert
    assertEquals("aaaaa", inboundMessage.getUserAgent());
  }

  @Test
  public void setUserTest() throws Exception {
    // Arrange
    InboundMessage inboundMessage = new InboundMessage();
    User user = new User();

    // Act
    inboundMessage.setUser(user);

    // Assert
    assertSame(user, inboundMessage.getUser());
  }
}
