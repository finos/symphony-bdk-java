package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.DatafeedClient;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DatafeedClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void DatafeedClientTest() throws Exception {
    // Arrange
    SymBotClient client = null;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new DatafeedClient(client);
  }
}
