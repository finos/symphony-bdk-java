package model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import java.util.ArrayList;
import java.util.List;
import model.NumericId;
import model.RoomSearchQuery;
import org.junit.Test;

public class RoomSearchQueryTest {
  @Test
  public void RoomSearchQueryTest() throws Exception {
    // Arrange and Act
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Assert
    assertEquals(null, roomSearchQuery.getQuery());
  }

  @Test
  public void getActiveTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    Boolean actual = roomSearchQuery.getActive();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getCreatorTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    NumericId actual = roomSearchQuery.getCreator();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getLabelsTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    List<String> actual = roomSearchQuery.getLabels();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getMemberTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    NumericId actual = roomSearchQuery.getMember();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getOwnerTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    NumericId actual = roomSearchQuery.getOwner();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPrivateTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    Boolean actual = roomSearchQuery.getPrivate();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getQueryTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();

    // Act
    String actual = roomSearchQuery.getQuery();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setActiveTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    Boolean active = new Boolean(true);

    // Act
    roomSearchQuery.setActive(active);

    // Assert
    assertEquals(Boolean.valueOf(true), roomSearchQuery.getActive());
  }

  @Test
  public void setCreatorTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    NumericId numericId = new NumericId();

    // Act
    roomSearchQuery.setCreator(numericId);

    // Assert
    assertSame(numericId, roomSearchQuery.getCreator());
  }

  @Test
  public void setLabelsTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    ArrayList<String> arrayList = new ArrayList<String>();
    arrayList.add("aaaaa");

    // Act
    roomSearchQuery.setLabels(arrayList);

    // Assert
    assertSame(arrayList, roomSearchQuery.getLabels());
  }

  @Test
  public void setMemberTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    NumericId numericId = new NumericId();

    // Act
    roomSearchQuery.setMember(numericId);

    // Assert
    assertSame(numericId, roomSearchQuery.getMember());
  }

  @Test
  public void setOwnerTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    NumericId numericId = new NumericId();

    // Act
    roomSearchQuery.setOwner(numericId);

    // Assert
    assertSame(numericId, roomSearchQuery.getOwner());
  }

  @Test
  public void setPrivateTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    Boolean isPrivate = new Boolean(true);

    // Act
    roomSearchQuery.setPrivate(isPrivate);

    // Assert
    assertEquals(Boolean.valueOf(true), roomSearchQuery.getPrivate());
  }

  @Test
  public void setQueryTest() throws Exception {
    // Arrange
    RoomSearchQuery roomSearchQuery = new RoomSearchQuery();
    String query = "aaaaa";

    // Act
    roomSearchQuery.setQuery(query);

    // Assert
    assertEquals("aaaaa", roomSearchQuery.getQuery());
  }
}
