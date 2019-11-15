package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.RoomInfo;
import model.RoomSearchResult;
import org.junit.Test;

public class RoomSearchResultTest {
  @Test
  public void RoomSearchResultTest() throws Exception {
    // Arrange and Act
    RoomSearchResult roomSearchResult = new RoomSearchResult();

    // Assert
    assertEquals(0, roomSearchResult.getSkip());
  }

  @Test
  public void getCountTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();

    // Act
    int actual = roomSearchResult.getCount();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getLimitTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();

    // Act
    int actual = roomSearchResult.getLimit();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getQueryTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();

    // Act
    RoomSearchQuery actual = roomSearchResult.getQuery();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getRoomsTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();

    // Act
    List<RoomInfo> actual = roomSearchResult.getRooms();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSkipTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();

    // Act
    int actual = roomSearchResult.getSkip();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void setCountTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();
    int count = 1;

    // Act
    roomSearchResult.setCount(count);

    // Assert
    assertEquals(1, roomSearchResult.getCount());
  }

  @Test
  public void setLimitTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();
    int limit = 1;

    // Act
    roomSearchResult.setLimit(limit);

    // Assert
    assertEquals(1, roomSearchResult.getLimit());
  }

  @Test
  public void setQueryTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    roomSearchResult.setQuery(roomSearchQuery);

    // Assert
    assertSame(roomSearchQuery, roomSearchResult.getQuery());
  }

  @Test
  public void setRoomsTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();
    ArrayList<RoomInfo> arrayList = new ArrayList<RoomInfo>();
    arrayList.add(new RoomInfo());

    // Act
    roomSearchResult.setRooms(arrayList);

    // Assert
    assertSame(arrayList, roomSearchResult.getRooms());
  }

  @Test
  public void setSkipTest() throws Exception {
    // Arrange
    RoomSearchResult roomSearchResult = new RoomSearchResult();
    int skip = 1;

    // Act
    roomSearchResult.setSkip(skip);

    // Assert
    assertEquals(1, roomSearchResult.getSkip());
  }
}
