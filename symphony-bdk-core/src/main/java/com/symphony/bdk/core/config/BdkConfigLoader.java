package com.symphony.bdk.core.config;

import com.github.wnameless.json.unflattener.JsonUnflattener;

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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@API(status = API.Status.STABLE)
public class BdkConfigLoader {

  private static final ObjectMapper JSON_MAPPER;
  private static final Set<String> BDK_PROPERTY_PREFIXES;
  private static final Set<String> BDK_PROPERTY_KEYS;

  static {
    JSON_MAPPER = new JsonMapper();
    JSON_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    BDK_PROPERTY_PREFIXES = Stream.of("pod.", "bot.", "keyManager.", "agent.", "sessionAuth.", "datafeed.", "retry.",
        "app.", "ssl.", "proxy.", "defaultHeaders.").collect(Collectors.toSet());
    BDK_PROPERTY_KEYS = Stream.of("scheme", "host", "port", "connectionTimeout", "readTimeout", "connectionPoolMax",
        "connectionPoolPerRoute").collect(Collectors.toSet());
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

  /**
   * Load {@link BdkConfig} from {@link Properties}
   *
   * @param properties {@link Properties} with BDK properties
   * @return Symphony Bot Configuration
   */
  public static BdkConfig loadFromProperties(Properties properties) throws BdkConfigException {
    final Map<String, String> propertyMap = (Map) properties;
    return loadFromPropertyMap(propertyMap);
  }

  /**
   * Load {@link BdkConfig} from a Map of properties
   *
   * @param properties Property map with BDK properties
   * @return Symphony Bot Configuration
   */
  public static BdkConfig loadFromPropertyMap(Map<String, String> properties) throws BdkConfigException {
    Map<String, String> bdkProperties = properties.entrySet().stream()
        .filter(entry -> isBDKProperty(entry.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    String unflattenedPropertyJson = JsonUnflattener.unflatten(bdkProperties);
    return BdkConfigLoader.loadFromInputStream(new ByteArrayInputStream(unflattenedPropertyJson.getBytes(
        StandardCharsets.UTF_8)));
  }

  private static boolean isBDKProperty(String propertyName) {
    return BDK_PROPERTY_PREFIXES.stream().anyMatch(propertyName::startsWith) ||
        BDK_PROPERTY_KEYS.stream().anyMatch(propertyName::equals);
  }
}
