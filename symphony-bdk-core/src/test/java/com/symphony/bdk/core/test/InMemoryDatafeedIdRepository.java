package com.symphony.bdk.core.test;

import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;

import java.util.Optional;

public class InMemoryDatafeedIdRepository implements DatafeedIdRepository {

  private String defaultAgentBasePath;
  private Optional<String> datafeedId;
  private Optional<String> agentBasePath;

  public InMemoryDatafeedIdRepository(String defaultAgentBasePath) {
    this.defaultAgentBasePath = defaultAgentBasePath;
    this.datafeedId = Optional.empty();
    this.agentBasePath = Optional.empty();
  }

  @Override
  public void write(String datafeedId) {
    write(datafeedId, this.defaultAgentBasePath);
  }

  @Override
  public void write(String datafeedId, String agentBasePath) {
    this.datafeedId = Optional.of(datafeedId);
    this.agentBasePath = Optional.of(agentBasePath);
  }

  @Override
  public Optional<String> read() {
    return this.datafeedId;
  }

  @Override
  public Optional<String> readAgentBasePath() {
    return this.agentBasePath;
  }
}
