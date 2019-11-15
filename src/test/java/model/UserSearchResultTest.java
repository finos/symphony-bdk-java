package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.UserSearchResult;
import org.junit.Test;

public class UserSearchResultTest {
  @Test
  public void UserSearchResultTest() throws Exception {
    // Arrange and Act
    UserSearchResult userSearchResult = new UserSearchResult();

    // Assert
    assertEquals(null, userSearchResult.getQuery());
  }

  @Test
  public void getCountTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();

    // Act
    int actual = userSearchResult.getCount();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getFiltersTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();

    // Act
    Map<String, String> actual = userSearchResult.getFilters();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLimitTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();

    // Act
    int actual = userSearchResult.getLimit();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getQueryTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();

    // Act
    String actual = userSearchResult.getQuery();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSkipTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();

    // Act
    int actual = userSearchResult.getSkip();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getUsersTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();

    // Act
    List<UserInfo> actual = userSearchResult.getUsers();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCountTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();
    int count = 1;

    // Act
    userSearchResult.setCount(count);

    // Assert
    assertEquals(1, userSearchResult.getCount());
  }

  @Test
  public void setFiltersTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();
    HashMap<String, String> hashMap = new HashMap<String, String>();
    hashMap.put("aaaaa", "aaaaa");

    // Act
    userSearchResult.setFilters(hashMap);

    // Assert
    assertSame(hashMap, userSearchResult.getFilters());
  }

  @Test
  public void setLimitTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();
    int limit = 1;

    // Act
    userSearchResult.setLimit(limit);

    // Assert
    assertEquals(1, userSearchResult.getLimit());
  }

  @Test
  public void setQueryTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();
    String query = "aaaaa";

    // Act
    userSearchResult.setQuery(query);

    // Assert
    assertEquals("aaaaa", userSearchResult.getQuery());
  }

  @Test
  public void setSkipTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();
    int skip = 1;

    // Act
    userSearchResult.setSkip(skip);

    // Assert
    assertEquals(1, userSearchResult.getSkip());
  }

  @Test
  public void setUsersTest() throws Exception {
    // Arrange
    UserSearchResult userSearchResult = new UserSearchResult();
    ArrayList<UserInfo> arrayList = new ArrayList<UserInfo>();
    arrayList.add(new UserInfo());

    // Act
    userSearchResult.setUsers(arrayList);

    // Assert
    assertSame(arrayList, userSearchResult.getUsers());
  }
}
