package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.StreamListItem;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class StreamListItemTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void StreamListItemTest() throws Exception {
    // Arrange and Act
    StreamListItem streamListItem = new StreamListItem();

    // Assert
    assertEquals(null, streamListItem.getStreamType());
  }

  @Test
  public void getActiveTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act
    Boolean actual = streamListItem.getActive();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCrossPodTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act
    Boolean actual = streamListItem.getCrossPod();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act
    String actual = streamListItem.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomAttributesTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act
    RoomName actual = streamListItem.getRoomAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamAttributesTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act
    StreamAttributes actual = streamListItem.getStreamAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTypeTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act
    TypeObject actual = streamListItem.getStreamType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTypeTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    streamListItem.getType();
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();
    Boolean active = new Boolean(true);

    // Act
    streamListItem.setActive(active);

    // Assert
    assertEquals(Boolean.valueOf(true), streamListItem.getActive());
  }

  @Test
  public void setCrossPodTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();
    Boolean crossPod = new Boolean(true);

    // Act
    streamListItem.setCrossPod(crossPod);

    // Assert
    assertEquals(Boolean.valueOf(true), streamListItem.getCrossPod());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();
    String id = "aaaaa";

    // Act
    streamListItem.setId(id);

    // Assert
    assertEquals("aaaaa", streamListItem.getId());
  }

  @Test
  public void setRoomAttributesTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();
    RoomName roomName = new RoomName();

    // Act
    streamListItem.setRoomAttributes(roomName);

    // Assert
    assertSame(roomName, streamListItem.getRoomAttributes());
  }

  @Test
  public void setStreamAttributesTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();
    StreamAttributes streamAttributes = new StreamAttributes();

    // Act
    streamListItem.setStreamAttributes(streamAttributes);

    // Assert
    assertSame(streamAttributes, streamListItem.getStreamAttributes());
  }

  @Test
  public void setStreamTypeTest() throws Exception {
    // Arrange
    StreamListItem streamListItem = new StreamListItem();
    TypeObject streamType = new TypeObject();

    // Act
    streamListItem.setStreamType(streamType);

    // Assert
    assertEquals(null, streamListItem.getType());
  }
}
