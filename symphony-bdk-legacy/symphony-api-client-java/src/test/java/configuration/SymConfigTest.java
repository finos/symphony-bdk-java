package configuration;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.List;

import static org.junit.Assert.*;

public class SymConfigTest {

  private SymConfig symConfigAgentUrl443;
  private SymConfig symConfigAgentUrl7443;
  private SymConfig symConfigPodUrl7443;
  private SymConfig symConfigDatafeedIdFilepathWithSeparator;
  private SymConfig symConfigDatafeedIdFilepathNull;
  private SymConfig symConfigDatafeedIdFilepathEmpty;

  @Before
  public void loadSymConfig(){
    symConfigAgentUrl443 = SymConfigLoader.loadFromFile("src/test/resources/sym-config-agent-url-443.json");
    assertNotNull(symConfigAgentUrl443);

    symConfigAgentUrl7443 = SymConfigLoader.loadFromFile("src/test/resources/sym-config.json");
    assertNotNull(symConfigAgentUrl7443);

    symConfigPodUrl7443 = SymConfigLoader.loadFromFile("src/test/resources/sym-config-agent-podport-7443.json");
    assertNotNull(symConfigPodUrl7443);

    symConfigDatafeedIdFilepathWithSeparator = SymConfigLoader.loadFromFile("src/test/resources/sym-config-datafeedIdFilepathWithSeparator.json");
    assertNotNull(symConfigDatafeedIdFilepathWithSeparator);

    symConfigDatafeedIdFilepathNull = SymConfigLoader.loadFromFile("src/test/resources/sym-config-datafeedIdFilepathNull.json");
    assertNotNull(symConfigDatafeedIdFilepathNull);

    symConfigDatafeedIdFilepathEmpty = SymConfigLoader.loadFromFile("src/test/resources/sym-config-datafeedIdFilepathEmpty.json");
    assertNotNull(symConfigDatafeedIdFilepathEmpty);
  }

  // agentUrl
  @Test
  public void testGetAgentUrl443(){
    final String expectedAgentUrl = "https://localhost/src";
    assertEquals(expectedAgentUrl, symConfigAgentUrl443.getAgentUrl());
  }

  @Test
  public void testGetAgentUrl7443(){
    final String expectedAgentUrl = "https://localhost:7443/src";
    assertEquals(expectedAgentUrl, symConfigAgentUrl7443.getAgentUrl());
  }
  // End agentUrl

  // podUrl
  @Test
  public void testGetPodUrl443(){
    final String expectedPodUrl = "https://localhost/src/test/resources";
    assertEquals(expectedPodUrl, symConfigAgentUrl443.getPodUrl());
  }

  @Test
  public void testGetPodUrl7443(){
    final String expectedPodUrl = "https://localhost:7443/src/test/resources";
    assertEquals(expectedPodUrl, symConfigPodUrl7443.getPodUrl());
  }
  // End podUrl

  // keyAuthUrl
  @Test
  public void testGetKeyAuthUrl443(){
    final String expectedKeyAutUrl = "https://localhost/src/test/resources";
    assertEquals(expectedKeyAutUrl, symConfigAgentUrl443.getKeyAuthUrl());
  }

  @Test
  public void testGetKeyAuthUrl7443(){
    final String expectedKeyAutUrl = "https://localhost:7443/src/test/resources";
    assertEquals(expectedKeyAutUrl, symConfigAgentUrl7443.getKeyAuthUrl());
  }
  // End keyAuthUrl

  // SessionAuthUrl
  @Test
  public void testGetSessionAuthUrl443(){
    final String expectedSessionAuthUrl = "https://localhost/test1";
    assertEquals(expectedSessionAuthUrl, symConfigAgentUrl443.getSessionAuthUrl());
  }

  @Test
  public void testGetSessionAuthUrl7443(){
    final String expectedSessionAuthUrl = "https://localhost:7443/test1";
    assertEquals(expectedSessionAuthUrl, symConfigAgentUrl7443.getSessionAuthUrl());
  }
  // End SessionAuthUrl

  // getDatafeedIdFilePath
  @Test
  public void testGetDataFeedIdPath(){
    final String expectedDataFilePath =  "src/test/resources/testdatafeed.id" + File.separator;
    assertEquals(expectedDataFilePath, symConfigAgentUrl7443.getDatafeedIdFilePath());
  }

