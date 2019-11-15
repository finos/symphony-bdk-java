package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.UserInfo;
import model.UserInfoList;
import org.junit.Test;

public class UserInfoListTest {
  @Test
  public void UserInfoListTest() throws Exception {
    // Arrange and Act
    UserInfoList userInfoList = new UserInfoList();

    // Assert
    assertEquals(null, userInfoList.getUsers());
  }

  @Test
  public void getUsersTest() throws Exception {
    // Arrange
    UserInfoList userInfoList = new UserInfoList();

    // Act
    List<UserInfo> actual = userInfoList.getUsers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setUsersTest() throws Exception {
    // Arrange
    UserInfoList userInfoList = new UserInfoList();
    ArrayList<UserInfo> arrayList = new ArrayList<UserInfo>();
    arrayList.add(new UserInfo());

    // Act
    userInfoList.setUsers(arrayList);

    // Assert
    assertSame(arrayList, userInfoList.getUsers());
  }
}
