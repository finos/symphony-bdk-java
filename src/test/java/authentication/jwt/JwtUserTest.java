package authentication.jwt;

import static org.junit.Assert.assertEquals;
import authentication.jwt.JwtUser;
import org.junit.Test;

public class JwtUserTest {
  @Test
  public void JwtUserTest() throws Exception {
    // Arrange and Act
    JwtUser jwtUser = new JwtUser();

    // Assert
    assertEquals(null, jwtUser.getLastName());
  }

  @Test
  public void getAvatarSmallUrlTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getAvatarSmallUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAvatarUrlTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getAvatarUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCompanyIdTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getCompanyId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCompanyTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getCompany();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDisplayNameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getDisplayName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEmailAddressTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getEmailAddress();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirstNameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getFirstName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastNameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getLastName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLocationTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getLocation();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTitleTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getTitle();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUsernameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();

    // Act
    String actual = jwtUser.getUsername();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAvatarSmallUrlTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String avatarSmallUrl = "aaaaa";

    // Act
    jwtUser.setAvatarSmallUrl(avatarSmallUrl);

    // Assert
    assertEquals("aaaaa", jwtUser.getAvatarSmallUrl());
  }

  @Test
  public void setAvatarUrlTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String avatarUrl = "aaaaa";

    // Act
    jwtUser.setAvatarUrl(avatarUrl);

    // Assert
    assertEquals("aaaaa", jwtUser.getAvatarUrl());
  }

  @Test
  public void setCompanyIdTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String companyId = "aaaaa";

    // Act
    jwtUser.setCompanyId(companyId);

    // Assert
    assertEquals("aaaaa", jwtUser.getCompanyId());
  }

  @Test
  public void setCompanyTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String company = "aaaaa";

    // Act
    jwtUser.setCompany(company);

    // Assert
    assertEquals("aaaaa", jwtUser.getCompany());
  }

  @Test
  public void setDisplayNameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String displayName = "aaaaa";

    // Act
    jwtUser.setDisplayName(displayName);

    // Assert
    assertEquals("aaaaa", jwtUser.getDisplayName());
  }

  @Test
  public void setEmailAddressTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String emailAddress = "aaaaa";

    // Act
    jwtUser.setEmailAddress(emailAddress);

    // Assert
    assertEquals("aaaaa", jwtUser.getEmailAddress());
  }

  @Test
  public void setFirstNameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String firstName = "aaaaa";

    // Act
    jwtUser.setFirstName(firstName);

    // Assert
    assertEquals("aaaaa", jwtUser.getFirstName());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String id = "aaaaa";

    // Act
    jwtUser.setId(id);

    // Assert
    assertEquals("aaaaa", jwtUser.getId());
  }

  @Test
  public void setLastNameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String lastName = "aaaaa";

    // Act
    jwtUser.setLastName(lastName);

    // Assert
    assertEquals("aaaaa", jwtUser.getLastName());
  }

  @Test
  public void setLocationTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String location = "aaaaa";

    // Act
    jwtUser.setLocation(location);

    // Assert
    assertEquals("aaaaa", jwtUser.getLocation());
  }

  @Test
  public void setTitleTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String title = "aaaaa";

    // Act
    jwtUser.setTitle(title);

    // Assert
    assertEquals("aaaaa", jwtUser.getTitle());
  }

  @Test
  public void setUsernameTest() throws Exception {
    // Arrange
    JwtUser jwtUser = new JwtUser();
    String username = "aaaaa";

    // Act
    jwtUser.setUsername(username);

    // Assert
    assertEquals("aaaaa", jwtUser.getUsername());
  }
}
