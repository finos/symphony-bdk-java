package it.commons;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.After;
import org.junit.Rule;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class ServerTest extends BaseTest {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(wireMockConfig()
      .httpsPort(config.getPodPort())
      .keystorePath(config.getTruststorePath())
      .keyManagerPassword(config.getTruststorePassword())
      .keystorePassword(config.getTruststorePassword()));

  @After
  public void tearDown() {
    wireMockRule.stop();
  }
}
