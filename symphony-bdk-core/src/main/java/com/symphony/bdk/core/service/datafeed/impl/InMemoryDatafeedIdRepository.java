package com.symphony.bdk.core.service.datafeed.impl;

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

  private final String defaultAgentBasePath;
  private String datafeedId;
  private String agentBasePath;

  public InMemoryDatafeedIdRepository(String defaultAgentBasePath) {
    this.defaultAgentBasePath = defaultAgentBasePath;
    this.datafeedId = null;
    this.agentBasePath = null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void write(String datafeedId) {
    write(datafeedId, defaultAgentBasePath);
  }

  @Override
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
    return Optional.ofNullable(this.datafeedId);
  }

  @Override
  public Optional<String> readAgentBasePath() {
    return Optional.ofNullable(this.agentBasePath);
  }
}
