package com.symphony.bdk.core.config.model;

import static org.apache.commons.lang3.ObjectUtils.isNotEmpty;

import com.symphony.bdk.core.client.exception.ApiClientInitializationException;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkCertificateConfig {

  private String path;
  private byte[] content;
  private String password;

  public BdkCertificateConfig() {
    // Needed for jax-rs mapper
  }

  public BdkCertificateConfig(String path, String password) {
    this(path, null, password);
  }

  public BdkCertificateConfig(String path, byte[] content, String password) {
    this.path = path;
    this.content = content;
    this.password = password;
  }

  /**
   * Check if the certificate authentication is configured or not
   *
   * @return true if the certificate authentication is configured
   */
  public boolean isConfigured() {
    return (isNotEmpty(path) || isNotEmpty(content)) && password != null;
  }

  /**
   * Check if the certificate configuration is valid.
   * If both of certificate path and content, the configuration is invalid.
   *
   * @return true if the certificate configuration is invalid.
   */
  public boolean isValid() {
    return !(isNotEmpty(path) && isNotEmpty(content));
  }

  /**
   * Returns either {@link #content} if not empty or the content of file located in {@link #path}.
   *
   * @return the certificate content as byte array
   */
  public byte[] getCertificateBytes() {
    if (isNotEmpty(content)) {
      return content;
    }
    return getBytesFromFile(path);
  }

  private byte[] getBytesFromFile(String filePath) {
    try {
      if (filePath.startsWith("classpath:")) {
        final URL resource = getClass().getResource(filePath.replace("classpath:", ""));
        return Files.readAllBytes(Paths.get(resource.toURI()));
      }
      return Files.readAllBytes(new File(filePath).toPath());
    } catch (IOException | URISyntaxException e) {
      throw new ApiClientInitializationException("Could not read file " + filePath, e);
    }
  }
}
