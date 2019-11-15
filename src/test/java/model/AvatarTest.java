package model;

import static org.junit.Assert.assertEquals;
import model.Avatar;
import org.junit.Test;

public class AvatarTest {
  @Test
  public void AvatarTest() throws Exception {
    // Arrange and Act
    Avatar avatar = new Avatar();

    // Assert
    assertEquals(null, avatar.getUrl());
  }

  @Test
  public void getSizeTest() throws Exception {
    // Arrange
    Avatar avatar = new Avatar();

    // Act
    String actual = avatar.getSize();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUrlTest() throws Exception {
    // Arrange
    Avatar avatar = new Avatar();

    // Act
    String actual = avatar.getUrl();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setSizeTest() throws Exception {
    // Arrange
    Avatar avatar = new Avatar();
    String size = "aaaaa";

    // Act
    avatar.setSize(size);

    // Assert
    assertEquals("aaaaa", avatar.getSize());
  }

  @Test
  public void setUrlTest() throws Exception {
    // Arrange
    Avatar avatar = new Avatar();
    String url = "aaaaa";

    // Act
    avatar.setUrl(url);

    // Assert
    assertEquals("aaaaa", avatar.getUrl());
  }
}
