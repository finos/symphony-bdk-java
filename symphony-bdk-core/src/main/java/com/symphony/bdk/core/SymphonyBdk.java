package com.symphony.bdk.core;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.client.exception.ApiClientInitializationException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.BotInfoService;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.Obo;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.gen.api.MessagesApi;

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
  private final BdkConfig config;

  public SymphonyBdk(BdkConfig config) throws AuthInitializationException, AuthUnauthorizedException {

    this.apiClientFactory = new ApiClientFactory(config);

    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(config, this.apiClientFactory);

    this.config = config;
    this.botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
    this.oboAuthenticator = authenticatorFactory.getOboAuthenticator();
  }

  public MessageService messages() {
    return new MessageService(new MessagesApi(this.apiClientFactory.getAgentClient()), this.botSession);
  }

  public MessageService messages(Obo.Handle oboHandle)
      throws AuthUnauthorizedException, ApiClientInitializationException {
    AuthSession oboSession;
    if (oboHandle.hasUsername()) {
      oboSession = this.oboAuthenticator.authenticateByUsername(oboHandle.getUsername());
    } else {
      oboSession = this.oboAuthenticator.authenticateByUserId(oboHandle.getUserId());
    }
    return new MessageService(new MessagesApi(this.apiClientFactory.getAgentClient()), oboSession);
  }

  /**
   * Get the {@link DatafeedService} from a Bdk entry point.
   * The returned datafeed service instance depends on the configuration of datafeed version.
   *
   * @return {@link DatafeedService} datafeed service instance.
   */
  public DatafeedService datafeed() {
    if (DatafeedVersion.of(this.config.getDatafeed().getVersion()) == DatafeedVersion.V2) {
      return new DatafeedServiceV2(this.apiClientFactory.getAgentClient(), this.botSession, this.config);
    }
    return new DatafeedServiceV1(this.apiClientFactory.getAgentClient(), this.botSession, this.config);
  }

}
