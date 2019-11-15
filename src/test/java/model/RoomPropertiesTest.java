package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.RoomProperties;
import org.junit.Test;

public class RoomPropertiesTest {
  @Test
  public void RoomPropertiesTest() throws Exception {
    // Arrange and Act
    RoomProperties roomProperties = new RoomProperties();

    // Assert
    assertEquals(null, roomProperties.getName());
  }

  @Test
  public void getCanViewHistoryTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getCanViewHistory();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCopyProtectedTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getCopyProtected();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCreatedDateTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Long actual = roomProperties.getCreatedDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCreatorUserTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    User actual = roomProperties.getCreatorUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCrossPodTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getCrossPod();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDescriptionTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    String actual = roomProperties.getDescription();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDiscoverableTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getDiscoverable();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getExternalTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getExternal();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeywordsTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    List<Keyword> actual = roomProperties.getKeywords();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMembersCanInviteTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getMembersCanInvite();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    String actual = roomProperties.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPublicTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getPublic();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getReadOnlyTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();

    // Act
    Boolean actual = roomProperties.getReadOnly();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCanViewHistoryTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean canViewHistory = new Boolean(true);

    // Act
    roomProperties.setCanViewHistory(canViewHistory);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getCanViewHistory());
  }

  @Test
  public void setCopyProtectedTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean copyProtected = new Boolean(true);

    // Act
    roomProperties.setCopyProtected(copyProtected);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getCopyProtected());
  }

  @Test
  public void setCreatedDateTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Long createdDate = new Long(1L);

    // Act
    roomProperties.setCreatedDate(createdDate);

    // Assert
    assertEquals(Long.valueOf(1L), roomProperties.getCreatedDate());
  }

  @Test
  public void setCreatorUserTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    User user = new User();

    // Act
    roomProperties.setCreatorUser(user);

    // Assert
    assertSame(user, roomProperties.getCreatorUser());
  }

  @Test
  public void setCrossPodTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean crossPod = new Boolean(true);

    // Act
    roomProperties.setCrossPod(crossPod);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getCrossPod());
  }

  @Test
  public void setDescriptionTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    String description = "aaaaa";

    // Act
    roomProperties.setDescription(description);

    // Assert
    assertEquals("aaaaa", roomProperties.getDescription());
  }

  @Test
  public void setDiscoverableTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean discoverable = new Boolean(true);

    // Act
    roomProperties.setDiscoverable(discoverable);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getDiscoverable());
  }

  @Test
  public void setExternalTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean external = new Boolean(true);

    // Act
    roomProperties.setExternal(external);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getExternal());
  }

  @Test
  public void setKeywordsTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    ArrayList<Keyword> arrayList = new ArrayList<Keyword>();
    arrayList.add(new Keyword());

    // Act
    roomProperties.setKeywords(arrayList);

    // Assert
    assertSame(arrayList, roomProperties.getKeywords());
  }

  @Test
  public void setMembersCanInviteTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean membersCanInvite = new Boolean(true);

    // Act
    roomProperties.setMembersCanInvite(membersCanInvite);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getMembersCanInvite());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    String name = "aaaaa";

    // Act
    roomProperties.setName(name);

    // Assert
    assertEquals("aaaaa", roomProperties.getName());
  }

  @Test
  public void setPublicTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean isPublic = new Boolean(true);

    // Act
    roomProperties.setPublic(isPublic);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getPublic());
  }

  @Test
  public void setReadOnlyTest() throws Exception {
    // Arrange
    RoomProperties roomProperties = new RoomProperties();
    Boolean readOnly = new Boolean(true);

    // Act
    roomProperties.setReadOnly(readOnly);

    // Assert
    assertEquals(Boolean.valueOf(true), roomProperties.getReadOnly());
  }
}
