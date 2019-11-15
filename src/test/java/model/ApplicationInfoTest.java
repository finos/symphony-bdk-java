package model;

import static org.junit.Assert.assertEquals;
import model.ApplicationInfo;
import org.junit.Test;

public class ApplicationInfoTest {
  @Test
  public void ApplicationInfoTest() throws Exception {
    // Arrange and Act
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Assert
    assertEquals(null, applicationInfo.getDomain());
  }

  @Test
  public void getAppIdTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Act
    String actual = applicationInfo.getAppId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppUrlTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Act
    String actual = applicationInfo.getAppUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDomainTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Act
    String actual = applicationInfo.getDomain();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Act
    String actual = applicationInfo.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPublisherTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();

    // Act
    String actual = applicationInfo.getPublisher();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAppIdTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();
    String appId = "aaaaa";

    // Act
    applicationInfo.setAppId(appId);

    // Assert
    assertEquals("aaaaa", applicationInfo.getAppId());
  }

  @Test
  public void setAppUrlTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();
    String appUrl = "aaaaa";

    // Act
    applicationInfo.setAppUrl(appUrl);

    // Assert
    assertEquals("aaaaa", applicationInfo.getAppUrl());
  }

  @Test
  public void setDomainTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();
    String domain = "aaaaa";

    // Act
    applicationInfo.setDomain(domain);

    // Assert
    assertEquals("aaaaa", applicationInfo.getDomain());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();
    String name = "aaaaa";

    // Act
    applicationInfo.setName(name);

    // Assert
    assertEquals("aaaaa", applicationInfo.getName());
  }

  @Test
  public void setPublisherTest() throws Exception {
    // Arrange
    ApplicationInfo applicationInfo = new ApplicationInfo();
    String publisher = "aaaaa";

    // Act
    applicationInfo.setPublisher(publisher);

    // Assert
    assertEquals("aaaaa", applicationInfo.getPublisher());
  }
}
