package com.symphony.bdk.core.service.datafeed.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.BdkConfigLoaderTest;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.config.model.BdkDatafeedConfig;
import com.symphony.bdk.core.service.datafeed.DatafeedIdRepository;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Test class for the {@link OnDiskDatafeedIdRepository}.
 */
public class OnDiskDatafeedIdRepositoryTest {

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
    DatafeedIdRepository repository = new OnDiskDatafeedIdRepository(bdkConfig);
    repository.write("test-id");
    String datafeedFileContent = Files.readAllLines(this.datafeedFile).get(0);
    assertEquals(datafeedFileContent, "test-id@devx1.symphony.com:443");
  }

  @Test
  void readDatafeedTest() throws IOException {
    InputStream inputStream = BdkConfigLoaderTest.class.getResourceAsStream("/datafeed/datafeedId");
    Files.copy(inputStream, datafeedFile);
    DatafeedIdRepository repository = new OnDiskDatafeedIdRepository(bdkConfig);
    Optional<String> datafeedId = repository.read();
    assertTrue(datafeedId.isPresent());
    assertEquals(datafeedId.get(), "8e7c8672-220");
  }

  @Test
  void readInvalidDatafeedFileTest() throws IOException {
    FileUtils.writeStringToFile(datafeedFile.toFile(), "invalid-datafeed-file", StandardCharsets.UTF_8);
    DatafeedIdRepository repository = new OnDiskDatafeedIdRepository(bdkConfig);
    Optional<String> datafeedId = repository.read();
    assertFalse(datafeedId.isPresent());
  }

  @Test
  void readEmptyDatafeedFileTest() throws IOException {
    FileUtils.writeStringToFile(datafeedFile.toFile(), "", StandardCharsets.UTF_8);
    DatafeedIdRepository repository = new OnDiskDatafeedIdRepository(bdkConfig);
    Optional<String> datafeedId = repository.read();
    assertFalse(datafeedId.isPresent());
  }

  @Test
  void readNotExistDatafeedFileTest() {
    DatafeedIdRepository repository = new OnDiskDatafeedIdRepository(bdkConfig);
    Optional<String> datafeedId = repository.read();
    assertFalse(datafeedId.isPresent());
  }

}
