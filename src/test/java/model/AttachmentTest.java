package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.Attachment;
import org.junit.Test;

public class AttachmentTest {
  @Test
  public void AttachmentTest() throws Exception {
    // Arrange and Act
    Attachment attachment = new Attachment();

    // Assert
    assertEquals(null, attachment.getSize());
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();

    // Act
    String actual = attachment.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getImageTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();

    // Act
    ImageInfo actual = attachment.getImage();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getNameTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();

    // Act
    String actual = attachment.getName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSizeTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();

    // Act
    Long actual = attachment.getSize();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();
    String id = "aaaaa";

    // Act
    attachment.setId(id);

    // Assert
    assertEquals("aaaaa", attachment.getId());
  }

  @Test
  public void setImageTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();
    ImageInfo imageInfo = new ImageInfo();

    // Act
    attachment.setImage(imageInfo);

    // Assert
    assertSame(imageInfo, attachment.getImage());
  }

  @Test
  public void setNameTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();
    String name = "aaaaa";

    // Act
    attachment.setName(name);

    // Assert
    assertEquals("aaaaa", attachment.getName());
  }

  @Test
  public void setSizeTest() throws Exception {
    // Arrange
    Attachment attachment = new Attachment();
    Long size = new Long(1L);

    // Act
    attachment.setSize(size);

    // Assert
    assertEquals(Long.valueOf(1L), attachment.getSize());
  }
}
