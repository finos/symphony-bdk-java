package model;

import static org.junit.Assert.assertEquals;
import model.StreamInfoList;
import org.junit.Test;

public class StreamInfoListTest {
  @Test
  public void StreamInfoListTest() throws Exception {
    // Arrange and Act
    StreamInfoList streamInfoList = new StreamInfoList();

    // Assert
    assertEquals(0, streamInfoList.size());
  }
}
