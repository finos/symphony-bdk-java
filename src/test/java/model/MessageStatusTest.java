package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.MessageStatus;
import model.MessageStatusUser;
import org.junit.Test;

public class MessageStatusTest {
  @Test
  public void MessageStatusTest() throws Exception {
    // Arrange and Act
    MessageStatus messageStatus = new MessageStatus();

    // Assert
    assertEquals(null, messageStatus.getDelivered());
  }

  @Test
  public void getDeliveredTest() throws Exception {
    // Arrange
    MessageStatus messageStatus = new MessageStatus();

    // Act
    List<MessageStatusUser> actual = messageStatus.getDelivered();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getReadTest() throws Exception {
    // Arrange
    MessageStatus messageStatus = new MessageStatus();

    // Act
    List<MessageStatusUser> actual = messageStatus.getRead();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSentTest() throws Exception {
    // Arrange
    MessageStatus messageStatus = new MessageStatus();

    // Act
    List<MessageStatusUser> actual = messageStatus.getSent();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setDeliveredTest() throws Exception {
    // Arrange
    MessageStatus messageStatus = new MessageStatus();
    ArrayList<MessageStatusUser> arrayList = new ArrayList<MessageStatusUser>();
    arrayList.add(new MessageStatusUser());

    // Act
    messageStatus.setDelivered(arrayList);

    // Assert
    assertSame(arrayList, messageStatus.getDelivered());
  }

  @Test
  public void setReadTest() throws Exception {
    // Arrange
    MessageStatus messageStatus = new MessageStatus();
    ArrayList<MessageStatusUser> arrayList = new ArrayList<MessageStatusUser>();
    arrayList.add(new MessageStatusUser());

    // Act
    messageStatus.setRead(arrayList);

    // Assert
    assertSame(arrayList, messageStatus.getRead());
  }

  @Test
  public void setSentTest() throws Exception {
    // Arrange
    MessageStatus messageStatus = new MessageStatus();
    ArrayList<MessageStatusUser> arrayList = new ArrayList<MessageStatusUser>();
    arrayList.add(new MessageStatusUser());

    // Act
    messageStatus.setSent(arrayList);

    // Assert
    assertSame(arrayList, messageStatus.getSent());
  }
}
