package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import model.FileAttachment;
import org.junit.Test;

public class FileAttachmentTest {
  @Test
  public void FileAttachmentTest() throws Exception {
    // Arrange and Act
    FileAttachment fileAttachment = new FileAttachment();

    // Assert
    assertEquals(null, fileAttachment.getFileName());
  }

  @Test
  public void getFileContentTest() throws Exception {
    // Arrange
    FileAttachment fileAttachment = new FileAttachment();

    // Act
    byte[] actual = fileAttachment.getFileContent();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFileNameTest() throws Exception {
    // Arrange
    FileAttachment fileAttachment = new FileAttachment();

    // Act
    String actual = fileAttachment.getFileName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSizeTest() throws Exception {
    // Arrange
    FileAttachment fileAttachment = new FileAttachment();

    // Act
    Long actual = fileAttachment.getSize();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setFileContentTest() throws Exception {
    // Arrange
    FileAttachment fileAttachment = new FileAttachment();
    byte[] byteArray = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    // Act
    fileAttachment.setFileContent(byteArray);

    // Assert
    assertSame(byteArray, fileAttachment.getFileContent());
  }

  @Test
  public void setFileNameTest() throws Exception {
    // Arrange
    FileAttachment fileAttachment = new FileAttachment();
    String fileName = "aaaaa";

    // Act
    fileAttachment.setFileName(fileName);

    // Assert
    assertEquals("aaaaa", fileAttachment.getFileName());
  }

  @Test
  public void setSizeTest() throws Exception {
    // Arrange
    FileAttachment fileAttachment = new FileAttachment();
    Long size = new Long(1L);

    // Act
    fileAttachment.setSize(size);

    // Assert
    assertEquals(Long.valueOf(1L), fileAttachment.getSize());
  }
}
