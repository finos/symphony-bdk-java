package com.symphony.bdk.spring;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

/**
 * Configuration Properties for the Symphony BDK: simple inheritance of the {@link BdkConfig} class.
 *
 * FIXME: this way to configure the BDK does not allow to have multiple bots running within the same application.
 */
@ConfigurationProperties(prefix = "bdk")
public class SymphonyBdkCoreProperties extends BdkConfig {

  @Value("#{'${bdk.datafeed.enabled:true}' == 'true' and '${bdk.datahose.enabled:false}' == 'true'}")
  private boolean areBothDatafeedAndDatahoseEnabled;

  @PostConstruct
  public void validate() {
    if (areBothDatafeedAndDatahoseEnabled) {
      throw new RuntimeException("Both datafeed and datahose are enabled. Please disable datafeed if you want to use datahose.");
    }
  }
}
