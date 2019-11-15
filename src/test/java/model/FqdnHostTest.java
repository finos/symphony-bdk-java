package model;

import static org.junit.Assert.assertEquals;
import model.FqdnHost;
import org.junit.Test;

public class FqdnHostTest {
  @Test
  public void FqdnHostTest() throws Exception {
    // Arrange and Act
    FqdnHost fqdnHost = new FqdnHost();

    // Assert
    assertEquals(null, fqdnHost.getServerFqdn());
  }

  @Test
  public void getServerFqdnTest() throws Exception {
    // Arrange
    FqdnHost fqdnHost = new FqdnHost();

    // Act
    String actual = fqdnHost.getServerFqdn();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setServerFqdnTest() throws Exception {
    // Arrange
    FqdnHost fqdnHost = new FqdnHost();
    String serverFqdn = "aaaaa";

    // Act
    fqdnHost.setServerFqdn(serverFqdn);

    // Assert
    assertEquals("aaaaa", fqdnHost.getServerFqdn());
  }
}
