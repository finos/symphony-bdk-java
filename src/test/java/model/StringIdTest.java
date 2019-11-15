package model;

import static org.junit.Assert.assertEquals;
import model.StringId;
import org.junit.Test;

public class StringIdTest {
  @Test
  public void StringIdTest() throws Exception {
    // Arrange and Act
    StringId stringId = new StringId();

    // Assert
    assertEquals(null, stringId.getId());
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    StringId stringId = new StringId();

    // Act
    String actual = stringId.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    StringId stringId = new StringId();
    String id = "aaaaa";

    // Act
    stringId.setId(id);

    // Assert
    assertEquals("aaaaa", stringId.getId());
  }
}
