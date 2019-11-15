package model;

import static org.junit.Assert.assertEquals;
import model.AdminUserSystemInfo;
import org.junit.Test;

public class AdminUserSystemInfoTest {
  @Test
  public void AdminUserSystemInfoTest() throws Exception {
    // Arrange and Act
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Assert
    assertEquals(null, adminUserSystemInfo.getCreatedBy());
  }

  @Test
  public void getCreatedByTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    String actual = adminUserSystemInfo.getCreatedBy();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCreatedDateTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    Long actual = adminUserSystemInfo.getCreatedDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    Long actual = adminUserSystemInfo.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastLoginDateTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    Long actual = adminUserSystemInfo.getLastLoginDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastUpdatedDateTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    Long actual = adminUserSystemInfo.getLastUpdatedDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStatusTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    String actual = adminUserSystemInfo.getStatus();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCreatedByTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();
    String createdBy = "aaaaa";

    // Act
    adminUserSystemInfo.setCreatedBy(createdBy);

    // Assert
    assertEquals("aaaaa", adminUserSystemInfo.getCreatedBy());
  }

  @Test
  public void setCreatedDateTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();
    Long createdDate = new Long(1L);

    // Act
    adminUserSystemInfo.setCreatedDate(createdDate);

    // Assert
    assertEquals(Long.valueOf(1L), adminUserSystemInfo.getCreatedDate());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();
    Long id = new Long(1L);

    // Act
    adminUserSystemInfo.setId(id);

    // Assert
    assertEquals(Long.valueOf(1L), adminUserSystemInfo.getId());
  }

  @Test
  public void setLastLoginDateTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();
    Long lastLoginDate = new Long(1L);

    // Act
    adminUserSystemInfo.setLastLoginDate(lastLoginDate);

    // Assert
    assertEquals(Long.valueOf(1L), adminUserSystemInfo.getLastLoginDate());
  }

  @Test
  public void setLastUpdatedDateTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();
    Long lastUpdatedDate = new Long(1L);

    // Act
    adminUserSystemInfo.setLastUpdatedDate(lastUpdatedDate);

    // Assert
    assertEquals(Long.valueOf(1L), adminUserSystemInfo.getLastUpdatedDate());
  }

  @Test
  public void setStatusTest() throws Exception {
    // Arrange
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();
    String status = "aaaaa";

    // Act
    adminUserSystemInfo.setStatus(status);

    // Assert
    assertEquals("aaaaa", adminUserSystemInfo.getStatus());
  }
}
