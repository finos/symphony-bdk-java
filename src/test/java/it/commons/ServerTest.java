package it.commons;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import org.junit.After;
import org.junit.Rule;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class ServerTest extends BaseTest {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(wireMockConfig()
      .httpsPort(config.getPodPort())
      .keystorePath(config.getTruststorePath())
      .keystorePassword(config.getTruststorePassword()));

  @After
  public void tearDown() {
    wireMockRule.stop();
  }
}
