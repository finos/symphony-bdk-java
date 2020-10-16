package com.symphony.bdk.app.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.app.spring.auth.CircleOfTrustController;
import com.symphony.bdk.app.spring.config.BdkExtAppControllerConfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class SymphonyBdkAppAutoConfigurationTest {

  @Test
  void shouldLoadContextWithSuccess() {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.app.appId=test-app",
            "bdk.app.privateKeyPath=classpath:/privatekey.pem",
            "bdk-app.auth.enabled=true",
            "bdk-app.auth.jwtCookies.enabled=true",
            "bdk-app.auth.jwtCookies.maxAge=1d"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAppAutoConfiguration.class));

    contextRunner.run(context -> {

      // verify that main beans have been injected
      assertThat(context).hasSingleBean(SymphonyBdkAppAutoConfiguration.class);
      assertThat(context).hasSingleBean(BdkExtAppControllerConfig.class);
      assertThat(context).hasSingleBean(CircleOfTrustController.class);
    });
  }

  @Test
  void shouldLoadContextWithoutCircleOfTrust() {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.app.appId=test-app",
            "bdk.app.privateKeyPath=classpath:/privatekey.pem",
            "bdk-app.auth.enabled=false"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAppAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).doesNotHaveBean(CircleOfTrustController.class);
    });
  }
}
