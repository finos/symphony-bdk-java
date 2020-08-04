package com.symphony.bdk.core.config;

import com.symphony.bdk.core.auth.jwt.JwtHelper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apiguardian.api.API;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;

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

  public PrivateKey getAppPrivateKey() {
    try {
      return JwtHelper.parseRSAPrivateKey(loadPrivateKeyContent(this.getAppPrivateKeyPath()));
    } catch (GeneralSecurityException | IOException e) {
      throw new BdkConfigException("Cannot load app RSA private key", e);
    }
  }

  public PrivateKey getBotPrivateKey() {
    try {
      return JwtHelper.parseRSAPrivateKey(loadPrivateKeyContent(this.getPrivateKeyPath()));
    } catch (GeneralSecurityException | IOException e) {
      throw new BdkConfigException("Cannot load app RSA private key", e);
    }
  }

  private static String loadPrivateKeyContent(String path) throws IOException {
    return IOUtils.toString(new FileInputStream(path), StandardCharsets.UTF_8);
  }
}
