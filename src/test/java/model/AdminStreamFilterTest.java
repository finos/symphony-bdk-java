package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.AdminStreamFilter;
import org.junit.Test;

public class AdminStreamFilterTest {
  @Test
  public void AdminStreamFilterTest() throws Exception {
    // Arrange and Act
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Assert
    assertEquals(null, adminStreamFilter.getOrigin());
  }

  @Test
  public void getEndDateTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    Long actual = adminStreamFilter.getEndDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOriginTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    String actual = adminStreamFilter.getOrigin();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPrivacyTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    String actual = adminStreamFilter.getPrivacy();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getScopeTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    String actual = adminStreamFilter.getScope();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStartDateTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    Long actual = adminStreamFilter.getStartDate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStatusTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    String actual = adminStreamFilter.getStatus();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getStreamTypesTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    List<String> actual = adminStreamFilter.getStreamTypes();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setEndDateTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    Long endDate = new Long(1L);

    // Act
    adminStreamFilter.setEndDate(endDate);

    // Assert
    assertEquals(Long.valueOf(1L), adminStreamFilter.getEndDate());
  }

  @Test
  public void setOriginTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    String origin = "aaaaa";

    // Act
    adminStreamFilter.setOrigin(origin);

    // Assert
    assertEquals("aaaaa", adminStreamFilter.getOrigin());
  }

  @Test
  public void setPrivacyTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    String privacy = "aaaaa";

    // Act
    adminStreamFilter.setPrivacy(privacy);

    // Assert
    assertEquals("aaaaa", adminStreamFilter.getPrivacy());
  }

  @Test
  public void setScopeTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    String scope = "aaaaa";

    // Act
    adminStreamFilter.setScope(scope);

    // Assert
    assertEquals("aaaaa", adminStreamFilter.getScope());
  }

  @Test
  public void setStartDateTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    Long startDate = new Long(1L);

    // Act
    adminStreamFilter.setStartDate(startDate);

    // Assert
    assertEquals(Long.valueOf(1L), adminStreamFilter.getStartDate());
  }

  @Test
  public void setStatusTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    String status = "aaaaa";

    // Act
    adminStreamFilter.setStatus(status);

    // Assert
    assertEquals("aaaaa", adminStreamFilter.getStatus());
  }

  @Test
  public void setStreamTypesTest() throws Exception {
    // Arrange
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    adminStreamFilter.setStreamTypes(arrayList);

    // Assert
    assertSame(arrayList, adminStreamFilter.getStreamTypes());
  }
}
