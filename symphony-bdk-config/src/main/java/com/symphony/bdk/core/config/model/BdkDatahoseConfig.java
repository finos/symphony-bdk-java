package com.symphony.bdk.core.config.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.List;

@Getter
@Setter
@API(status = API.Status.STABLE)
public class BdkDatahoseConfig {

  private BdkRetryConfig retry = new BdkRetryConfig(BdkRetryConfig.INFINITE_MAX_ATTEMPTS);
  private String tag = "";
  private List<String> eventTypes = null;
}
