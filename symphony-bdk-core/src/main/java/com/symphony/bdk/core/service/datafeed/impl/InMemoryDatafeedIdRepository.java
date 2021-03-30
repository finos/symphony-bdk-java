package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

/**
 * The implementation of {@link DatafeedIdRepository} interface for a
 * non-persistent in memory Datafeed.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class InMemoryDatafeedIdRepository implements DatafeedIdRepository {

  private final BdkConfig config;
  private String datafeedId = null;
  private String agentBasePath = null;

  public InMemoryDatafeedIdRepository(BdkConfig config) {
    this.config = config;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(String datafeedId) {
    write(datafeedId, this.config.getAgent().getBasePath());
  }

  public void write(String datafeedId, String agentBasePath) {
    log.debug("Writing datafeed id {} to memory.", datafeedId);
    this.datafeedId = datafeedId;
    this.agentBasePath = agentBasePath;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> read() {
    if (datafeedId == null) {
      return Optional.empty();
    } else {
      return Optional.of(datafeedId);
    }
  }

  public Optional<String> readAgentBasePath() {
    if (agentBasePath == null) {
      return Optional.empty();
    } else {
      return Optional.of(agentBasePath);
    }
  }
}
