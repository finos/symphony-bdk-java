package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.StreamInfo;
import org.junit.Test;

public class StreamInfoTest {
  @Test
  public void StreamInfoTest() throws Exception {
    // Arrange and Act
    StreamInfo streamInfo = new StreamInfo();

    // Assert
    assertEquals(null, streamInfo.getOrigin());
  }

  @Test
  public void getActiveTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    Boolean actual = streamInfo.getActive();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCrossPodTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    Boolean actual = streamInfo.getCrossPod();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    String actual = streamInfo.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastMessageDateTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    Long actual = streamInfo.getLastMessageDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    String actual = streamInfo.getOrigin();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomAttributesTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    RoomName actual = streamInfo.getRoomAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamAttributesTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    StreamAttributes actual = streamInfo.getStreamAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTypeTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();

    // Act
    StreamType actual = streamInfo.getStreamType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    Boolean active = new Boolean(true);

    // Act
    streamInfo.setActive(active);

    // Assert
    assertEquals(Boolean.valueOf(true), streamInfo.getActive());
  }

  @Test
  public void setCrossPodTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    Boolean crossPod = new Boolean(true);

    // Act
    streamInfo.setCrossPod(crossPod);

    // Assert
    assertEquals(Boolean.valueOf(true), streamInfo.getCrossPod());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    String id = "aaaaa";

    // Act
    streamInfo.setId(id);

    // Assert
    assertEquals("aaaaa", streamInfo.getId());
  }

  @Test
  public void setLastMessageDateTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    Long lastMessageDate = new Long(1L);

    // Act
    streamInfo.setLastMessageDate(lastMessageDate);

    // Assert
    assertEquals(Long.valueOf(1L), streamInfo.getLastMessageDate());
  }

  @Test
  public void setOriginTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    String origin = "aaaaa";

    // Act
    streamInfo.setOrigin(origin);

    // Assert
    assertEquals("aaaaa", streamInfo.getOrigin());
  }

  @Test
  public void setRoomAttributesTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    RoomName roomName = new RoomName();

    // Act
    streamInfo.setRoomAttributes(roomName);

    // Assert
    assertSame(roomName, streamInfo.getRoomAttributes());
  }

  @Test
  public void setStreamAttributesTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    StreamAttributes streamAttributes = new StreamAttributes();

    // Act
    streamInfo.setStreamAttributes(streamAttributes);

    // Assert
    assertSame(streamAttributes, streamInfo.getStreamAttributes());
  }

  @Test
  public void setStreamTypeTest() throws Exception {
    // Arrange
    StreamInfo streamInfo = new StreamInfo();
    StreamType streamType = new StreamType();

    // Act
    streamInfo.setStreamType(streamType);

    // Assert
    assertSame(streamType, streamInfo.getStreamType());
  }
}
