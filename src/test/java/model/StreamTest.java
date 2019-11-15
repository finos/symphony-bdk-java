package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.Stream;
import org.junit.Test;

public class StreamTest {
  @Test
  public void StreamTest() throws Exception {
    // Arrange and Act
    Stream stream = new Stream();

    // Assert
    assertEquals(null, stream.getStreamType());
  }

  @Test
  public void getCrossPodTest() throws Exception {
    // Arrange
    Stream stream = new Stream();

    // Act
    Boolean actual = stream.getCrossPod();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getExternalTest() throws Exception {
    // Arrange
    Stream stream = new Stream();

    // Act
    Boolean actual = stream.getExternal();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMembersTest() throws Exception {
    // Arrange
    Stream stream = new Stream();

    // Act
    List<User> actual = stream.getMembers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomNameTest() throws Exception {
    // Arrange
    Stream stream = new Stream();

    // Act
    String actual = stream.getRoomName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamIdTest() throws Exception {
    // Arrange
    Stream stream = new Stream();

    // Act
    String actual = stream.getStreamId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTypeTest() throws Exception {
    // Arrange
    Stream stream = new Stream();

    // Act
    String actual = stream.getStreamType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCrossPodTest() throws Exception {
    // Arrange
    Stream stream = new Stream();
    Boolean crossPod = new Boolean(true);

    // Act
    stream.setCrossPod(crossPod);

    // Assert
    assertEquals(Boolean.valueOf(true), stream.getCrossPod());
  }

  @Test
  public void setExternalTest() throws Exception {
    // Arrange
    Stream stream = new Stream();
    Boolean external = new Boolean(true);

    // Act
    stream.setExternal(external);

    // Assert
    assertEquals(Boolean.valueOf(true), stream.getExternal());
  }

  @Test
  public void setMembersTest() throws Exception {
    // Arrange
    Stream stream = new Stream();
    ArrayList<User> arrayList = new ArrayList<User>();
    arrayList.add(new User());

    // Act
    stream.setMembers(arrayList);

    // Assert
    assertSame(arrayList, stream.getMembers());
  }

  @Test
  public void setRoomNameTest() throws Exception {
    // Arrange
    Stream stream = new Stream();
    String roomName = "aaaaa";

    // Act
    stream.setRoomName(roomName);

    // Assert
    assertEquals("aaaaa", stream.getRoomName());
  }

  @Test
  public void setStreamIdTest() throws Exception {
    // Arrange
    Stream stream = new Stream();
    String streamId = "aaaaa";

    // Act
    stream.setStreamId(streamId);

    // Assert
    assertEquals("aaaaa", stream.getStreamId());
  }

  @Test
  public void setStreamTypeTest() throws Exception {
    // Arrange
    Stream stream = new Stream();
    String streamType = "aaaaa";

    // Act
    stream.setStreamType(streamType);

    // Assert
    assertEquals("aaaaa", stream.getStreamType());
  }
}
