package model;

import static org.junit.Assert.assertEquals;
import model.NumericId;
import org.junit.Test;

public class NumericIdTest {
  @Test
  public void NumericIdTest() throws Exception {
    // Arrange and Act
    NumericId numericId = new NumericId();

    // Assert
    assertEquals(0L, numericId.getId());
  }

  @Test
  public void NumericIdTest2() throws Exception {
    // Arrange
    Long userId = new Long(1L);

    // Act
    NumericId numericId = new NumericId(userId);

    // Assert
    assertEquals(1L, numericId.getId());
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    NumericId numericId = new NumericId();

    // Act
    long actual = numericId.getId();

    // Assert
    assertEquals(0L, actual);
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    NumericId numericId = new NumericId();
    long id = 1L;

    // Act
    numericId.setId(id);

    // Assert
    assertEquals(1L, numericId.getId());
  }
}
