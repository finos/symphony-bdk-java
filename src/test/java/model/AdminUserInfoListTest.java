package model;

import static org.junit.Assert.assertEquals;
import model.AdminUserInfoList;
import org.junit.Test;

public class AdminUserInfoListTest {
  @Test
  public void AdminUserInfoListTest() throws Exception {
    // Arrange and Act
    AdminUserInfoList adminUserInfoList = new AdminUserInfoList();

    // Assert
    assertEquals(0, adminUserInfoList.size());
  }
}
