package model;

import static org.junit.Assert.assertEquals;
import model.ImageInfo;
import org.junit.Test;

public class ImageInfoTest {
  @Test
  public void ImageInfoTest() throws Exception {
    // Arrange and Act
    ImageInfo imageInfo = new ImageInfo();

    // Assert
    assertEquals(null, imageInfo.getId());
  }

  @Test
  public void getDimensionTest() throws Exception {
    // Arrange
    ImageInfo imageInfo = new ImageInfo();

    // Act
    String actual = imageInfo.getDimension();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    ImageInfo imageInfo = new ImageInfo();

    // Act
    String actual = imageInfo.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setDimensionTest() throws Exception {
    // Arrange
    ImageInfo imageInfo = new ImageInfo();
    String dimension = "aaaaa";

    // Act
    imageInfo.setDimension(dimension);

    // Assert
    assertEquals("aaaaa", imageInfo.getDimension());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    ImageInfo imageInfo = new ImageInfo();
    String id = "aaaaa";

    // Act
    imageInfo.setId(id);

    // Assert
    assertEquals("aaaaa", imageInfo.getId());
  }
}
