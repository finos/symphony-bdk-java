package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import model.AdminStreamAttributes;
import model.AdminStreamInfo;
import org.junit.Test;

public class AdminStreamInfoTest {
  @Test
  public void AdminStreamInfoTest() throws Exception {
    // Arrange and Act
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Assert
    assertEquals(null, adminStreamInfo.getOrigin());
  }

  @Test
  public void getAttributesTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    AdminStreamAttributes actual = adminStreamInfo.getAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    String actual = adminStreamInfo.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    String actual = adminStreamInfo.getOrigin();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTypeTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    String actual = adminStreamInfo.getType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void isActiveTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    boolean actual = adminStreamInfo.isActive();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void isCrossPodTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    boolean actual = adminStreamInfo.isCrossPod();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void isExternalTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    boolean actual = adminStreamInfo.isExternal();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void isPublicTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();

    // Act
    boolean actual = adminStreamInfo.isPublic();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    boolean active = true;

    // Act
    adminStreamInfo.setActive(active);

    // Assert
    assertTrue(adminStreamInfo.isActive());
  }

  @Test
  public void setAttributesTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    AdminStreamAttributes adminStreamAttributes = new AdminStreamAttributes();

    // Act
    adminStreamInfo.setAttributes(adminStreamAttributes);

    // Assert
    assertSame(adminStreamAttributes, adminStreamInfo.getAttributes());
  }

  @Test
  public void setCrossPodTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    boolean crossPod = true;

    // Act
    adminStreamInfo.setCrossPod(crossPod);

    // Assert
    assertTrue(adminStreamInfo.isCrossPod());
  }

  @Test
  public void setExternalTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    boolean external = true;

    // Act
    adminStreamInfo.setExternal(external);

    // Assert
    assertTrue(adminStreamInfo.isExternal());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    String id = "aaaaa";

    // Act
    adminStreamInfo.setId(id);

    // Assert
    assertEquals("aaaaa", adminStreamInfo.getId());
  }

  @Test
  public void setOriginTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    String origin = "aaaaa";

    // Act
    adminStreamInfo.setOrigin(origin);

    // Assert
    assertEquals("aaaaa", adminStreamInfo.getOrigin());
  }

  @Test
  public void setPublicTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    boolean isPublic = true;

    // Act
    adminStreamInfo.setPublic(isPublic);

    // Assert
    assertTrue(adminStreamInfo.isPublic());
  }

  @Test
  public void setTypeTest() throws Exception {
    // Arrange
    AdminStreamInfo adminStreamInfo = new AdminStreamInfo();
    String type = "aaaaa";

    // Act
    adminStreamInfo.setType(type);

    // Assert
    assertEquals("aaaaa", adminStreamInfo.getType());
  }
}
