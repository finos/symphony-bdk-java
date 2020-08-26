package com.symphony.bdk.core.client;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientBuilder;
import com.symphony.bdk.core.api.invoker.ApiClientBuilderProvider;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.config.model.BdkBotConfig;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkSslConfig;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

/**
 * Factory responsible for creating {@link ApiClient} instances for each main Symphony's components
 * :
 * <ul>
 *   <li>Agent</li>
 *   <li>KeyManager</li>
 *   <li>Pod</li>
 * </ul>
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class ApiClientFactory {

  private final BdkConfig config;
  private final ApiClientBuilderProvider apiClientBuilderProvider;

  public ApiClientFactory(@Nonnull BdkConfig config) {
    this.config = config;
    this.apiClientBuilderProvider = findApiClientBuilderProvider();
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Login API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getLoginClient() throws AuthInitializationException {
    return buildClient(this.config.getPod().getBasePath() + "/login");
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Pod API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getPodClient() throws AuthInitializationException {
    return buildClient(this.config.getPod().getBasePath() + "/pod");
  }

  /**
   * Returns a fully initialized {@link ApiClient} for KeyManager API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getRelayClient() throws AuthInitializationException {
    return buildClient(this.config.getKeyManager().getBasePath() + "/relay");
  }

  /**
   * Returns a fully initialized {@link ApiClient} for Agent API.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getAgentClient() throws AuthInitializationException {
    return buildClient(this.config.getAgent().getBasePath() + "/agent");
  }

  /**
   * Returns a fully initialized {@link ApiClient} for the SessionAuth API. This only works with a
   * certificate configured.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getSessionAuthClient() throws AuthInitializationException {
    return buildClientWithCertificate(this.config.getSessionAuth().getBasePath() + "/sessionauth");
  }

  /**
   * Returns a fully initialized {@link ApiClient} for the KayAuth API. This only works with a
   * certificate configured.
   *
   * @return an new {@link ApiClient} instance.
   */
  public ApiClient getKeyAuthClient() throws AuthInitializationException {
    return buildClientWithCertificate(this.config.getKeyManager().getBasePath() + "/keyauth");
  }

  private ApiClient buildClient(String basePath) throws AuthInitializationException {
    return getApiClientBuilder(basePath).build();
  }

  private ApiClient buildClientWithCertificate(String basePath) throws AuthInitializationException {
    BdkBotConfig botConfig = this.config.getBot();

    if (!botConfig.isCertificateAuthenticationConfigured()) {
      throw new AuthInitializationException("For certificate authentication, " +
          "certificatePath and certificatePassword must be set", null);
    }

    byte[] certificateBytes = getBytesFromFile(botConfig.getCertificatePath());

    return getApiClientBuilder(basePath)
        .withKeyStore(certificateBytes, botConfig.getCertificatePassword())
        .build();
  }

  private ApiClientBuilder getApiClientBuilder(String basePath) throws AuthInitializationException {
    ApiClientBuilder apiClientBuilder = this.apiClientBuilderProvider
        .newInstance()
        .withBasePath(basePath);

    BdkSslConfig sslConfig = this.config.getSsl();

    if(isNotEmpty(sslConfig.getTrustStorePath())) {
      byte[] trustStoreBytes = getBytesFromFile(sslConfig.getTrustStorePath());
      apiClientBuilder.withTrustStore(trustStoreBytes, sslConfig.getTrustStorePassword());
    }

    return apiClientBuilder;
  }

  private byte[] getBytesFromFile(String filePath) throws AuthInitializationException {
    try {
      return Files.readAllBytes(new File(filePath).toPath());
    } catch (IOException e) {
      throw new AuthInitializationException("Could not read file " + filePath, e);
    }
  }

  /**
   * Load {@link ApiClient} implementation class using {@link ServiceLoader}.
   *
   * @return an {@link ApiClientBuilderProvider}.
   */
  @Generated
  // exclude from code coverage as it is very difficult to mock the ServiceLoader class (which is
  // final)
  private static ApiClientBuilderProvider findApiClientBuilderProvider() {

    final ServiceLoader<ApiClientBuilderProvider> apiClientServiceLoader =
        ServiceLoader.load(ApiClientBuilderProvider.class);

    final List<ApiClientBuilderProvider> apiClientProviders =
        StreamSupport.stream(apiClientServiceLoader.spliterator(), false)
            .collect(Collectors.toList());

    if (apiClientProviders.isEmpty()) {
      throw new IllegalStateException("No ApiClientProvider implementation found in classpath.");
    } else if (apiClientProviders.size() > 1) {
      log.warn("More than 1 ApiClientProvider implementation found in classpath, will use : {}",
          apiClientProviders.stream().findFirst().get());
    }

    return apiClientProviders.stream().findFirst().get();
  }
}
