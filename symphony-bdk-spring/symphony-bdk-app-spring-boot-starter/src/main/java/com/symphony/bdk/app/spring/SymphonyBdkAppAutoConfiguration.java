package com.symphony.bdk.app.spring;

import com.symphony.bdk.app.spring.config.BdkExtAppControllerConfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Configuration entry-point the Symphony BDK Spring Boot ExtensionApp wrapper.
 */
@Import(BdkExtAppControllerConfig.class)
@EnableConfigurationProperties(SymphonyBdkAppProperties.class)
public class SymphonyBdkAppAutoConfiguration {
}
