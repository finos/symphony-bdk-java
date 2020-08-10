package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.service.Obo;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.V4MessageService;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * BDK entry point.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class SymphonyBdk {

  private final ApiClientFactory apiClientFactory;

  private final AuthSession botSession;
  private final OboAuthenticator oboAuthenticator;

  public SymphonyBdk(BdkConfig config) throws AuthInitializationException {

    this.apiClientFactory = new ApiClientFactory(config);

    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(
        config,
        apiClientFactory.getLoginClient(),
        apiClientFactory.getRelayClient()
    );

    this.botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
    this.oboAuthenticator = authenticatorFactory.getOboAuthenticator();
  }

  public V4MessageService messages() {
    return new V4MessageService(this.apiClientFactory.getAgentClient(), this.botSession);
  }

  public V4MessageService messages(Obo.Handle oboHandle) {
    AuthSession oboSession;
    if (oboHandle.hasUsername()) {
      oboSession = this.oboAuthenticator.authenticateByUsername(oboHandle.getUsername());
    } else {
      oboSession = this.oboAuthenticator.authenticateByUserId(oboHandle.getUserId());
    }
    return new V4MessageService(this.apiClientFactory.getAgentClient(), oboSession);
  }
}
