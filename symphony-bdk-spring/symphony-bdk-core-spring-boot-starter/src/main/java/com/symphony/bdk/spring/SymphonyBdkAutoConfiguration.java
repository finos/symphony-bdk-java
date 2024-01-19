package com.symphony.bdk.spring;

import com.symphony.bdk.spring.config.BdkAuthEnhancedServiceConfig;
import com.symphony.bdk.spring.config.BdkActivityConfig;
import com.symphony.bdk.spring.config.BdkApiClientsConfig;
import com.symphony.bdk.spring.config.BdkCommonFeedConfig;
import com.symphony.bdk.spring.config.BdkCoreConfig;
import com.symphony.bdk.spring.config.BdkDatafeedConfig;
import com.symphony.bdk.spring.config.BdkDatahoseConfig;
import com.symphony.bdk.spring.config.BdkExtensionConfig;
import com.symphony.bdk.spring.config.BdkOboServiceConfig;
import com.symphony.bdk.spring.config.BdkRetryConfig;
import com.symphony.bdk.spring.config.BdkServiceConfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Configuration entry-point the Symphony BDK Spring Boot wrapper.
 */
@Import({
    BdkCoreConfig.class,
    BdkRetryConfig.class,
    BdkApiClientsConfig.class,
    BdkCommonFeedConfig.class,
    BdkDatafeedConfig.class,
    BdkDatahoseConfig.class,
    BdkAuthEnhancedServiceConfig.class,
    BdkServiceConfig.class,
    BdkOboServiceConfig.class,
    BdkActivityConfig.class,
    BdkExtensionConfig.class
})
@EnableConfigurationProperties(SymphonyBdkCoreProperties.class)
public class SymphonyBdkAutoConfiguration {}
