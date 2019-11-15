package model;

import static org.junit.Assert.assertEquals;
import model.Password;
import org.junit.Test;

public class PasswordTest {
  @Test
  public void PasswordTest() throws Exception {
    // Arrange and Act
    Password password = new Password();

    // Assert
    assertEquals(null, password.getKhSalt());
  }

  @Test
  public void getKhPasswordTest() throws Exception {
    // Arrange
    Password password = new Password();

    // Act
    String actual = password.getKhPassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKhSaltTest() throws Exception {
    // Arrange
    Password password = new Password();

    // Act
    String actual = password.getKhSalt();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void gethPasswordTest() throws Exception {
    // Arrange
    Password password = new Password();

    // Act
    String actual = password.gethPassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void gethSaltTest() throws Exception {
    // Arrange
    Password password = new Password();

    // Act
    String actual = password.gethSalt();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setKhPasswordTest() throws Exception {
    // Arrange
    Password password = new Password();
    String khPassword = "aaaaa";

    // Act
    password.setKhPassword(khPassword);

    // Assert
    assertEquals("aaaaa", password.getKhPassword());
  }

  @Test
  public void setKhSaltTest() throws Exception {
    // Arrange
    Password password = new Password();
    String khSalt = "aaaaa";

    // Act
    password.setKhSalt(khSalt);

    // Assert
    assertEquals("aaaaa", password.getKhSalt());
  }

  @Test
  public void sethPasswordTest() throws Exception {
    // Arrange
    Password password = new Password();
    String hhPassword = "aaaaa";

    // Act
    password.sethPassword(hhPassword);

    // Assert
    assertEquals(null, password.getKhSalt());
  }

  @Test
  public void sethSaltTest() throws Exception {
    // Arrange
    Password password = new Password();
    String hhSalt = "aaaaa";

    // Act
    password.sethSalt(hhSalt);

    // Assert
    assertEquals(null, password.getKhSalt());
  }
}
