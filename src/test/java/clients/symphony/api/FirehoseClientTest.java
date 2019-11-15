package clients.symphony.api;

import clients.SymBotClient;
import clients.symphony.api.FirehoseClient;
import java.util.List;
import model.DatafeedEvent;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FirehoseClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void createFirehoseTest() throws Exception {
    // Arrange
    FirehoseClient firehoseClient = new FirehoseClient(null);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    firehoseClient.createFirehose();
  }

  @Test
  public void readFirehoseTest() throws Exception {
    // Arrange
    FirehoseClient firehoseClient = new FirehoseClient(null);
    String id = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    firehoseClient.readFirehose(id);
  }
}
