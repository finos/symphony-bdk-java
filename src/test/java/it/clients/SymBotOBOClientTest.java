package it.clients;

import authentication.SymBotRSAAuth;
import clients.SymOBOClient;
import it.commons.BaseTest;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;

public class SymBotOBOClientTest extends BaseTest {
  @Test
  public void initOBOClientSuccess() {
    SymOBOClient symOBOClient = SymOBOClient.initOBOClient(config, new SymBotRSAAuth(config));
    assertNotNull(symOBOClient);
  }
}
