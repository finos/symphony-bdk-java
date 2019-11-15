package model;

import static org.junit.Assert.assertEquals;
import model.MemberList;
import org.junit.Test;

public class MemberListTest {
  @Test
  public void MemberListTest() throws Exception {
    // Arrange and Act
    MemberList memberList = new MemberList();

    // Assert
    assertEquals(0, memberList.size());
  }
}
