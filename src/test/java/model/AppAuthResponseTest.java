package model;

import static org.junit.Assert.assertEquals;
import model.AppAuthResponse;
import org.junit.Test;

public class AppAuthResponseTest {
  @Test
  public void AppAuthResponseTest() throws Exception {
    // Arrange and Act
    AppAuthResponse appAuthResponse = new AppAuthResponse();

    // Assert
    assertEquals(null, appAuthResponse.getAppToken());
  }

  @Test
  public void getAppIdTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();

    // Act
    String actual = appAuthResponse.getAppId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppTokenTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();

    // Act
    String actual = appAuthResponse.getAppToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getExpireAtTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();

    // Act
    long actual = appAuthResponse.getExpireAt();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void getSymphonyTokenTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();

    // Act
    String actual = appAuthResponse.getSymphonyToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAppIdTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();
    String appId = "aaaaa";

    // Act
    appAuthResponse.setAppId(appId);

    // Assert
    assertEquals("aaaaa", appAuthResponse.getAppId());
  }

  @Test
  public void setAppTokenTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();
    String appToken = "aaaaa";

    // Act
    appAuthResponse.setAppToken(appToken);

    // Assert
    assertEquals("aaaaa", appAuthResponse.getAppToken());
  }

  @Test
  public void setExpireAtTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();
    long expireAt = 1L;

    // Act
    appAuthResponse.setExpireAt(expireAt);

    // Assert
    assertEquals(1L, appAuthResponse.getExpireAt());
  }

  @Test
  public void setSymphonyTokenTest() throws Exception {
    // Arrange
    AppAuthResponse appAuthResponse = new AppAuthResponse();
    String symphonyToken = "aaaaa";

    // Act
    appAuthResponse.setSymphonyToken(symphonyToken);

    // Assert
    assertEquals("aaaaa", appAuthResponse.getSymphonyToken());
  }
}
