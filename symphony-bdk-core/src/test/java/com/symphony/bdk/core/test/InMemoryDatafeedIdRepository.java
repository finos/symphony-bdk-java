package com.symphony.bdk.core.test;

import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;

import java.util.Optional;

public class InMemoryDatafeedIdRepository implements DatafeedIdRepository {

  private final String defaultAgentBasePath;
  private String datafeedId;
  private String agentBasePath;

  public InMemoryDatafeedIdRepository(String defaultAgentBasePath) {
    this.defaultAgentBasePath = defaultAgentBasePath;
    this.datafeedId = null;
    this.agentBasePath = null;
  }

  @Override
  public void write(String datafeedId) {
    write(datafeedId, this.defaultAgentBasePath);
  }

  @Override
  public void write(String datafeedId, String agentBasePath) {
    this.datafeedId = datafeedId;
    this.agentBasePath = agentBasePath;
  }

  @Override
  public Optional<String> read() {
    return Optional.ofNullable(this.datafeedId);
  }

  @Override
  public Optional<String> readAgentBasePath() {
    return Optional.ofNullable(this.agentBasePath);
  }
}
