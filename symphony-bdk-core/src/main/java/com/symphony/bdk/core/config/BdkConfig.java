package com.symphony.bdk.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apiguardian.api.API;

/**
 * /!\ Temporary class, complete Config API will be done through https://perzoinc.atlassian.net/browse/APP-2884 /!\
 */
@Getter
@Setter
@API(status = API.Status.DEPRECATED)
@Deprecated
public class BdkConfig {

  private String podUrl;
  private String agentUrl;

  private String username;
  private String privateKeyPath;

  private String appId;
  private String appPrivateKeyPath;

  @SneakyThrows
  public static BdkConfig load(String path) {
    final ObjectMapper objectMapper = new YAMLMapper();
    return objectMapper.readValue(BdkConfig.class.getResourceAsStream(path), BdkConfig.class);
  }
}
