package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.SignalsClient;
import configuration.SymConfig;
import java.util.ArrayList;
import java.util.List;
import model.Signal;
import model.SignalSubscriberList;
import model.SignalSubscriptionResult;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SignalsClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void SignalsClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new SignalsClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void createSignalTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    Signal signal = new Signal();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.createSignal(signal);
  }

  @Test
  public void deleteSignalTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    String id = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.deleteSignal(id);
  }

  @Test
  public void getSignalSubscribersTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    String id = "aaaaa";
    int skip = 1;
    int limit = 2561;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.getSignalSubscribers(id, skip, limit);
  }

  @Test
  public void getSignalTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    String id = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.getSignal(id);
  }

  @Test
  public void listSignalsTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    int skip = 1;
    int limit = 1;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.listSignals(skip, limit);
  }

  @Test
  public void subscribeSignalTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    String id = "aaaaa";
    boolean self = true;
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(null);
    boolean pushed = true;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.subscribeSignal(id, self, arrayList, pushed);
  }

  @Test
  public void unsubscribeSignalTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    String id = "aaaaa";
    boolean self = true;
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(null);

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.unsubscribeSignal(id, self, arrayList);
  }

  @Test
  public void updateSignalTest() throws Exception {
    // Arrange
    SignalsClient signalsClient = new SignalsClient(new SymOBOClient(new SymConfig(), null));
    Signal signal = new Signal();

    // Act and Assert
    thrown.expect(NullPointerException.class);
    signalsClient.updateSignal(signal);
  }
}
