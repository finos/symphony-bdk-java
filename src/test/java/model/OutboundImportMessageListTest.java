package model;

import static org.junit.Assert.assertEquals;
import model.OutboundImportMessageList;
import org.junit.Test;

public class OutboundImportMessageListTest {
  @Test
  public void OutboundImportMessageListTest() throws Exception {
    // Arrange and Act
    OutboundImportMessageList outboundImportMessageList = new OutboundImportMessageList();

    // Assert
    assertEquals(0, outboundImportMessageList.size());
  }
}