  @Test
  public void testGetDataFeedIdPathEmpty(){
    final String expectedDataFilePath = "." + File.separator;
    assertEquals(expectedDataFilePath, symConfigDatafeedIdFilepathEmpty.getDatafeedIdFilePath());
  }

  @Test
  public void testGetDataFeedIdPathNull(){
    final String expectedDataFilePath = "." + File.separator;
    assertEquals(expectedDataFilePath, symConfigDatafeedIdFilepathNull.getDatafeedIdFilePath());
  }

  @Test
  public void testGetDataFeedIdPathWithSeparator(){
    final String dataFilePath = symConfigDatafeedIdFilepathWithSeparator.getDatafeedIdFilePath();
    assertTrue(dataFilePath.endsWith(File.separator));
  }

  @Test
  public void testGetSessionAuthHost(){
    assertEquals("localhost", symConfigAgentUrl7443.getSessionAuthHost());
  }

  @Test
  public void testGetSessionAuthPort(){
    assertEquals(7443, symConfigAgentUrl7443.getSessionAuthPort());
  }

  @Test
  public void testGetSessionAuthContextPath(){
    assertEquals("test1", symConfigAgentUrl7443.getSessionAuthContextPath());
  }

  @Test
  public void testGetKeyAuthHost(){
    assertEquals("localhost", symConfigAgentUrl7443.getKeyAuthHost());
  }

  @Test
  public void testGetKeyAuthPort(){
    assertEquals(7443, symConfigAgentUrl7443.getKeyAuthPort());
  }

  @Test
  public void testGetKeyAuthContextPath(){
    assertEquals("src/test/resources/", symConfigAgentUrl7443.getKeyAuthContextPath());
  }

  @Test
  public void testGetKeyManagerProxyURL(){
    assertEquals("key.manager.proxy.url", symConfigAgentUrl7443.getKeyManagerProxyURL());
  }

  @Test
  public void testGetKeyManagerProxyUsername(){
    assertEquals("test_un", symConfigAgentUrl7443.getKeyManagerProxyUsername());
  }

  @Test
  public void testGetKeyManagerProxyPassword(){
    assertEquals("test_pw", symConfigAgentUrl7443.getKeyManagerProxyPassword());
  }

  @Test
  public void testGetPodHost(){
    assertEquals("localhost", symConfigAgentUrl7443.getPodHost());
  }

  @Test
  public void testGetPodPort(){
    assertEquals(7443, symConfigAgentUrl7443.getPodPort());
  }

  @Test
  public void testGetPodContextPath(){
    assertEquals("src/test/resources/", symConfigAgentUrl7443.getPodContextPath());
  }

  @Test
  public void testGetPodProxyURL(){
    assertEquals("pod.proxy.url", symConfigAgentUrl7443.getPodProxyURL());
  }

  @Test
  public void testGetPodProxyUsername(){
    assertEquals("pod_un", symConfigAgentUrl7443.getPodProxyUsername());
  }

  @Test
  public void testGetPodProxyPassword(){
    assertEquals("pod.proxy.pw", symConfigAgentUrl7443.getPodProxyPassword());
  }

  @Test
  public void testGetAgentHost(){
    assertEquals("localhost", symConfigAgentUrl7443.getAgentHost());
  }

  @Test
  public void testGetAgentPort(){
    assertEquals(7443, symConfigAgentUrl7443.getAgentPort());
  }

  @Test
  public void testGetAgentContextPath(){
    assertEquals("src", symConfigAgentUrl7443.getAgentContextPath());
  }

  @Test
  public void testGetAgentProxyURL(){
    assertEquals("agent.proxy.url", symConfigAgentUrl7443.getAgentProxyURL());
  }

  @Test
  public void testGetAgentProxyUsername(){
    assertEquals("agent_un", symConfigAgentUrl7443.getAgentProxyUsername());
  }

  @Test
  public void testGetAgentProxyPassword(){
    assertEquals("agent_pw", symConfigAgentUrl7443.getAgentProxyPassword());
  }

