package model;

import static org.junit.Assert.assertEquals;
import model.SignalList;
import org.junit.Test;

public class SignalListTest {
  @Test
  public void SignalListTest() throws Exception {
    // Arrange and Act
    SignalList signalList = new SignalList();

    // Assert
    assertEquals(0, signalList.size());
  }
}
