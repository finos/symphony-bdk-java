package model;

import static org.junit.Assert.assertEquals;
import model.Token;
import org.junit.Test;

public class TokenTest {
  @Test
  public void TokenTest() throws Exception {
    // Arrange and Act
    Token token = new Token();

    // Assert
    String name = token.getName();
    assertEquals(null, name);
    assertEquals(null, token.getToken());
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    Token token = new Token();

    // Act
    String actual = token.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTokenTest() throws Exception {
    // Arrange
    Token token = new Token();

    // Act
    String actual = token.getToken();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    Token token = new Token();
    String name = "aaaaa";

    // Act
    token.setName(name);

    // Assert
    assertEquals("aaaaa", token.getName());
  }

  @Test
  public void setTokenTest() throws Exception {
    // Arrange
    Token token = new Token();
    String token1 = "aaaaa";

    // Act
    token.setToken(token1);

    // Assert
    assertEquals("aaaaa", token.getToken());
  }
}
