package model;

import static org.junit.Assert.assertEquals;
import model.UserFilter;
import org.junit.Test;

public class UserFilterTest {
  @Test
  public void UserFilterTest() throws Exception {
    // Arrange and Act
    UserFilter userFilter = new UserFilter();

    // Assert
    assertEquals(null, userFilter.getLocation());
  }

  @Test
  public void getCompanyTest() throws Exception {
    // Arrange
    UserFilter userFilter = new UserFilter();

    // Act
    String actual = userFilter.getCompany();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLocationTest() throws Exception {
    // Arrange
    UserFilter userFilter = new UserFilter();

    // Act
    String actual = userFilter.getLocation();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTitleTest() throws Exception {
    // Arrange
    UserFilter userFilter = new UserFilter();

    // Act
    String actual = userFilter.getTitle();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCompanyTest() throws Exception {
    // Arrange
    UserFilter userFilter = new UserFilter();
    String company = "aaaaa";

    // Act
    userFilter.setCompany(company);

    // Assert
    assertEquals("aaaaa", userFilter.getCompany());
  }

  @Test
  public void setLocationTest() throws Exception {
    // Arrange
    UserFilter userFilter = new UserFilter();
    String location = "aaaaa";

    // Act
    userFilter.setLocation(location);

    // Assert
    assertEquals("aaaaa", userFilter.getLocation());
  }

  @Test
  public void setTitleTest() throws Exception {
    // Arrange
    UserFilter userFilter = new UserFilter();
    String title = "aaaaa";

    // Act
    userFilter.setTitle(title);

    // Assert
    assertEquals("aaaaa", userFilter.getTitle());
  }
}
