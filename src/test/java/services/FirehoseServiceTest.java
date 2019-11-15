package services;

import clients.SymBotClient;
import listeners.FirehoseListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;
import services.FirehoseService;

public class FirehoseServiceTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void FirehoseServiceTest() throws Exception {
    // Arrange
    SymBotClient client = null;
    String firehoseId = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new FirehoseService(client, firehoseId);
  }

  @Test
  public void FirehoseServiceTest2() throws Exception {
    // Arrange
    SymBotClient client = null;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new FirehoseService(client);
  }

  @Test
  public void readFirehoseTest() throws Exception {
    // Arrange
    FirehoseService firehoseService = Whitebox.newInstance(FirehoseService.class);

    // Act
    firehoseService.readFirehose();
  }
}
