package model;

import static org.junit.Assert.assertEquals;
import java.io.File;
import model.OutboundMessage;
import org.junit.Test;

public class OutboundMessageTest {
  @Test
  public void OutboundMessageTest() throws Exception {
    // Arrange
    String message = "aaaaa";
    String data = "aaaaa";

    // Act
    OutboundMessage outboundMessage = new OutboundMessage(message, data);

    // Assert
    String message1 = outboundMessage.getMessage();
    assertEquals("aaaaa", message1);
    assertEquals("aaaaa", outboundMessage.getData());
  }

  @Test
  public void OutboundMessageTest2() throws Exception {
    // Arrange
    String message = "aaaaa";

    // Act
    OutboundMessage outboundMessage = new OutboundMessage(message);

    // Assert
    assertEquals("aaaaa", outboundMessage.getMessage());
  }

  @Test
  public void OutboundMessageTest3() throws Exception {
    // Arrange
    String message = "aaaaa";
    String string = "aaaaa";
    File file = new File("aaaaa");
    File[] attachment = new File[]{file, new File(string), new File(string)};

    // Act
    OutboundMessage outboundMessage = new OutboundMessage(message, string, attachment);

    // Assert
    String message1 = outboundMessage.getMessage();
    String data = outboundMessage.getData();
    assertEquals("aaaaa", message1);
    assertEquals(3, outboundMessage.getAttachment().length);
    assertEquals("aaaaa", data);
  }

  @Test
  public void OutboundMessageTest4() throws Exception {
    // Arrange and Act
    OutboundMessage outboundMessage = new OutboundMessage();

    // Assert
    assertEquals(null, outboundMessage.getMessage());
  }

  @Test
  public void getAttachmentTest() throws Exception {
    // Arrange
    OutboundMessage outboundMessage = new OutboundMessage();

    // Act
    File[] actual = outboundMessage.getAttachment();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDataTest() throws Exception {
    // Arrange
    OutboundMessage outboundMessage = new OutboundMessage();

    // Act
    String actual = outboundMessage.getData();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMessageTest() throws Exception {
    // Arrange
    OutboundMessage outboundMessage = new OutboundMessage();

    // Act
    String actual = outboundMessage.getMessage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAttachmentTest() throws Exception {
    // Arrange
    OutboundMessage outboundMessage = new OutboundMessage();
    File file = new File("aaaaa");
    File file1 = new File("aaaaa");
    File[] fileArray = new File[]{file, file1, new File("aaaaaaaaaaaaaaa")};

    // Act
    outboundMessage.setAttachment(fileArray);

    // Assert
    assertEquals(3, fileArray.length);
  }

  @Test
  public void setDataTest() throws Exception {
    // Arrange
    OutboundMessage outboundMessage = new OutboundMessage();
    String data = "aaaaa";

    // Act
    outboundMessage.setData(data);

    // Assert
    assertEquals("aaaaa", outboundMessage.getData());
  }

  @Test
  public void setMessageTest() throws Exception {
    // Arrange
    OutboundMessage outboundMessage = new OutboundMessage();
    String message = "aaaaa";

    // Act
    outboundMessage.setMessage(message);

    // Assert
    assertEquals("aaaaa", outboundMessage.getMessage());
  }
}
