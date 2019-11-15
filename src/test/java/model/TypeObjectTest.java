package model;

import static org.junit.Assert.assertEquals;
import model.TypeObject;
import org.junit.Test;

public class TypeObjectTest {
  @Test
  public void TypeObjectTest() throws Exception {
    // Arrange and Act
    TypeObject typeObject = new TypeObject();

    // Assert
    assertEquals(null, typeObject.getType());
  }

  @Test
  public void getTypeTest() throws Exception {
    // Arrange
    TypeObject typeObject = new TypeObject();

    // Act
    String actual = typeObject.getType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setTypeTest() throws Exception {
    // Arrange
    TypeObject typeObject = new TypeObject();
    String type = "aaaaa";

    // Act
    typeObject.setType(type);

    // Assert
    assertEquals("aaaaa", typeObject.getType());
  }
}
