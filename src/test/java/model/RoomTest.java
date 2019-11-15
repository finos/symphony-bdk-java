package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.Room;
import org.junit.Test;

public class RoomTest {
  @Test
  public void RoomTest() throws Exception {
    // Arrange and Act
    Room room = new Room();

    // Assert
    assertEquals(null, room.getMembersCanInvite());
  }

  @Test
  public void getCopyProtectedTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getCopyProtected();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCrossPodTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getCrossPod();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDescriptionTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    String actual = room.getDescription();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDiscoverableTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getDiscoverable();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeywordsTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    List<Keyword> actual = room.getKeywords();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMembersCanInviteTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getMembersCanInvite();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMultiLateralRoomTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getMultiLateralRoom();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    String actual = room.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPublicTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getPublic();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getReadOnlyTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getReadOnly();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getViewHistoryTest() throws Exception {
    // Arrange
    Room room = new Room();

    // Act
    Boolean actual = room.getViewHistory();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCopyProtectedTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean copyProtected = new Boolean(true);

    // Act
    room.setCopyProtected(copyProtected);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getCopyProtected());
  }

  @Test
  public void setCrossPodTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean crossPod = new Boolean(true);

    // Act
    room.setCrossPod(crossPod);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getCrossPod());
  }

  @Test
  public void setDescriptionTest() throws Exception {
    // Arrange
    Room room = new Room();
    String description = "aaaaa";

    // Act
    room.setDescription(description);

    // Assert
    assertEquals("aaaaa", room.getDescription());
  }

  @Test
  public void setDiscoverableTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean discoverable = new Boolean(true);

    // Act
    room.setDiscoverable(discoverable);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getDiscoverable());
  }

  @Test
  public void setKeywordsTest() throws Exception {
    // Arrange
    Room room = new Room();
    ArrayList<Keyword> arrayList = new ArrayList<Keyword>();
    arrayList.add(new Keyword());

    // Act
    room.setKeywords(arrayList);

    // Assert
    assertSame(arrayList, room.getKeywords());
  }

  @Test
  public void setMembersCanInviteTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean membersCanInvite = new Boolean(true);

    // Act
    room.setMembersCanInvite(membersCanInvite);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getMembersCanInvite());
  }

  @Test
  public void setMultiLateralRoomTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean multiLateralRoom = new Boolean(true);

    // Act
    room.setMultiLateralRoom(multiLateralRoom);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getMultiLateralRoom());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    Room room = new Room();
    String name = "aaaaa";

    // Act
    room.setName(name);

    // Assert
    assertEquals("aaaaa", room.getName());
  }

  @Test
  public void setPublicTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean isPublic = new Boolean(true);

    // Act
    room.setPublic(isPublic);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getPublic());
  }

  @Test
  public void setReadOnlyTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean readOnly = new Boolean(true);

    // Act
    room.setReadOnly(readOnly);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getReadOnly());
  }

  @Test
  public void setViewHistoryTest() throws Exception {
    // Arrange
    Room room = new Room();
    Boolean viewHistory = new Boolean(true);

    // Act
    room.setViewHistory(viewHistory);

    // Assert
    assertEquals(Boolean.valueOf(true), room.getViewHistory());
  }
}
