package it.clients;

import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import authentication.SymBotRSAAuth;
import clients.SymOBOClient;
import it.commons.BaseTest;

public class SymBotOBOClientTest extends BaseTest {
  @Test
  public void initOBOClientSuccess() {
    SymOBOClient symOBOClient = SymOBOClient.initOBOClient(config, new SymBotRSAAuth(config));
    assertNotNull(symOBOClient);
  }
}
