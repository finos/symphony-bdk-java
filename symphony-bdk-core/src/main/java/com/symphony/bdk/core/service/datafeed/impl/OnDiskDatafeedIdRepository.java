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
    log.debug("Writing datafeed id {} to file: {}", datafeedId, this.getDatafeedIdFile().toString());
    String agentUrl = this.config.getAgent().getHost() + ":" + this.config.getAgent().getPort();
    try {
      FileUtils.writeStringToFile(this.getDatafeedIdFile(), datafeedId + "@" + agentUrl, StandardCharsets.UTF_8);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Optional<String> read() {
    log.debug("Reading datafeed id from file: {}", this.getDatafeedIdFile().toString());
    String datafeedId;
    try {
      File file = this.getDatafeedIdFile();
      Path datafeedIdPath = Paths.get(file.getPath());
      List<String> lines = Files.readAllLines(datafeedIdPath);
      if (lines.isEmpty() || !lines.get(0).contains("@")) {
        return Optional.empty();
      }
      String[] persistedDatafeed = lines.get(0).split("@");
      datafeedId = persistedDatafeed[0];
      log.info("Retrieved datafeed id from datafeed repository: {}", datafeedId);
      return Optional.of(datafeedId);
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
