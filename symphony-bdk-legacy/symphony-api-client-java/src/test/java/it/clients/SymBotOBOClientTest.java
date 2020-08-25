package it.clients;

import authentication.SymBotRSAAuth;
import authentication.SymOBOUserRSAAuth;
import clients.SymOBOClient;
import it.commons.BaseTest;
import org.junit.Test;

import static org.junit.Assert.*;

public class SymBotOBOClientTest extends BaseTest {
  @Test
  public void initOBOClientSuccess() {
    SymOBOClient symOBOClient = SymOBOClient.initOBOClient(config, new SymBotRSAAuth(config));
    assertNotNull(symOBOClient);
  }

  @Test
  public void initOBOClientWithSameUserAuth() {
    SymOBOUserRSAAuth userAuth = new SymOBOUserRSAAuth(config, null,"username", null);
    SymOBOClient symOBOClient1 = SymOBOClient.initOBOClient(config, userAuth);
    SymOBOClient symOBOClient2 = SymOBOClient.initOBOClient(config, userAuth);
    assertNotEquals(symOBOClient1, symOBOClient2);
  }

  @Test
  public void initOBOClientWithDiffUserAuth() {
    SymOBOUserRSAAuth userAuth = new SymOBOUserRSAAuth(config, null, "username-1", null);
    SymOBOClient symOBOClient1 = SymOBOClient.initOBOClient(config, userAuth);
    SymOBOClient symOBOClient2 = SymOBOClient.initOBOClient(config,
            new SymOBOUserRSAAuth(config, null, "username-2", null));
    assertNotEquals(symOBOClient1, symOBOClient2);
  }
}
