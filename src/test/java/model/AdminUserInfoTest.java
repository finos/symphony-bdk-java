package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.AdminUserInfo;
import model.Avatar;
import org.junit.Test;

public class AdminUserInfoTest {
  @Test
  public void AdminUserInfoTest() throws Exception {
    // Arrange and Act
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Assert
    assertEquals(null, adminUserInfo.getFeatures());
  }

  @Test
  public void getAppsTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    List<Long> actual = adminUserInfo.getApps();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAvatarTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    Avatar actual = adminUserInfo.getAvatar();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDisclaimersTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    List<Long> actual = adminUserInfo.getDisclaimers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFeaturesTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    List<Long> actual = adminUserInfo.getFeatures();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getGroupsTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    List<Long> actual = adminUserInfo.getGroups();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRolesTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    List<String> actual = adminUserInfo.getRoles();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserAttributesTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    AdminUserAttributes actual = adminUserInfo.getUserAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserSystemInfoTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();

    // Act
    AdminUserSystemInfo actual = adminUserInfo.getUserSystemInfo();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAppsTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    adminUserInfo.setApps(arrayList);

    // Assert
    assertSame(arrayList, adminUserInfo.getApps());
  }

  @Test
  public void setAvatarTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    Avatar avatar = new Avatar();

    // Act
    adminUserInfo.setAvatar(avatar);

    // Assert
    assertSame(avatar, adminUserInfo.getAvatar());
  }

  @Test
  public void setDisclaimersTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    adminUserInfo.setDisclaimers(arrayList);

    // Assert
    assertSame(arrayList, adminUserInfo.getDisclaimers());
  }

  @Test
  public void setFeaturesTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    adminUserInfo.setFeatures(arrayList);

    // Assert
    assertSame(arrayList, adminUserInfo.getFeatures());
  }

  @Test
  public void setGroupsTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act
    adminUserInfo.setGroups(arrayList);

    // Assert
    assertSame(arrayList, adminUserInfo.getGroups());
  }

  @Test
  public void setRolesTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    adminUserInfo.setRoles(arrayList);

    // Assert
    assertSame(arrayList, adminUserInfo.getRoles());
  }

  @Test
  public void setUserAttributesTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    adminUserInfo.setUserAttributes(adminUserAttributes);

    // Assert
    assertSame(adminUserAttributes, adminUserInfo.getUserAttributes());
  }

  @Test
  public void setUserSystemInfoTest() throws Exception {
    // Arrange
    AdminUserInfo adminUserInfo = new AdminUserInfo();
    AdminUserSystemInfo adminUserSystemInfo = new AdminUserSystemInfo();

    // Act
    adminUserInfo.setUserSystemInfo(adminUserSystemInfo);

    // Assert
    assertSame(adminUserSystemInfo, adminUserInfo.getUserSystemInfo());
  }
}
