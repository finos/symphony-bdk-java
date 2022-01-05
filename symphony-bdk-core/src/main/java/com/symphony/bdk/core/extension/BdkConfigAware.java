package com.symphony.bdk.core.extension;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.apiguardian.api.API;

/**
 * TODO javadoc
 */
@API(status = API.Status.EXPERIMENTAL)
public interface BdkConfigAware {

  void setConfiguration(BdkConfig config);
}
