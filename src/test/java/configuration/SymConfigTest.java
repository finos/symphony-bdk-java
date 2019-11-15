package configuration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import configuration.SymConfig;
import org.junit.Test;

public class SymConfigTest {
  @Test
  public void SymConfigTest() throws Exception {
    // Arrange and Act
    SymConfig symConfig = new SymConfig();

    // Assert
    assertEquals(null, symConfig.getAppCertName());
  }

  @Test
  public void getAgentHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAgentHost();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAgentPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getAgentPort();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getAgentUrlTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAgentUrl();

    // Assert
    assertEquals("https://null:0", actual);
  }

  @Test
  public void getAppCertNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAppCertName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppCertPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAppCertPassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppCertPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAppCertPath();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppIdTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAppId();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppPrivateKeyNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAppPrivateKeyName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAppPrivateKeyPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAppPrivateKeyPath();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getAuthTokenRefreshPeriodTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getAuthTokenRefreshPeriod();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getAuthenticationFilterUrlPatternTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getAuthenticationFilterUrlPattern();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotCertNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotCertName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotCertPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotCertPassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotCertPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotCertPath();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotEmailAddressTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotEmailAddress();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotPrivateKeyNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotPrivateKeyName();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotPrivateKeyPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotPrivateKeyPath();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getBotUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getBotUsername();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getConnectionTimeoutTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getConnectionTimeout();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getDatafeedEventsErrorTimeoutTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getDatafeedEventsErrorTimeout();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getDatafeedEventsThreadpoolSizeTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getDatafeedEventsThreadpoolSize();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getKeyAuthHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getKeyAuthHost();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeyAuthPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getKeyAuthPort();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getKeyAuthUrlTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getKeyAuthUrl();

    // Assert
    assertEquals("https://null:0", actual);
  }

  @Test
  public void getKeyManagerProxyPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getKeyManagerProxyPassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getKeyManagerProxyURLTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getKeyManagerProxyURL();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getKeyManagerProxyUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getKeyManagerProxyUsername();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPodHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getPodHost();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getPodPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getPodPort();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getPodProxyPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getPodProxyPassword();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getPodProxyURLTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getPodProxyURL();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getPodProxyUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getPodProxyUsername();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getPodUrlTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getPodUrl();

    // Assert
    assertEquals("https://null:0", actual);
  }

  @Test
  public void getProxyPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getProxyPassword();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getProxyURLTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getProxyURL();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getProxyUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getProxyUsername();

    // Assert
    assertEquals("", actual);
  }

  @Test
  public void getSessionAuthHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getSessionAuthHost();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getSessionAuthPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    int actual = symConfig.getSessionAuthPort();

    // Assert
    assertEquals(0, actual);
  }

  @Test
  public void getSessionAuthUrlTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getSessionAuthUrl();

    // Assert
    assertEquals("https://null:0", actual);
  }

  @Test
  public void getShowFirehoseErrorsTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    boolean actual = symConfig.getShowFirehoseErrors();

    // Assert
    assertFalse(actual);
  }

  @Test
  public void getTruststorePasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getTruststorePassword();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void getTruststorePathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();

    // Act
    String actual = symConfig.getTruststorePath();

    // Assert
    assertEquals(null, actual);
  }

  @Test
  public void setAgentHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String agentHost = "aaaaa";

    // Act
    symConfig.setAgentHost(agentHost);

    // Assert
    assertEquals("aaaaa", symConfig.getAgentHost());
  }

  @Test
  public void setAgentPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int agentPort = 1;

    // Act
    symConfig.setAgentPort(agentPort);

    // Assert
    assertEquals(1, symConfig.getAgentPort());
  }

  @Test
  public void setAppCertNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String appCertName = "aaaaa";

    // Act
    symConfig.setAppCertName(appCertName);

    // Assert
    assertEquals("aaaaa", symConfig.getAppCertName());
  }

  @Test
  public void setAppCertPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String appCertPassword = "aaaaa";

    // Act
    symConfig.setAppCertPassword(appCertPassword);

    // Assert
    assertEquals("aaaaa", symConfig.getAppCertPassword());
  }

  @Test
  public void setAppCertPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String appCertPath = "aaaaa";

    // Act
    symConfig.setAppCertPath(appCertPath);

    // Assert
    assertEquals("aaaaa", symConfig.getAppCertPath());
  }

  @Test
  public void setAppIdTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String appId = "aaaaa";

    // Act
    symConfig.setAppId(appId);

    // Assert
    assertEquals("aaaaa", symConfig.getAppId());
  }

  @Test
  public void setAppPrivateKeyNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String appPrivateKeyName = "aaaaa";

    // Act
    symConfig.setAppPrivateKeyName(appPrivateKeyName);

    // Assert
    assertEquals("aaaaa", symConfig.getAppPrivateKeyName());
  }

  @Test
  public void setAppPrivateKeyPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String appPrivateKeyPath = "aaaaa";

    // Act
    symConfig.setAppPrivateKeyPath(appPrivateKeyPath);

    // Assert
    assertEquals("aaaaa", symConfig.getAppPrivateKeyPath());
  }

  @Test
  public void setAuthTokenRefreshPeriodTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int authTokenRefreshPeriod = 1;

    // Act
    symConfig.setAuthTokenRefreshPeriod(authTokenRefreshPeriod);

    // Assert
    assertEquals(1, symConfig.getAuthTokenRefreshPeriod());
  }

  @Test
  public void setAuthenticationFilterUrlPatternTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String authenticationFilterUrlPattern = "aaaaa";

    // Act
    symConfig.setAuthenticationFilterUrlPattern(authenticationFilterUrlPattern);

    // Assert
    assertEquals("aaaaa", symConfig.getAuthenticationFilterUrlPattern());
  }

  @Test
  public void setBotCertNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botCertName = "aaaaa";

    // Act
    symConfig.setBotCertName(botCertName);

    // Assert
    assertEquals("aaaaa", symConfig.getBotCertName());
  }

  @Test
  public void setBotCertPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botCertPassword = "aaaaa";

    // Act
    symConfig.setBotCertPassword(botCertPassword);

    // Assert
    assertEquals("aaaaa", symConfig.getBotCertPassword());
  }

  @Test
  public void setBotCertPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botCertPath = "aaaaa";

    // Act
    symConfig.setBotCertPath(botCertPath);

    // Assert
    assertEquals("aaaaa", symConfig.getBotCertPath());
  }

  @Test
  public void setBotEmailAddressTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botEmailAddress = "aaaaa";

    // Act
    symConfig.setBotEmailAddress(botEmailAddress);

    // Assert
    assertEquals("aaaaa", symConfig.getBotEmailAddress());
  }

  @Test
  public void setBotPrivateKeyNameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botPrivateKeyName = "aaaaa";

    // Act
    symConfig.setBotPrivateKeyName(botPrivateKeyName);

    // Assert
    assertEquals("aaaaa", symConfig.getBotPrivateKeyName());
  }

  @Test
  public void setBotPrivateKeyPathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botPrivateKeyPath = "aaaaa";

    // Act
    symConfig.setBotPrivateKeyPath(botPrivateKeyPath);

    // Assert
    assertEquals("aaaaa", symConfig.getBotPrivateKeyPath());
  }

  @Test
  public void setBotUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String botUsername = "aaaaa";

    // Act
    symConfig.setBotUsername(botUsername);

    // Assert
    assertEquals("aaaaa", symConfig.getBotUsername());
  }

  @Test
  public void setConnectionTimeoutTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int connectionTimeout = 1;

    // Act
    symConfig.setConnectionTimeout(connectionTimeout);

    // Assert
    assertEquals(1, symConfig.getConnectionTimeout());
  }

  @Test
  public void setDatafeedEventsErrorTimeoutTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int datafeedEventsErrorTimeout = 1;

    // Act
    symConfig.setDatafeedEventsErrorTimeout(datafeedEventsErrorTimeout);

    // Assert
    assertEquals(1, symConfig.getDatafeedEventsErrorTimeout());
  }

  @Test
  public void setDatafeedEventsThreadpoolSizeTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int datafeedEventsThreadpoolSize = 1;

    // Act
    symConfig.setDatafeedEventsThreadpoolSize(datafeedEventsThreadpoolSize);

    // Assert
    assertEquals(1, symConfig.getDatafeedEventsThreadpoolSize());
  }

  @Test
  public void setKeyAuthHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String keyAuthHost = "aaaaa";

    // Act
    symConfig.setKeyAuthHost(keyAuthHost);

    // Assert
    assertEquals("aaaaa", symConfig.getKeyAuthHost());
  }

  @Test
  public void setKeyAuthPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int keyAuthPort = 1;

    // Act
    symConfig.setKeyAuthPort(keyAuthPort);

    // Assert
    assertEquals(1, symConfig.getKeyAuthPort());
  }

  @Test
  public void setKeyManagerProxyPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String keyManagerProxyPassword = "aaaaa";

    // Act
    symConfig.setKeyManagerProxyPassword(keyManagerProxyPassword);

    // Assert
    assertEquals("aaaaa", symConfig.getKeyManagerProxyPassword());
  }

  @Test
  public void setKeyManagerProxyURLTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String keyManagerProxyURL = "aaaaa";

    // Act
    symConfig.setKeyManagerProxyURL(keyManagerProxyURL);

    // Assert
    assertEquals("aaaaa", symConfig.getKeyManagerProxyURL());
  }

  @Test
  public void setKeyManagerProxyUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String keyManagerProxyUsername = "aaaaa";

    // Act
    symConfig.setKeyManagerProxyUsername(keyManagerProxyUsername);

    // Assert
    assertEquals("aaaaa", symConfig.getKeyManagerProxyUsername());
  }

  @Test
  public void setPodHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String podHost = "aaaaa";

    // Act
    symConfig.setPodHost(podHost);

    // Assert
    assertEquals("aaaaa", symConfig.getPodHost());
  }

  @Test
  public void setPodPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int podPort = 1;

    // Act
    symConfig.setPodPort(podPort);

    // Assert
    assertEquals(1, symConfig.getPodPort());
  }

  @Test
  public void setPodProxyPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String podProxyPassword = "aaaaa";

    // Act
    symConfig.setPodProxyPassword(podProxyPassword);

    // Assert
    assertEquals("aaaaa", symConfig.getPodProxyPassword());
  }

  @Test
  public void setPodProxyURLTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String podProxyURL = "aaaaa";

    // Act
    symConfig.setPodProxyURL(podProxyURL);

    // Assert
    assertEquals("aaaaa", symConfig.getPodProxyURL());
  }

  @Test
  public void setPodProxyUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String podProxyUsername = "aaaaa";

    // Act
    symConfig.setPodProxyUsername(podProxyUsername);

    // Assert
    assertEquals("aaaaa", symConfig.getPodProxyUsername());
  }

  @Test
  public void setProxyPasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String proxyPassword = "aaaaa";

    // Act
    symConfig.setProxyPassword(proxyPassword);

    // Assert
    assertEquals("aaaaa", symConfig.getProxyPassword());
  }

  @Test
  public void setProxyURLTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String proxyURL = "aaaaa";

    // Act
    symConfig.setProxyURL(proxyURL);

    // Assert
    assertEquals("aaaaa", symConfig.getProxyURL());
  }

  @Test
  public void setProxyUsernameTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String proxyUsername = "aaaaa";

    // Act
    symConfig.setProxyUsername(proxyUsername);

    // Assert
    assertEquals("aaaaa", symConfig.getProxyUsername());
  }

  @Test
  public void setSessionAuthHostTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String sessionAuthHost = "aaaaa";

    // Act
    symConfig.setSessionAuthHost(sessionAuthHost);

    // Assert
    assertEquals("aaaaa", symConfig.getSessionAuthHost());
  }

  @Test
  public void setSessionAuthPortTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    int sessionAuthPort = 1;

    // Act
    symConfig.setSessionAuthPort(sessionAuthPort);

    // Assert
    assertEquals(1, symConfig.getSessionAuthPort());
  }

  @Test
  public void setShowFirehoseErrorsTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    boolean showFirehoseErrors = true;

    // Act
    symConfig.setShowFirehoseErrors(showFirehoseErrors);

    // Assert
    assertTrue(symConfig.getShowFirehoseErrors());
  }

  @Test
  public void setTruststorePasswordTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String truststorePassword = "aaaaa";

    // Act
    symConfig.setTruststorePassword(truststorePassword);

    // Assert
    assertEquals("aaaaa", symConfig.getTruststorePassword());
  }

  @Test
  public void setTruststorePathTest() throws Exception {
    // Arrange
    SymConfig symConfig = new SymConfig();
    String truststorePath = "aaaaa";

    // Act
    symConfig.setTruststorePath(truststorePath);

    // Assert
    assertEquals("aaaaa", symConfig.getTruststorePath());
  }
}
