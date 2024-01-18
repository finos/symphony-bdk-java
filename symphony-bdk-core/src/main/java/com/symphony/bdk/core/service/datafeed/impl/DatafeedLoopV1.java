package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.client.loadbalancing.LoadBalancedApiClient;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkLoadBalancingConfig;
import com.symphony.bdk.core.retry.RetryWithRecovery;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;
import com.symphony.bdk.core.service.datafeed.exception.NestedRetryException;
import com.symphony.bdk.gen.api.DatafeedApi;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.List;
import java.util.Optional;

/**
 * A class for implementing the datafeed v1 loop service.
 * <p>
 * This service will be started by calling {@link DatafeedLoopV1#start()}
 * <p>
 * At the beginning, a datafeed will be created and the BDK bot will listen to this datafeed to receive the real-time
 * events. With datafeed service v1, we don't have the api endpoint to retrieve the datafeed id that a service account
 * is listening, so, the id of the created datafeed must be persisted in the bot side.
 * <p>
 * The BDK bot will listen to this datafeed to get all the received real-time events.
 * <p>
 * From the second time, the bot will firstly retrieve the datafeed that was persisted and try to read the real-time
 * events from this datafeed. If this datafeed is expired or faulty, the datafeed service will create the new one for
 * listening.
 * <p>
 * This service will be stopped by calling {@link DatafeedLoopV1#stop()}
 * <p>
 * If the datafeed service is stopped during a read datafeed call, it has to wait until the last read finish to be
 * really stopped
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class DatafeedLoopV1 extends AbstractDatafeedLoop {

  private final ApiClient apiClient;
  private final RetryWithRecoveryBuilder<?> retryWithRecoveryBuilder;
  private final DatafeedIdRepository datafeedRepository;
  private final RetryWithRecovery<Void> readDatafeed;
  private final RetryWithRecovery<String> createDatafeed;
  private String datafeedId;

  public DatafeedLoopV1(DatafeedApi datafeedApi, BotAuthSession authSession, BdkConfig config, UserV2 botInfo) {
    this(datafeedApi, authSession, config, botInfo, new OnDiskDatafeedIdRepository(config));
  }

  public DatafeedLoopV1(DatafeedApi datafeedApi, BotAuthSession authSession, BdkConfig config, UserV2 botInfo,
      DatafeedIdRepository repository) {
    super(datafeedApi, authSession, config, botInfo);

    this.apiClient = datafeedApi.getApiClient();
    this.retryWithRecoveryBuilder = new RetryWithRecoveryBuilder<>()
        .basePath(this.apiClient.getBasePath())
        .retryConfig(config.getDatafeedRetryConfig())
        .recoveryStrategy(Exception.class, this.apiClient::rotate)  // always rotate in case of any error
        .recoveryStrategy(ApiException::isUnauthorized, this::refresh);

    final BdkLoadBalancingConfig loadBalancing = config.getAgent().getLoadBalancing();
    if (loadBalancing != null && !loadBalancing.isStickiness()) {
      log.warn("DF used with agent load balancing configured with stickiness false. DF calls will still be sticky.");
    }

    this.datafeedRepository = repository;

    this.datafeedId = this.retrieveDatafeed().orElse(null);
    if (this.apiClient instanceof LoadBalancedApiClient) {
      this.datafeedRepository.readAgentBasePath()
          .ifPresent(s -> ((LoadBalancedApiClient) this.apiClient).setBasePath(s));
    }

    this.readDatafeed = RetryWithRecoveryBuilder.<Void>from(retryWithRecoveryBuilder)
        .name("Read Datafeed V1")
        .supplier(this::readAndHandleEvents)
        .recoveryStrategy(ApiException::isClientError, this::recreateDatafeed)
        .retryOnException(RetryWithRecoveryBuilder::isNetworkIssueOrMinorErrorOrClientError)
        .build();

    this.createDatafeed = RetryWithRecoveryBuilder.<String>from(retryWithRecoveryBuilder)
        .name("Create Datafeed V1")
        .supplier(this::createDatafeedAndPersist)
        .build();
  }

  @Override
  protected void runLoop() throws Throwable {
    this.datafeedId = this.datafeedId == null ? this.createDatafeed.execute() : this.datafeedId;
    log.info("Start reading events from datafeed {}", datafeedId);

    this.started.set(true);
    do {
      this.readDatafeed.execute();
    } while (this.started.get());

    log.info("Datafeed loop successfully stopped.");
  }

  private Void readAndHandleEvents() throws ApiException {
    List<V4Event> events = this.datafeedApi.v4DatafeedIdReadGet(
        datafeedId,
        authSession.getSessionToken(),
        authSession.getKeyManagerToken(),
        null
    );

    try {

      super.handleV4EventList(events);

    } catch (RequeueEventException e) {
      log.warn("EventException is not supported for DFv1, events will not get re-queued", e);
    }

    return null;
  }

  private void recreateDatafeed() {
    log.info("Recreate a new datafeed and try again");
    try {
      datafeedId = this.createDatafeed.execute();
    } catch (Throwable throwable) {
      throw new NestedRetryException("Recreation of datafeed failed", throwable);
    }
  }

  private String createDatafeedAndPersist() throws ApiException {
    String id = datafeedApi.v4DatafeedCreatePost(authSession.getSessionToken(), authSession.getKeyManagerToken()).getId();
    datafeedRepository.write(id, getBasePathWithoutTrailingAgent());
    log.debug("Datafeed: {} was created and persisted", id);
    return id;
  }

  private String getBasePathWithoutTrailingAgent() {
    final String basePath = apiClient.getBasePath();
    return basePath.substring(0, basePath.length() - "/agent".length());
  }

  protected Optional<String> retrieveDatafeed() {
    log.debug("Start retrieving datafeed id");
    return this.datafeedRepository.read();
  }
}
