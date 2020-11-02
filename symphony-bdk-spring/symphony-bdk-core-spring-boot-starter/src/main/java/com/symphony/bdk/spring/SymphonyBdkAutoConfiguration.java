package com.symphony.bdk.spring;

import com.symphony.bdk.spring.config.BdkActivityConfig;
import com.symphony.bdk.spring.config.BdkApiClientsConfig;
import com.symphony.bdk.spring.config.BdkCoreConfig;
import com.symphony.bdk.spring.config.BdkDatafeedConfig;
import com.symphony.bdk.spring.config.BdkServiceConfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Configuration entry-point the Symphony BDK Spring Boot wrapper.
 */
@Import({
    BdkCoreConfig.class,
    BdkApiClientsConfig.class,
    BdkDatafeedConfig.class,
    BdkServiceConfig.class,
    BdkActivityConfig.class
})
@EnableConfigurationProperties(SymphonyBdkCoreProperties.class)
public class SymphonyBdkAutoConfiguration {}
