package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import model.InformationBarrierGroup;
import org.junit.Test;

public class InformationBarrierGroupTest {
  @Test
  public void InformationBarrierGroupTest() throws Exception {
    // Arrange and Act
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Assert
    assertEquals(null, informationBarrierGroup.getName());
  }

  @Test
  public void getCreatedDateTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    long actual = informationBarrierGroup.getCreatedDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    String actual = informationBarrierGroup.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMemberCountTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    int actual = informationBarrierGroup.getMemberCount();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getModifiedDateTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    long actual = informationBarrierGroup.getModifiedDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    String actual = informationBarrierGroup.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPoliciesTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    List<String> actual = informationBarrierGroup.getPolicies();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void isActiveTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();

    // Act
    boolean actual = informationBarrierGroup.isActive();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    boolean active = true;

    // Act
    informationBarrierGroup.setActive(active);

    // Assert
    assertTrue(informationBarrierGroup.isActive());
  }

  @Test
  public void setCreatedDateTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    long createdDate = 1L;

    // Act
    informationBarrierGroup.setCreatedDate(createdDate);

    // Assert
    assertEquals(1L, informationBarrierGroup.getCreatedDate());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    String id = "aaaaa";

    // Act
    informationBarrierGroup.setId(id);

    // Assert
    assertEquals("aaaaa", informationBarrierGroup.getId());
  }

  @Test
  public void setMemberCountTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    int memberCount = 1;

    // Act
    informationBarrierGroup.setMemberCount(memberCount);

    // Assert
    assertEquals(1, informationBarrierGroup.getMemberCount());
  }

  @Test
  public void setModifiedDateTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    long modifiedDate = 1L;

    // Act
    informationBarrierGroup.setModifiedDate(modifiedDate);

    // Assert
    assertEquals(1L, informationBarrierGroup.getModifiedDate());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    String name = "aaaaa";

    // Act
    informationBarrierGroup.setName(name);

    // Assert
    assertEquals("aaaaa", informationBarrierGroup.getName());
  }

  @Test
  public void setPoliciesTest() throws Exception {
    // Arrange
    InformationBarrierGroup informationBarrierGroup = new InformationBarrierGroup();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    informationBarrierGroup.setPolicies(arrayList);

    // Assert
    assertSame(arrayList, informationBarrierGroup.getPolicies());
  }
}
