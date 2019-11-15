package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.Application;
import org.junit.Test;

public class ApplicationTest {
  @Test
  public void ApplicationTest() throws Exception {
    // Arrange and Act
    Application application = new Application();

    // Assert
    assertEquals(null, application.getApplicationInfo());
  }

  @Test
  public void getAllowOriginsTest() throws Exception {
    // Arrange
    Application application = new Application();

    // Act
    String actual = application.getAllowOrigins();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getApplicationInfoTest() throws Exception {
    // Arrange
    Application application = new Application();

    // Act
    ApplicationInfo actual = application.getApplicationInfo();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCertTest() throws Exception {
    // Arrange
    Application application = new Application();

    // Act
    String actual = application.getCert();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDescriptionTest() throws Exception {
    // Arrange
    Application application = new Application();

    // Act
    String actual = application.getDescription();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIconUrlTest() throws Exception {
    // Arrange
    Application application = new Application();

    // Act
    String actual = application.getIconUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPermissionsTest() throws Exception {
    // Arrange
    Application application = new Application();

    // Act
    List<String> actual = application.getPermissions();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAllowOriginsTest() throws Exception {
    // Arrange
    Application application = new Application();
    String allowOrigins = "aaaaa";

    // Act
    application.setAllowOrigins(allowOrigins);

    // Assert
    assertEquals("aaaaa", application.getAllowOrigins());
  }

  @Test
  public void setApplicationInfoTest() throws Exception {
    // Arrange
    Application application = new Application();
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Act
    application.setApplicationInfo(applicationInfo);

    // Assert
    assertSame(applicationInfo, application.getApplicationInfo());
  }

  @Test
  public void setCertTest() throws Exception {
    // Arrange
    Application application = new Application();
    String cert = "aaaaa";

    // Act
    application.setCert(cert);

    // Assert
    assertEquals("aaaaa", application.getCert());
  }

  @Test
  public void setDescriptionTest() throws Exception {
    // Arrange
    Application application = new Application();
    String description = "aaaaa";

    // Act
    application.setDescription(description);

    // Assert
    assertEquals("aaaaa", application.getDescription());
  }

  @Test
  public void setIconUrlTest() throws Exception {
    // Arrange
    Application application = new Application();
    String iconUrl = "aaaaa";

    // Act
    application.setIconUrl(iconUrl);

    // Assert
    assertEquals("aaaaa", application.getIconUrl());
  }

  @Test
  public void setPermissionsTest() throws Exception {
    // Arrange
    Application application = new Application();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    application.setPermissions(arrayList);

    // Assert
    assertSame(arrayList, application.getPermissions());
  }
}
