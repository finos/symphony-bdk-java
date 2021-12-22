package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.config.model.BdkConfig;

public interface BdkConfigAware extends Extension {
  void setBdkConfig(BdkConfig bdkConfig);
}
