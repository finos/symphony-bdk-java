package model;

import static org.junit.Assert.assertEquals;
import model.AvatarList;
import org.junit.Test;

public class AvatarListTest {
  @Test
  public void AvatarListTest() throws Exception {
    // Arrange and Act
    AvatarList avatarList = new AvatarList();

    // Assert
    assertEquals(0, avatarList.size());
  }
}
