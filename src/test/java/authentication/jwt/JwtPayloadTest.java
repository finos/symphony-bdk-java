package authentication.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import authentication.jwt.JwtPayload;
import org.junit.Test;

public class JwtPayloadTest {
  @Test
  public void JwtPayloadTest() throws Exception {
    // Arrange and Act
    JwtPayload jwtPayload = new JwtPayload();

    // Assert
    assertEquals(null, jwtPayload.getUser());
  }

  @Test
  public void getApplicationIdTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();

    // Act
    String actual = jwtPayload.getApplicationId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCompanyNameTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();

    // Act
    String actual = jwtPayload.getCompanyName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getExpirationDateInSecondsTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();

    // Act
    Long actual = jwtPayload.getExpirationDateInSeconds();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserIdTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();

    // Act
    String actual = jwtPayload.getUserId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();

    // Act
    JwtUser actual = jwtPayload.getUser();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setApplicationIdTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();
    String applicationId = "aaaaa";

    // Act
    jwtPayload.setApplicationId(applicationId);

    // Assert
    assertEquals("aaaaa", jwtPayload.getApplicationId());
  }

  @Test
  public void setCompanyNameTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();
    String companyName = "aaaaa";

    // Act
    jwtPayload.setCompanyName(companyName);

    // Assert
    assertEquals("aaaaa", jwtPayload.getCompanyName());
  }

  @Test
  public void setExpirationDateInSecondsTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();
    Long expirationDateInSeconds = new Long(1L);

    // Act
    jwtPayload.setExpirationDateInSeconds(expirationDateInSeconds);

    // Assert
    assertEquals(Long.valueOf(1L), jwtPayload.getExpirationDateInSeconds());
  }

  @Test
  public void setUserIdTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();
    String userId = "aaaaa";

    // Act
    jwtPayload.setUserId(userId);

    // Assert
    assertEquals("aaaaa", jwtPayload.getUserId());
  }

  @Test
  public void setUserTest() throws Exception {
    // Arrange
    JwtPayload jwtPayload = new JwtPayload();
    JwtUser jwtUser = new JwtUser();

    // Act
    jwtPayload.setUser(jwtUser);

    // Assert
    assertSame(jwtUser, jwtPayload.getUser());
  }
}
