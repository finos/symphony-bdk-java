package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.AdminNewUser;
import model.Password;
import org.junit.Test;

public class AdminNewUserTest {
  @Test
  public void AdminNewUserTest() throws Exception {
    // Arrange and Act
    AdminNewUser adminNewUser = new AdminNewUser();

    // Assert
    assertEquals(null, adminNewUser.getPassword());
  }

  @Test
  public void getPasswordTest() throws Exception {
    // Arrange
    AdminNewUser adminNewUser = new AdminNewUser();

    // Act
    Password actual = adminNewUser.getPassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRolesTest() throws Exception {
    // Arrange
    AdminNewUser adminNewUser = new AdminNewUser();

    // Act
    List<String> actual = adminNewUser.getRoles();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserAttributesTest() throws Exception {
    // Arrange
    AdminNewUser adminNewUser = new AdminNewUser();

    // Act
    AdminUserAttributes actual = adminNewUser.getUserAttributes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setPasswordTest() throws Exception {
    // Arrange
    AdminNewUser adminNewUser = new AdminNewUser();
    Password password = new Password();

    // Act
    adminNewUser.setPassword(password);

    // Assert
    assertSame(password, adminNewUser.getPassword());
  }

  @Test
  public void setRolesTest() throws Exception {
    // Arrange
    AdminNewUser adminNewUser = new AdminNewUser();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    adminNewUser.setRoles(arrayList);

    // Assert
    assertSame(arrayList, adminNewUser.getRoles());
  }

  @Test
  public void setUserAttributesTest() throws Exception {
    // Arrange
    AdminNewUser adminNewUser = new AdminNewUser();
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    adminNewUser.setUserAttributes(adminUserAttributes);

    // Assert
    assertSame(adminUserAttributes, adminNewUser.getUserAttributes());
  }
}
