package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.UserInfo;
import org.junit.Test;

public class UserInfoTest {
  @Test
  public void UserInfoTest() throws Exception {
    // Arrange and Act
    UserInfo userInfo = new UserInfo();

    // Assert
    assertEquals(null, userInfo.getLocation());
  }

  @Test
  public void getAvatarsTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    List<Avatar> actual = userInfo.getAvatars();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCompanyTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getCompany();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDepartmentTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getDepartment();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDisplayNameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getDisplayName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDivisionTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getDivision();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEmailAddressTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getEmailAddress();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirstNameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getFirstName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIdTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    Long actual = userInfo.getId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getJobFunctionTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getJobFunction();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastNameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getLastName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLocationTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getLocation();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMobilePhoneNumberTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getMobilePhoneNumber();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTitleTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getTitle();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUsernameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getUsername();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getWorkPhoneNumberTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();

    // Act
    String actual = userInfo.getWorkPhoneNumber();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAvatarsTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    ArrayList<Avatar> arrayList = new ArrayList<Avatar>();
    arrayList.add(new Avatar());

    // Act
    userInfo.setAvatars(arrayList);

    // Assert
    assertSame(arrayList, userInfo.getAvatars());
  }

  @Test
  public void setCompanyTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String company = "aaaaa";

    // Act
    userInfo.setCompany(company);

    // Assert
    assertEquals("aaaaa", userInfo.getCompany());
  }

  @Test
  public void setDepartmentTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String department = "aaaaa";

    // Act
    userInfo.setDepartment(department);

    // Assert
    assertEquals("aaaaa", userInfo.getDepartment());
  }

  @Test
  public void setDisplayNameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String displayName = "aaaaa";

    // Act
    userInfo.setDisplayName(displayName);

    // Assert
    assertEquals("aaaaa", userInfo.getDisplayName());
  }

  @Test
  public void setDivisionTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String division = "aaaaa";

    // Act
    userInfo.setDivision(division);

    // Assert
    assertEquals("aaaaa", userInfo.getDivision());
  }

  @Test
  public void setEmailAddressTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String emailAddress = "aaaaa";

    // Act
    userInfo.setEmailAddress(emailAddress);

    // Assert
    assertEquals("aaaaa", userInfo.getEmailAddress());
  }

  @Test
  public void setFirstNameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String firstName = "aaaaa";

    // Act
    userInfo.setFirstName(firstName);

    // Assert
    assertEquals("aaaaa", userInfo.getFirstName());
  }

  @Test
  public void setIdTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    Long id = new Long(1L);

    // Act
    userInfo.setId(id);

    // Assert
    assertEquals(Long.valueOf(1L), userInfo.getId());
  }

  @Test
  public void setJobFunctionTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String jobFunction = "aaaaa";

    // Act
    userInfo.setJobFunction(jobFunction);

    // Assert
    assertEquals("aaaaa", userInfo.getJobFunction());
  }

  @Test
  public void setLastNameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String lastName = "aaaaa";

    // Act
    userInfo.setLastName(lastName);

    // Assert
    assertEquals("aaaaa", userInfo.getLastName());
  }

  @Test
  public void setLocationTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String location = "aaaaa";

    // Act
    userInfo.setLocation(location);

    // Assert
    assertEquals("aaaaa", userInfo.getLocation());
  }

  @Test
  public void setMobilePhoneNumberTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String mobilePhoneNumber = "aaaaa";

    // Act
    userInfo.setMobilePhoneNumber(mobilePhoneNumber);

    // Assert
    assertEquals("aaaaa", userInfo.getMobilePhoneNumber());
  }

  @Test
  public void setTitleTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String title = "aaaaa";

    // Act
    userInfo.setTitle(title);

    // Assert
    assertEquals("aaaaa", userInfo.getTitle());
  }

  @Test
  public void setUsernameTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String username = "aaaaa";

    // Act
    userInfo.setUsername(username);

    // Assert
    assertEquals("aaaaa", userInfo.getUsername());
  }

  @Test
  public void setWorkPhoneNumberTest() throws Exception {
    // Arrange
    UserInfo userInfo = new UserInfo();
    String workPhoneNumber = "aaaaa";

    // Act
    userInfo.setWorkPhoneNumber(workPhoneNumber);

    // Assert
    assertEquals("aaaaa", userInfo.getWorkPhoneNumber());
  }
}
