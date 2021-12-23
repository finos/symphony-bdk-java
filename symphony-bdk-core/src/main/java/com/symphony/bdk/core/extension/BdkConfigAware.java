package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public interface BdkConfigAware extends Extension {
  void setBdkConfig(BdkConfig bdkConfig);
}
