package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apiguardian.api.API;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * The implementation of {@link DatafeedIdRepository} interface for persisting a datafeed id on disk.
 */
@Slf4j
@API(status = API.Status.INTERNAL)
public class OnDiskDatafeedIdRepository implements DatafeedIdRepository {

  private static final String DATAFEED_ID_FILE = "datafeed.id";

  private final BdkConfig config;

  public OnDiskDatafeedIdRepository(BdkConfig config) {
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
    log.debug("Writing datafeed id {} to file: {}", datafeedId, this.getDatafeedIdFile().toString());
    try {
      FileUtils.writeStringToFile(this.getDatafeedIdFile(), datafeedId + "@" + agentBasePath, StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error("Error occurred when writing datafeed id", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> read() {
    final Optional<String> s = readDatafeedInformation();
    if (s.isPresent()) {
      final String datafeedId = s.get().split("@")[0];
      log.info("Retrieved datafeed id from datafeed repository: {}", datafeedId);
      return Optional.of(datafeedId);
    }
    return Optional.empty();
  }

  public Optional<String> readAgentBasePath() {
    final Optional<String> s = readDatafeedInformation();
    if (s.isPresent()) {
      final String agentBasePath = s.get().split("@")[1];
      log.info("Retrieved agent base path from datafeed repository: {}", agentBasePath);
      return Optional.of(agentBasePath);
    }
    return Optional.empty();
  }

  private Optional<String> readDatafeedInformation() {
    log.debug("Reading stored datafeed information from file: {}", this.getDatafeedIdFile().toString());
    try {
      Path datafeedIdPath = Paths.get(this.getDatafeedIdFile().getPath());
      List<String> lines = Files.readAllLines(datafeedIdPath);
      if (lines.isEmpty() || !lines.get(0).contains("@")) {
        return Optional.empty();
      }
      return Optional.of(lines.get(0));
    } catch (IOException e) {
      log.debug("No persisted datafeed id could be retrieved from disk");
      return Optional.empty();
    }
  }

  private File getDatafeedIdFile() {
    File file = new File(this.config.getDatafeed().getIdFilePath());
    if (file.isDirectory()) {
      file = new File(this.config.getDatafeed().getIdFilePath() + File.separator + DATAFEED_ID_FILE);
    }
    return file;
  }
}
