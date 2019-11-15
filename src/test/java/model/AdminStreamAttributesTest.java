package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.AdminStreamAttributes;
import org.junit.Test;

public class AdminStreamAttributesTest {
  @Test
  public void AdminStreamAttributesTest() throws Exception {
    // Arrange and Act
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Assert
    assertEquals(null, adminStreamAttributes.getRoomName());
  }

  @Test
  public void getCreatedByUserIdTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    long actual = adminStreamAttributes.getCreatedByUserId();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getCreatedDateTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    long actual = adminStreamAttributes.getCreatedDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getDescriptionTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    String actual = adminStreamAttributes.getDescription();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastMessageDateTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    long actual = adminStreamAttributes.getLastMessageDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getLastModifiedDateTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    long actual = adminStreamAttributes.getLastModifiedDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getMembersCountTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    Integer actual = adminStreamAttributes.getMembersCount();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMembersTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    List<Long> actual = adminStreamAttributes.getMembers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginCompanyIdTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    Integer actual = adminStreamAttributes.getOriginCompanyId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginCompanyTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    String actual = adminStreamAttributes.getOriginCompany();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomDescriptionTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    String actual = adminStreamAttributes.getRoomDescription();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomNameTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    String actual = adminStreamAttributes.getRoomName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCreatedByUserIdTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    long createdByUserId = 1L;

    // Act
    adminStreamAttributes.setCreatedByUserId(createdByUserId);

    // Assert
    assertEquals(1L, adminStreamAttributes.getCreatedByUserId());
  }

  @Test
  public void setCreatedDateTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    long createdDate = 1L;

    // Act
    adminStreamAttributes.setCreatedDate(createdDate);

    // Assert
    assertEquals(1L, adminStreamAttributes.getCreatedDate());
  }

  @Test
  public void setDescriptionTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    String description = "aaaaa";

    // Act
    adminStreamAttributes.setDescription(description);

    // Assert
    assertEquals("aaaaa", adminStreamAttributes.getDescription());
  }

  @Test
  public void setLastMessageDateTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    long lastMessageDate = 1L;

    // Act
    adminStreamAttributes.setLastMessageDate(lastMessageDate);

    // Assert
    assertEquals(1L, adminStreamAttributes.getLastMessageDate());
  }

  @Test
  public void setLastModifiedDateTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    long lastModifiedDate = 1L;

    // Act
    adminStreamAttributes.setLastModifiedDate(lastModifiedDate);

    // Assert
    assertEquals(1L, adminStreamAttributes.getLastModifiedDate());
  }

  @Test
  public void setMembersCountTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    Integer membersCount = new Integer(1);

    // Act
    adminStreamAttributes.setMembersCount(membersCount);

    // Assert
    assertEquals(Integer.valueOf(1), adminStreamAttributes.getMembersCount());
  }

  @Test
  public void setMembersTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    adminStreamAttributes.setMembers(arrayList);

    // Assert
    assertSame(arrayList, adminStreamAttributes.getMembers());
  }

  @Test
  public void setOriginCompanyIdTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    Integer originCompanyId = new Integer(1);

    // Act
    adminStreamAttributes.setOriginCompanyId(originCompanyId);

    // Assert
    assertEquals(Integer.valueOf(1), adminStreamAttributes.getOriginCompanyId());
  }

  @Test
  public void setOriginCompanyTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    String originCompany = "aaaaa";

    // Act
    adminStreamAttributes.setOriginCompany(originCompany);

    // Assert
    assertEquals("aaaaa", adminStreamAttributes.getOriginCompany());
  }

  @Test
  public void setRoomDescriptionTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    String roomDescription = "aaaaa";

    // Act
    adminStreamAttributes.setRoomDescription(roomDescription);

    // Assert
    assertEquals("aaaaa", adminStreamAttributes.getRoomDescription());
  }

  @Test
  public void setRoomNameTest() throws Exception {
    // Arrange
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();
    String roomName = "aaaaa";

    // Act
    adminStreamAttributes.setRoomName(roomName);

    // Assert
    assertEquals("aaaaa", adminStreamAttributes.getRoomName());
  }
}
