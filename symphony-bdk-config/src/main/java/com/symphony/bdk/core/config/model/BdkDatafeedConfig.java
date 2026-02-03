package com.symphony.bdk.core.config.model;

import static com.symphony.bdk.core.config.util.DeprecationLogger.logDeprecation;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.io.File;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkDatafeedConfig {

  private String version = "v2";
  private String idFilePath;
  private BdkRetryConfig retry = new BdkRetryConfig(BdkRetryConfig.INFINITE_MAX_ATTEMPTS);
  private boolean includeInvisible = false;
  private String tag;

  public void setVersion(String version) {
    if ("v1".equalsIgnoreCase(version)) {
      logDeprecation("The datafeed 1 service will be fully replaced by the datafeed 2 service in the future. "
          + "Please consider migrating over to datafeed 2. For more information on the timeline as well as on the "
          + "benefits of datafeed 2, please reach out to your Technical Account Manager or to our developer "
          + "documentation https://docs.developers.symphony.com/building-bots-on-symphony/datafeed)");
    }
    this.version = version;
  }

  public String getIdFilePath() {
    if (idFilePath == null || idFilePath.isEmpty()) {
      return "." + File.separator;
    }
    if (!idFilePath.endsWith(File.separator)) {
      return idFilePath + File.separator;
    }
    return idFilePath;
  }
}
