package utils;

import configuration.SymConfig;
import configuration.SymConfigLoader;
import lombok.SneakyThrows;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.*;
import org.mockito.MockedStatic;

import javax.net.ssl.SSLContext;
import javax.ws.rs.client.ClientBuilder;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HttpClientBuilderHelperTest {
  private final String proxyURL = "http://proxy:1234";
  private final String proxyUser = "user";
  private final String proxyPassword = "pass";
  private MockedStatic<HttpClientBuilderHelper> mockedHttpClientBuilder;

  @SneakyThrows
  @Before
  public void init() {
    SSLContext sslContext = SSLContext.getDefault();
    mockedHttpClientBuilder = mockStatic(HttpClientBuilderHelper.class, CALLS_REAL_METHODS);
    mockedHttpClientBuilder.when(() -> {
      HttpClientBuilderHelper.createSSLContext(anyString(), anyString(), anyString(), anyString());
    }).thenReturn(sslContext);
  }

  @After
  public void close() {
    mockedHttpClientBuilder.close();
  }

  @Test
  public void getHttpClientBuilderWithTruststoreTest() {
    SymConfig symConfig = SymConfigLoader.loadFromFile("src/test/resources/sym-config.json");
    ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBuilderWithTruststore(symConfig);

    mockedHttpClientBuilder.verify(times(1), ()->{
      HttpClientBuilderHelper.createSSLContext(
          "src/test/resources/testkeystore.jks",
          "123456",
          null,
          null
      );
    });
  }

  @Test
  public void getHttpClientBotBuilderTest() {
    SymConfig symConfig = SymConfigLoader.loadFromFile("src/test/resources/sym-config.json");
    ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientBotBuilder(symConfig);

    mockedHttpClientBuilder.verify(times(1), ()->{
      HttpClientBuilderHelper.createSSLContext(
          "src/test/resources/testkeystore.jks",
          "123456",
          "src/test/resources/testkeystore.jks",
          "123456"
      );
    });
  }

  @Test
  public void getHttpClientAppBuilderTest() {
    SymConfig symConfig = SymConfigLoader.loadFromFile("src/test/resources/sym-config.json");
    ClientBuilder clientBuilder = HttpClientBuilderHelper.getHttpClientAppBuilder(symConfig);

    mockedHttpClientBuilder.verify(times(1), ()->{
      HttpClientBuilderHelper.createSSLContext(
          "src/test/resources/testkeystore.jks",
          "123456",
          "src/test/resources/testcertificate.crt",
          "etttty"
      );
    });
  }

  @Test
  public void getPodClientConfig() {
    SymConfig symConfig = this.createSymConfig();
    Map<String, Object> properties = HttpClientBuilderHelper.getPodClientConfig(symConfig).getProperties();

    assertEquals(this.proxyURL, properties.get(ClientProperties.PROXY_URI));
    assertEquals(this.proxyUser, properties.get(ClientProperties.PROXY_USERNAME));
    assertEquals(this.proxyPassword, properties.get(ClientProperties.PROXY_PASSWORD));
  }

  @Test
  public void getAgentClientConfigTest() {
    SymConfig symConfig = this.createSymConfig();
    Map<String, Object> properties = HttpClientBuilderHelper.getAgentClientConfig(symConfig).getProperties();

    assertEquals(this.proxyURL, properties.get(ClientProperties.PROXY_URI));
    assertEquals(this.proxyUser, properties.get(ClientProperties.PROXY_USERNAME));
    assertEquals(this.proxyPassword, properties.get(ClientProperties.PROXY_PASSWORD));
  }

  @Test
  public void getKMClientConfigTest() {
    SymConfig symConfig = this.createSymConfig();
    final Map<String, Object> properties = HttpClientBuilderHelper.getKMClientConfig(symConfig).getProperties();

    assertEquals(this.proxyURL, properties.get(ClientProperties.PROXY_URI));
    assertEquals(this.proxyUser, properties.get(ClientProperties.PROXY_USERNAME));
    assertEquals(this.proxyPassword, properties.get(ClientProperties.PROXY_PASSWORD));
  }

  private SymConfig createSymConfig() {
    SymConfig symConfig = new SymConfig();
    symConfig.setProxyURL(this.proxyURL);
    symConfig.setProxyUsername(this.proxyUser);
    symConfig.setProxyPassword(this.proxyPassword);

    return symConfig;
  }
}
