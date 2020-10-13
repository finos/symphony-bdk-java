package com.symphony.bdk.app.spring;

import com.symphony.bdk.app.spring.config.BdkExtAppConfig;

import com.symphony.bdk.app.spring.config.BdkExtAppControllerConfig;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@Import({
    BdkExtAppConfig.class,
    BdkExtAppControllerConfig.class
})
@EnableConfigurationProperties(SymphonyBdkCoreProperties.class)
public class SymphonyBdkAppAutoConfiguration {
}
