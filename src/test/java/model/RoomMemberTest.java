package model;

import static org.junit.Assert.assertEquals;
import model.RoomMember;
import org.junit.Test;

public class RoomMemberTest {
  @Test
  public void RoomMemberTest() throws Exception {
    // Arrange and Act
    RoomMember roomMember = new RoomMember();

    // Assert
    assertEquals(null, roomMember.getJoinDate());
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    RoomMember roomMember = new RoomMember();

    // Act
    Long actual = roomMember.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getJoinDateTest() throws Exception {
    // Arrange
    RoomMember roomMember = new RoomMember();

    // Act
    Long actual = roomMember.getJoinDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOwnerTest() throws Exception {
    // Arrange
    RoomMember roomMember = new RoomMember();

    // Act
    Boolean actual = roomMember.getOwner();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    RoomMember roomMember = new RoomMember();
    Long id = new Long(1L);

    // Act
    roomMember.setId(id);

    // Assert
    assertEquals(Long.valueOf(1L), roomMember.getId());
  }

  @Test
  public void setJoinDateTest() throws Exception {
    // Arrange
    RoomMember roomMember = new RoomMember();
    Long joinDate = new Long(1L);

    // Act
    roomMember.setJoinDate(joinDate);

    // Assert
    assertEquals(Long.valueOf(1L), roomMember.getJoinDate());
  }

  @Test
  public void setOwnerTest() throws Exception {
    // Arrange
    RoomMember roomMember = new RoomMember();
    Boolean owner = new Boolean(true);

    // Act
    roomMember.setOwner(owner);

    // Assert
    assertEquals(Boolean.valueOf(true), roomMember.getOwner());
  }
}
