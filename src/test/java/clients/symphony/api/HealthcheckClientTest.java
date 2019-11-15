package clients.symphony.api;

import static org.junit.Assert.assertEquals;
import authentication.ISymAuth;
import authentication.SymBotAuth;
import clients.SymOBOClient;
import clients.symphony.api.HealthcheckClient;
import configuration.SymConfig;
import model.HealthcheckResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HealthcheckClientTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void HealthcheckClientTest() throws Exception {
    // Arrange
    SymOBOClient symOBOClient = new SymOBOClient(new SymConfig(), null);

    // Act
    new HealthcheckClient(symOBOClient);

    // Assert
    assertEquals(null, symOBOClient.getSymAuth());
  }

  @Test
  public void performHealthCheckTest() throws Exception {
    // Arrange
    HealthcheckClient healthcheckClient = new HealthcheckClient(new SymOBOClient(new SymConfig(), null));

    // Act and Assert
    thrown.expect(NullPointerException.class);
    healthcheckClient.performHealthCheck();
  }
}
