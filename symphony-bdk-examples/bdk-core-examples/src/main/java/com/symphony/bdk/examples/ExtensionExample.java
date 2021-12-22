package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.SymphonyBdkBuilder;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.AuthSessionAware;
import com.symphony.bdk.core.extension.BdkConfigAware;
import com.symphony.bdk.core.extension.HttpClientAware;
import com.symphony.bdk.http.api.ApiException;
import com.symphony.bdk.http.api.HttpClient;
import com.symphony.bdk.http.api.util.TypeReference;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtensionExample {
  public static class MyExtension implements BdkConfigAware, HttpClientAware, AuthSessionAware {
    private AuthSession authSession;
    private BdkConfig bdkConfig;
    private HttpClient client;

    @Override
    public void setAuthSession(AuthSession authSession) {
      this.authSession = authSession;
    }

    @Override
    public void setBdkConfig(BdkConfig bdkConfig) {
      this.bdkConfig = bdkConfig;
    }

    @Override
    public void setHttpClientBuilder(HttpClient.Builder builder) {
      this.client = builder.basePath("https://www.google.com").build();
    }

    void testAuthSession() {
      log.info("Session token: {}", authSession.getSessionToken());
      log.info("KM token: {}", authSession.getKeyManagerToken());
    }

    void testConfig() {
      log.info("Config: " + bdkConfig.getHost());
    }

    void testClient() throws ApiException {
      log.info(client.path("").get(new TypeReference<String>() {}));
    }
  }

  @SneakyThrows
  public static void main(String[] args) {
    final SymphonyBdk symphonyBdk = new SymphonyBdkBuilder()
        .config(loadFromSymphonyDir("config.yaml"))
        .extension(MyExtension.class)
        .build();
    final MyExtension extension = symphonyBdk.getExtension(MyExtension.class);
    extension.testAuthSession();
    extension.testConfig();
    extension.testClient();
  }
}
