package com.symphony.bdk.app.spring;

import com.symphony.bdk.app.spring.config.BdkExtAppControllerConfig;

import com.symphony.bdk.app.spring.config.BdkExtAppSecurityConfig;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

/**
 * Configuration entry-point the Symphony BDK Spring Boot ExtensionApp wrapper.
 */
@Import({
    BdkExtAppControllerConfig.class,
    BdkExtAppSecurityConfig.class
})
@EnableConfigurationProperties(SymphonyBdkAppProperties.class)
public class SymphonyBdkAppAutoConfiguration {
}
