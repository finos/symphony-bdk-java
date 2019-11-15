package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import model.RoomSystemInfo;
import org.junit.Test;

public class RoomSystemInfoTest {
  @Test
  public void RoomSystemInfoTest() throws Exception {
    // Arrange and Act
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();

    // Assert
    assertEquals(0L, roomSystemInfo.getCreatedByUserId());
  }

  @Test
  public void getCreatedByUserIdTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();

    // Act
    long actual = roomSystemInfo.getCreatedByUserId();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getCreationDateTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();

    // Act
    long actual = roomSystemInfo.getCreationDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();

    // Act
    String actual = roomSystemInfo.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void isActiveTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();

    // Act
    boolean actual = roomSystemInfo.isActive();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();
    boolean active = true;

    // Act
    roomSystemInfo.setActive(active);

    // Assert
    assertTrue(roomSystemInfo.isActive());
  }

  @Test
  public void setCreatedByUserIdTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();
    long createdByUserId = 1L;

    // Act
    roomSystemInfo.setCreatedByUserId(createdByUserId);

    // Assert
    assertEquals(1L, roomSystemInfo.getCreatedByUserId());
  }

  @Test
  public void setCreationDateTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();
    long creationDate = 1L;

    // Act
    roomSystemInfo.setCreationDate(creationDate);

    // Assert
    assertEquals(1L, roomSystemInfo.getCreationDate());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    RoomSystemInfo roomSystemInfo = new RoomSystemInfo();
    String id = "aaaaa";

    // Act
    roomSystemInfo.setId(id);

    // Assert
    assertEquals("aaaaa", roomSystemInfo.getId());
  }
}
