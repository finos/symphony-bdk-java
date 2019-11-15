package model.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.AdminStreamFilter;
import model.AdminStreamInfo;
import model.events.AdminStreamInfoList;
import org.junit.Test;

public class AdminStreamInfoListTest {
  @Test
  public void AdminStreamInfoListTest() throws Exception {
    // Arrange and Act
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();

    // Assert
    assertEquals(0, adminStreamInfoList.getSkip());
  }

  @Test
  public void getCountTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();

    // Act
    int actual = adminStreamInfoList.getCount();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getFilterTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();

    // Act
    AdminStreamFilter actual = adminStreamInfoList.getFilter();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLimitTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();

    // Act
    int actual = adminStreamInfoList.getLimit();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getSkipTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();

    // Act
    int actual = adminStreamInfoList.getSkip();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getStreamsTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();

    // Act
    List<AdminStreamInfo> actual = adminStreamInfoList.getStreams();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setCountTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();
    int count = 1;

    // Act
    adminStreamInfoList.setCount(count);

    // Assert
    assertEquals(1, adminStreamInfoList.getCount());
  }

  @Test
  public void setFilterTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();
    AdminStreamFilter adminStreamFilter = new AdminStreamFilter();

    // Act
    adminStreamInfoList.setFilter(adminStreamFilter);

    // Assert
    assertSame(adminStreamFilter, adminStreamInfoList.getFilter());
  }

  @Test
  public void setLimitTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();
    int limit = 1;

    // Act
    adminStreamInfoList.setLimit(limit);

    // Assert
    assertEquals(1, adminStreamInfoList.getLimit());
  }

  @Test
  public void setSkipTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();
    int skip = 1;

    // Act
    adminStreamInfoList.setSkip(skip);

    // Assert
    assertEquals(1, adminStreamInfoList.getSkip());
  }

  @Test
  public void setStreamsTest() throws Exception {
    // Arrange
    AdminStreamInfoList adminStreamInfoList = new AdminStreamInfoList();
    ArrayList<AdminStreamInfo> arrayList = new ArrayList<AdminStreamInfo>();
    arrayList.add(new AdminStreamInfo());

    // Act
    adminStreamInfoList.setStreams(arrayList);

    // Assert
    assertSame(arrayList, adminStreamInfoList.getStreams());
  }
}