  @Test
  public void testGetBotUsername(){
    assertEquals("bot", symConfigAgentUrl7443.getBotUsername());
  }

  @Test
  public void testGetBotEmailAddress(){
    assertEquals("bot@symphony.com", symConfigAgentUrl7443.getBotEmailAddress());
  }

  @Test
  public void testGetBotPrivateKeyPath(){
    assertEquals("src/test/resources/", symConfigAgentUrl7443.getBotPrivateKeyPath());
  }

  @Test
  public void testGetBotPrivateKeyName(){
    assertEquals("private-key.pem", symConfigAgentUrl7443.getBotPrivateKeyName());
  }

  @Test
  public void testGetAppId(){
    assertEquals("testapp", symConfigAgentUrl7443.getAppId());
  }

  @Test
  public void testGetAppPrivateKeyPath(){
    assertEquals("src/test/resources/", symConfigAgentUrl7443.getAppPrivateKeyPath());
  }

  @Test
  public void testGetAppPrivateKeyName(){
    assertEquals("testprivatekey.pkcs8", symConfigAgentUrl7443.getAppPrivateKeyName());
  }

  @Test
  public void testGetAppCertPath(){
    assertEquals("src/test/resources/", symConfigAgentUrl7443.getAppCertPath());
  }

  @Test
  public void testGetAppCertName(){
    assertEquals("testcertificate.crt", symConfigAgentUrl7443.getAppCertName());
  }

  @Test
  public void testGetAppCertPassword(){
    assertEquals("etttty", symConfigAgentUrl7443.getAppCertPassword());
  }

  @Test
  public void testGetReadTimeout(){
    assertEquals(5000, symConfigAgentUrl7443.getReadTimeout());
  }

  @Test
  public void testGetConnectionTimeout(){
    assertEquals(1000, symConfigAgentUrl7443.getConnectionTimeout());
  }

  @Test
  public void testGetTruststorePath(){
    assertEquals("src/test/resources/testkeystore.jks", symConfigAgentUrl7443.getTruststorePath());
  }

  @Test
  public void testGetTruststorePassword(){
    assertEquals("123456", symConfigAgentUrl7443.getTruststorePassword());
  }

  @Test
  public void testGetDatafeedVersion(){
    assertEquals("2.0", symConfigAgentUrl7443.getDatafeedVersion());
  }

  @Test
  public void testGetDatafeedEventsThreadpoolSize(){
    assertEquals(4, symConfigAgentUrl7443.getDatafeedEventsThreadpoolSize());
  }

  @Test
  public void testGetDatafeedEventsErrorTimeout(){
    assertEquals(6, symConfigAgentUrl7443.getDatafeedEventsErrorTimeout());
  }

  @Test
  public void testGetReuseDatafeedID(){
    assertTrue(symConfigAgentUrl7443.getReuseDatafeedID());
  }

  @Test
  public void testGetDatafeedIdFilePath(){
    assertEquals("src/test/resources/testdatafeed.id"+ File.separator, symConfigAgentUrl7443.getDatafeedIdFilePath());
  }

  @Test
  public void testGetAuthenticationFilterUrlPattern(){
    assertEquals("auth", symConfigAgentUrl7443.getAuthenticationFilterUrlPattern());
  }

  @Test
  public void testIsShowFirehoseErrors(){
    assertFalse(symConfigAgentUrl7443.isShowFirehoseErrors());
  }

  @Test
  public void testGetSupportedUriSchemes(){
    final List<String> supportedUriSchemes = symConfigAgentUrl7443.getSupportedUriSchemes();
    assertNotNull(supportedUriSchemes);
    assertEquals(3, supportedUriSchemes.size());
    assertTrue(supportedUriSchemes.contains("http"));
    assertTrue(supportedUriSchemes.contains("https"));
    assertTrue(supportedUriSchemes.contains("ftp"));
  }

  @Test
  public void testGetRetry(){
    final RetryConfiguration retryConfiguration = symConfigAgentUrl7443.getRetry();
    assertNotNull(retryConfiguration);
    assertEquals(2, retryConfiguration.getMaxAttempts());
    assertEquals(200, retryConfiguration.getInitialIntervalMillis());
    assertEquals(1.5, retryConfiguration.getMultiplier(), 0);
  }
}
