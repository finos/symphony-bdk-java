package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.AdminUserAttributes;
import org.junit.Test;

public class AdminUserAttributesTest {
  @Test
  public void AdminUserAttributesTest() throws Exception {
    // Arrange and Act
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Assert
    assertEquals(null, adminUserAttributes.getLocation());
  }

  @Test
  public void getAccountTypeTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getAccountType();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAssetClassesTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    List<String> actual = adminUserAttributes.getAssetClasses();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCompanyNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getCompanyName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCurrentKeyTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    UserKey actual = adminUserAttributes.getCurrentKey();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDepartmentTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getDepartment();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDisplayNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getDisplayName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getDivisionTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getDivision();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getEmailAddressTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getEmailAddress();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getFirstNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getFirstName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getIndustriesTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    List<String> actual = adminUserAttributes.getIndustries();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getJobFunctionTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getJobFunction();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLastNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getLastName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLocationTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getLocation();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMobilePhoneNumberTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getMobilePhoneNumber();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPreviousKeyTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    UserKey actual = adminUserAttributes.getPreviousKey();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSmsNumberTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getSmsNumber();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTitleTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getTitle();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTwoFactorAuthPhoneTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getTwoFactorAuthPhone();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getUserNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getUserName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getWorkPhoneNumberTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();

    // Act
    String actual = adminUserAttributes.getWorkPhoneNumber();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAccountTypeTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String accountType = "aaaaa";

    // Act
    adminUserAttributes.setAccountType(accountType);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getAccountType());
  }

  @Test
  public void setAssetClassesTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    adminUserAttributes.setAssetClasses(arrayList);

    // Assert
    assertSame(arrayList, adminUserAttributes.getAssetClasses());
  }

  @Test
  public void setCompanyNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String companyName = "aaaaa";

    // Act
    adminUserAttributes.setCompanyName(companyName);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getCompanyName());
  }

  @Test
  public void setCurrentKeyTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    UserKey userKey = new UserKey();

    // Act
    adminUserAttributes.setCurrentKey(userKey);

    // Assert
    assertSame(userKey, adminUserAttributes.getCurrentKey());
  }

  @Test
  public void setDepartmentTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String department = "aaaaa";

    // Act
    adminUserAttributes.setDepartment(department);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getDepartment());
  }

  @Test
  public void setDisplayNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String displayName = "aaaaa";

    // Act
    adminUserAttributes.setDisplayName(displayName);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getDisplayName());
  }

  @Test
  public void setDivisionTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String division = "aaaaa";

    // Act
    adminUserAttributes.setDivision(division);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getDivision());
  }

  @Test
  public void setEmailAddressTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String emailAddress = "aaaaa";

    // Act
    adminUserAttributes.setEmailAddress(emailAddress);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getEmailAddress());
  }

  @Test
  public void setFirstNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String firstName = "aaaaa";

    // Act
    adminUserAttributes.setFirstName(firstName);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getFirstName());
  }

  @Test
  public void setIndustriesTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    adminUserAttributes.setIndustries(arrayList);

    // Assert
    assertSame(arrayList, adminUserAttributes.getIndustries());
  }

  @Test
  public void setJobFunctionTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String jobFunction = "aaaaa";

    // Act
    adminUserAttributes.setJobFunction(jobFunction);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getJobFunction());
  }

  @Test
  public void setLastNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String lastName = "aaaaa";

    // Act
    adminUserAttributes.setLastName(lastName);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getLastName());
  }

  @Test
  public void setLocationTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String location = "aaaaa";

    // Act
    adminUserAttributes.setLocation(location);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getLocation());
  }

  @Test
  public void setMobilePhoneNumberTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String mobilePhoneNumber = "aaaaa";

    // Act
    adminUserAttributes.setMobilePhoneNumber(mobilePhoneNumber);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getMobilePhoneNumber());
  }

  @Test
  public void setPreviousKeyTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    UserKey userKey = new UserKey();

    // Act
    adminUserAttributes.setPreviousKey(userKey);

    // Assert
    assertSame(userKey, adminUserAttributes.getPreviousKey());
  }

  @Test
  public void setSmsNumberTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String smsNumber = "aaaaa";

    // Act
    adminUserAttributes.setSmsNumber(smsNumber);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getSmsNumber());
  }

  @Test
  public void setTitleTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String title = "aaaaa";

    // Act
    adminUserAttributes.setTitle(title);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getTitle());
  }

  @Test
  public void setTwoFactorAuthPhoneTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String twoFactorAuthPhone = "aaaaa";

    // Act
    adminUserAttributes.setTwoFactorAuthPhone(twoFactorAuthPhone);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getTwoFactorAuthPhone());
  }

  @Test
  public void setUserNameTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String userName = "aaaaa";

    // Act
    adminUserAttributes.setUserName(userName);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getUserName());
  }

  @Test
  public void setWorkPhoneNumberTest() throws Exception {
    // Arrange
    AdminUserAttributes adminUserAttributes = new AdminUserAttributes();
    String workPhoneNumber = "aaaaa";

    // Act
    adminUserAttributes.setWorkPhoneNumber(workPhoneNumber);

    // Assert
    assertEquals("aaaaa", adminUserAttributes.getWorkPhoneNumber());
  }
}
