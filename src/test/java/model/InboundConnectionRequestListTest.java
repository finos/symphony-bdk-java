package model;

import static org.junit.Assert.assertEquals;
import model.InboundConnectionRequestList;
import org.junit.Test;

public class InboundConnectionRequestListTest {
  @Test
  public void InboundConnectionRequestListTest() throws Exception {
    // Arrange and Act
    InboundConnectionRequestList inboundConnectionRequestList = new InboundConnectionRequestList();

    // Assert
    assertEquals(0, inboundConnectionRequestList.size());
  }
}
