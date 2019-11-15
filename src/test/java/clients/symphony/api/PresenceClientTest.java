package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.PresenceClient;
import configuration.SymConfig;
import java.util.ArrayList;
import model.Presence;
import model.UserPresence;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PresenceClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void PresenceClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new PresenceClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void getUserPresenceTest() throws Exception {
    // Arrange
    PresenceClient presenceClient = new PresenceClient(new SymOBOClient(new SymConfig(), null));
    Long userId = new Long(1L);
    boolean local = true;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    presenceClient.getUserPresence(userId, local);
  }

  @Test
  public void registerInterestExtUserTest() throws Exception {
    // Arrange
    PresenceClient presenceClient = new PresenceClient(new SymOBOClient(new SymConfig(), null));
    ArrayList<Long> arrayList = new ArrayList<Long>();
    arrayList.add(new Long(1L));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    presenceClient.registerInterestExtUser(arrayList);
  }

  @Test
  public void setPresenceTest() throws Exception {
    // Arrange
    PresenceClient presenceClient = new PresenceClient(new SymOBOClient(new SymConfig(), null));
    Presence status = Presence.AVAILABLE;

    // Act and Assert
    thrown.expect(NullPointerException.class);
    presenceClient.setPresence(status);
  }

  @Test
  public void setPresenceTest2() throws Exception {
    // Arrange
    PresenceClient presenceClient = new PresenceClient(new SymOBOClient(new SymConfig(), null));
    String status = "aaaaa";

    // Act and Assert
    thrown.expect(NullPointerException.class);
    presenceClient.setPresence(status);
  }
}
