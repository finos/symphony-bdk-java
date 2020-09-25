package com.symphony.bdk.spring;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration Properties for the Symphony BDK: simple inheritance of the {@link BdkConfig} class.
 *
 * FIXME: this way to configure the BDK does not allow to have multiple bots running within the same application.
 */
@ConfigurationProperties(prefix = "bdk")
public class SymphonyBdkCoreProperties extends BdkConfig {
}
