package model;

import static org.junit.Assert.assertEquals;
import model.InboundMessageList;
import org.junit.Test;

public class InboundMessageListTest {
  @Test
  public void InboundMessageListTest() throws Exception {
    // Arrange and Act
    InboundMessageList inboundMessageList = new InboundMessageList();

    // Assert
    assertEquals(0, inboundMessageList.size());
  }
}
