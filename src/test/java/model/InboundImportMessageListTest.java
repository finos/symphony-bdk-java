package model;

import static org.junit.Assert.assertEquals;
import model.InboundImportMessageList;
import org.junit.Test;

public class InboundImportMessageListTest {
  @Test
  public void InboundImportMessageListTest() throws Exception {
    // Arrange and Act
    InboundImportMessageList inboundImportMessageList = new InboundImportMessageList();

    // Assert
    assertEquals(0, inboundImportMessageList.size());
  }
}
