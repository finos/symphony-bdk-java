package com.symphony.bdk.core;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.Obo;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.DatafeedVersion;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV1;
import com.symphony.bdk.core.service.datafeed.impl.DatafeedServiceV2;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.Optional;

/**
 * BDK entry point.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class SymphonyBdk {

  private final ApiClientFactory apiClientFactory;

  private final AuthSession botSession;
  private final OboAuthenticator oboAuthenticator;

  private final DatafeedService datafeedService;
  private final SessionService sessionService;

  private final ActivityRegistry activityRegistry;

  public SymphonyBdk(BdkConfig config) throws AuthInitializationException, AuthUnauthorizedException {

    this.apiClientFactory = new ApiClientFactory(config);

    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(config, this.apiClientFactory);

    this.botSession = authenticatorFactory.getBotAuthenticator().authenticateBot();
    this.oboAuthenticator = config.isOboConfigured() ? authenticatorFactory.getOboAuthenticator() : null;

    // setup the datafeed
    final DatafeedApi datafeedApi = new DatafeedApi(this.apiClientFactory.getAgentClient());
    if (DatafeedVersion.of(config.getDatafeed().getVersion()) == DatafeedVersion.V2) {
      this.datafeedService = new DatafeedServiceV2(datafeedApi, this.botSession, config);
    } else {
      this.datafeedService = new DatafeedServiceV1(datafeedApi, this.botSession, config);
    }

    this.sessionService = new SessionService(new SessionApi(this.apiClientFactory.getPodClient()));
    final UserV2 botSession = this.sessionService.getSession(this.botSession);

    // initialize activity registry
    this.activityRegistry = new ActivityRegistry(botSession.getDisplayName(), this.datafeedService::subscribe);
  }

  public ActivityRegistry activities() {
    return this.activityRegistry;
  }

  public MessageService messages() {
    return new MessageService(new MessagesApi(this.apiClientFactory.getAgentClient()), this.botSession);
  }

  public MessageService messages(Obo.Handle oboHandle) throws AuthUnauthorizedException {
    AuthSession oboSession;
    if (oboHandle.hasUsername()) {
      oboSession = this.getOboAuthenticator().authenticateByUsername(oboHandle.getUsername());
    } else {
      oboSession = this.getOboAuthenticator().authenticateByUserId(oboHandle.getUserId());
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
    return this.datafeedService;
  }

  protected OboAuthenticator getOboAuthenticator() {
    return Optional.ofNullable(this.oboAuthenticator)
        .orElseThrow(() -> new IllegalStateException("OBO is not configured."));
  }
}
