package com.symphony.bdk.core;

import com.symphony.bdk.core.api.invoker.ApiClient;
import com.symphony.bdk.core.api.invoker.ApiClientProvider;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.obo.Obo;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfig;
import com.symphony.bdk.core.service.V4MessageService;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class SymphonyBdk {

  private final BdkConfig config;
  private final ApiClientFactory apiClientFactory;
  private final AuthenticatorFactory authenticatorFactory;

  private final AuthSession botSession;

  /**
   *
   * @param config
   */
  public SymphonyBdk(BdkConfig config) {
    this.config = config;

    this.apiClientFactory = new ApiClientFactory(this.config, findApiClientProvider());

    this.authenticatorFactory = new AuthenticatorFactory(
        this.config,
        apiClientFactory.getLoginClient(),
        apiClientFactory.getRelayClient()
    );

    this.botSession = this.authenticatorFactory.getBotAuthenticator().authenticateBot();
  }

  public V4MessageService messages() {
    return new V4MessageService(this.apiClientFactory.getAgentClient(), this.botSession);
  }

  public V4MessageService messages(Obo.Handle oboHandle) {
    AuthSession oboSession;
    if (oboHandle.hasUsername()) {
      oboSession = this.authenticatorFactory.getOboAuthenticator().authenticateByUsername(oboHandle.getUsername());
    } else {
      oboSession = this.authenticatorFactory.getOboAuthenticator().authenticateByUserId(oboHandle.getUserId());
    }
    return new V4MessageService(this.apiClientFactory.getAgentClient(), oboSession);
  }

  /**
   * Load {@link ApiClient} implementation class using {@link ServiceLoader}.
   *
   * @return an {@link ApiClientProvider}.
   */
  private static ApiClientProvider findApiClientProvider() {

    final ServiceLoader<ApiClientProvider> apiClientServiceLoader = ServiceLoader.load(ApiClientProvider.class);

    final List<ApiClientProvider> apiClientProviders = StreamSupport.stream(apiClientServiceLoader.spliterator(), false)
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
