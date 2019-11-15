package model;

import static org.junit.Assert.assertEquals;
import model.DatafeedEventsList;
import org.junit.Test;

public class DatafeedEventsListTest {
  @Test
  public void DatafeedEventsListTest() throws Exception {
    // Arrange and Act
    DatafeedEventsList datafeedEventsList = new DatafeedEventsList();

    // Assert
    assertEquals(0, datafeedEventsList.size());
  }
}
