package com.symphony.bdk.spring;

import com.symphony.bdk.core.config.model.BdkConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * TODO: add description here
 */
@ConfigurationProperties(prefix = "bdk")
public class SymphonyBdkCoreProperties extends BdkConfig {
}
