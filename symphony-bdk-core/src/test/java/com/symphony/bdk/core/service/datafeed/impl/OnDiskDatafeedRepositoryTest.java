package com.symphony.bdk.core.service.datafeed.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.BdkConfigLoaderTest;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedRepository;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class OnDiskDatafeedRepositoryTest {

  private BdkConfig bdkConfig;
  private Path datafeedFile;

  @BeforeEach
  void setUp(@TempDir Path tempDir) throws BdkConfigException {
    this.bdkConfig = BdkConfigLoader.loadFromClasspath("/config/config.yaml");
    BdkDatafeedConfig datafeedConfig = this.bdkConfig.getDatafeed();
    datafeedConfig.setIdFilePath(tempDir.toString());
    this.bdkConfig.setDatafeed(datafeedConfig);
    this.datafeedFile = tempDir.resolve("datafeed.id");
  }

  @Test
  void writeDatafeedTest() throws IOException {
    DatafeedRepository repository = new OnDiskDatafeedRepository(bdkConfig);
    repository.write("test-id");
    String datafeedFileContent = Files.readAllLines(this.datafeedFile).get(0);
    assertEquals(datafeedFileContent, "test-id@devx1.symphony.com:443");
  }

  @Test
  void readDatafeedTest() throws IOException {
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/datafeed/datafeedId");
    Files.copy(inputStream, datafeedFile);
    DatafeedRepository repository = new OnDiskDatafeedRepository(bdkConfig);
    String datafeedId = repository.read();
    assertEquals(datafeedId, "8e7c8672-220");
  }

  @Test
  void readInvalidDatafeedFileTest() throws IOException {
    FileUtils.writeStringToFile(datafeedFile.toFile(), "invalid-datafeed-file", StandardCharsets.UTF_8);
    DatafeedRepository repository = new OnDiskDatafeedRepository(bdkConfig);
    String datafeedId = repository.read();
    assertNull(datafeedId);
  }

  @Test
  void readEmptyDatafeedFileTest() throws IOException {
    FileUtils.writeStringToFile(datafeedFile.toFile(), "", StandardCharsets.UTF_8);
    DatafeedRepository repository = new OnDiskDatafeedRepository(bdkConfig);
    String datafeedId = repository.read();
    assertNull(datafeedId);
  }

  @Test
  void readNotExistDatafeedFileTest() {
    DatafeedRepository repository = new OnDiskDatafeedRepository(bdkConfig);
    String datafeedId = repository.read();
    assertNull(datafeedId);
  }

}
