package com.symphony.bdk.core.config;

import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.config.legacy.LegacyConfigMapper;
import com.symphony.bdk.core.config.legacy.model.LegacySymConfig;
import com.symphony.bdk.core.config.model.BdkConfig;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@API(status = API.Status.STABLE)
public class BdkConfigLoader {

  private static final ObjectMapper JSON_MAPPER;

  static {
    JSON_MAPPER = new JsonMapper();
    JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  /**
   * Load {@link BdkConfig} from a file path
   *
   * @param configPath Path of the config file
   * @return Symphony Bot Configuration
   */
  public static BdkConfig loadFromFile(String configPath) throws BdkConfigException {
    try {
      final InputStream inputStream = new FileInputStream(configPath);
      return loadFromInputStream(inputStream);
    } catch (FileNotFoundException e) {
      throw new BdkConfigException("Config file has not been found from location: " + configPath, e);
    }
  }

  /**
   * Load {@link BdkConfig} from an InputStream
   *
   * @param inputStream InputStream
   * @return Symphony Bot Configuration
   */
  public static BdkConfig loadFromInputStream(InputStream inputStream) throws BdkConfigException {
    BdkConfigParser parser = new BdkConfigParser();
    return parseConfig(parser.parse(inputStream));
  }

  private static BdkConfig parseConfig(JsonNode jsonNode) {
    if (jsonNode.at("/botUsername").isMissingNode()) {
      return JSON_MAPPER.convertValue(jsonNode, BdkConfig.class);
    } else {
      LegacySymConfig legacySymConfig = JSON_MAPPER.convertValue(jsonNode, LegacySymConfig.class);
      return LegacyConfigMapper.map(legacySymConfig);
    }
  }

  /**
   * Load {@link BdkConfig} from a relative path located in the .symphony directory.
   *
   * <p>
   * Note: The .symphony directory is located under your home directory (<code>System.getProperty("user.home")</code>).
   * Convention adopted in order to avoid storing sensitive information (such as usernames, private keys...)
   * within the code base.
   * </p>
   *
   * @param relPath Configuration file relative path from the ${user.home}/.symphony directory
   * @return Symphony Bot Configuration
   */
  @Generated // skip code coverage, CircleCI does not allow to create file in the home directory
  public static BdkConfig loadFromSymphonyDir(String relPath) throws BdkConfigException {
    final String home = System.getProperty("user.home");
    final Path symphonyDirPath = Paths.get(home, ".symphony");
    final Path configPath = symphonyDirPath.resolve(relPath);
    log.debug("Loading configuration from the Symphony directory : {}", configPath);
    try {
      return loadFromInputStream(new FileInputStream(configPath.toFile()));
    } catch (FileNotFoundException e) {
      throw new BdkConfigException("Unable to load configuration from the .symphony directory with relative location: "
          + relPath, e);
    }
  }

  /**
   * Load {@link BdkConfig} from a classpath
   *
   * @param configPath Classpath to config file
   * @return Symphony Bot Configuration
   */
  public static BdkConfig loadFromClasspath(String configPath) throws BdkConfigException {
    InputStream inputStream = BdkConfigLoader.class.getResourceAsStream(configPath);
    if (inputStream != null) {
      return loadFromInputStream(inputStream);
    }
    throw new BdkConfigException("Config file has not found from classpath location: " + configPath);
  }

}
