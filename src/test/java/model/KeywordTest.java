package model;

import static org.junit.Assert.assertEquals;
import model.Keyword;
import org.junit.Test;

public class KeywordTest {
  @Test
  public void KeywordTest() throws Exception {
    // Arrange and Act
    Keyword keyword = new Keyword();

    // Assert
    assertEquals(null, keyword.getKey());
  }

  @Test
  public void getKeyTest() throws Exception {
    // Arrange
    Keyword keyword = new Keyword();

    // Act
    String actual = keyword.getKey();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getValueTest() throws Exception {
    // Arrange
    Keyword keyword = new Keyword();

    // Act
    String actual = keyword.getValue();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setKeyTest() throws Exception {
    // Arrange
    Keyword keyword = new Keyword();
    String key = "aaaaa";

    // Act
    keyword.setKey(key);

    // Assert
    assertEquals("aaaaa", keyword.getKey());
  }

  @Test
  public void setValueTest() throws Exception {
    // Arrange
    Keyword keyword = new Keyword();
    String value = "aaaaa";

    // Act
    keyword.setValue(value);

    // Assert
    assertEquals("aaaaa", keyword.getValue());
  }
}
