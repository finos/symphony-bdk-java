package utils;

import configuration.SymConfig;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class HttpClientBuilderHelperTest {

  @Test
  public void getAgentClientConfig() {
    final String proxyURL = "http://proxy:1234";
    final String proxyUser = "user";
    final String proxyPassword = "pass";

    SymConfig config = new SymConfig();
    config.setProxyURL(proxyURL);
    config.setProxyUsername(proxyUser);
    config.setProxyPassword(proxyPassword);

    final Map<String, Object> properties = HttpClientBuilderHelper.getKMClientConfig(config).getProperties();

    assertEquals(proxyURL, properties.get(ClientProperties.PROXY_URI));
    assertEquals(proxyUser, properties.get(ClientProperties.PROXY_USERNAME));
    assertEquals(proxyPassword, properties.get(ClientProperties.PROXY_PASSWORD));
  }
}
