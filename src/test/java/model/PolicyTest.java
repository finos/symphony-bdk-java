package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import model.Policy;
import org.junit.Test;

public class PolicyTest {
  @Test
  public void PolicyTest() throws Exception {
    // Arrange and Act
    Policy policy = new Policy();

    // Assert
    assertEquals(0L, policy.getModifiedDate());
  }

  @Test
  public void getCreatedDateTest() throws Exception {
    // Arrange
    Policy policy = new Policy();

    // Act
    long actual = policy.getCreatedDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getGroupsTest() throws Exception {
    // Arrange
    Policy policy = new Policy();

    // Act
    List<String> actual = policy.getGroups();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    Policy policy = new Policy();

    // Act
    String actual = policy.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getModifiedDateTest() throws Exception {
    // Arrange
    Policy policy = new Policy();

    // Act
    long actual = policy.getModifiedDate();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getPolicyTypeTest() throws Exception {
    // Arrange
    Policy policy = new Policy();

    // Act
    String actual = policy.getPolicyType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void isActiveTest() throws Exception {
    // Arrange
    Policy policy = new Policy();

    // Act
    boolean actual = policy.isActive();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    Policy policy = new Policy();
    boolean active = true;

    // Act
    policy.setActive(active);

    // Assert
    assertTrue(policy.isActive());
  }

  @Test
  public void setCreatedDateTest() throws Exception {
    // Arrange
    Policy policy = new Policy();
    long createdDate = 1L;

    // Act
    policy.setCreatedDate(createdDate);

    // Assert
    assertEquals(1L, policy.getCreatedDate());
  }

  @Test
  public void setGroupsTest() throws Exception {
    // Arrange
    Policy policy = new Policy();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    policy.setGroups(arrayList);

    // Assert
    assertSame(arrayList, policy.getGroups());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    Policy policy = new Policy();
    String id = "aaaaa";

    // Act
    policy.setId(id);

    // Assert
    assertEquals("aaaaa", policy.getId());
  }

  @Test
  public void setModifiedDateTest() throws Exception {
    // Arrange
    Policy policy = new Policy();
    long modifiedDate = 1L;

    // Act
    policy.setModifiedDate(modifiedDate);

    // Assert
    assertEquals(1L, policy.getModifiedDate());
  }

  @Test
  public void setPolicyTypeTest() throws Exception {
    // Arrange
    Policy policy = new Policy();
    String policyType = "aaaaa";

    // Act
    policy.setPolicyType(policyType);

    // Assert
    assertEquals("aaaaa", policy.getPolicyType());
  }
}
