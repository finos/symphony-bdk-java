package services;

import static org.junit.Assert.assertEquals;
import clients.SymBotClient;
import listeners.ConnectionListener;
import listeners.DatafeedListener;
import listeners.ElementsListener;
import listeners.IMListener;
import listeners.RoomListener;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.powermock.reflect.Whitebox;
import services.DatafeedEventsService;

public class DatafeedEventsServiceTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void DatafeedEventsServiceTest() throws Exception {
    // Arrange
    SymBotClient client = null;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    new DatafeedEventsService(client);
  }

  @Test
  public void addListenersTest() throws Exception {
    // Arrange
    DatafeedEventsService datafeedEventsService = Whitebox.newInstance(DatafeedEventsService.class);
    DatafeedListener[] datafeedListenerArray = new DatafeedListener[]{Whitebox.newInstance(DatafeedListener.class),
        Whitebox.newInstance(DatafeedListener.class), Whitebox.newInstance(DatafeedListener.class)};

    // Act
    datafeedEventsService.addListeners(datafeedListenerArray);

    // Assert
    assertEquals(3, datafeedListenerArray.length);
  }

  @Test
  public void removeListenersTest() throws Exception {
    // Arrange
    DatafeedEventsService datafeedEventsService = Whitebox.newInstance(DatafeedEventsService.class);
    DatafeedListener[] datafeedListenerArray = new DatafeedListener[]{Whitebox.newInstance(DatafeedListener.class),
        Whitebox.newInstance(DatafeedListener.class), Whitebox.newInstance(DatafeedListener.class)};

    // Act
    datafeedEventsService.removeListeners(datafeedListenerArray);

    // Assert
    assertEquals(3, datafeedListenerArray.length);
  }
}
