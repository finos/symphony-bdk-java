package com.symphony.bdk.app.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.app.spring.config.BdkExtAppControllerConfig;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class SymphonyBdkAppAutoConfigurationTest {

  @Test
  void shouldLoadContextWithSuccess() {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.pod.scheme=http",
            "bdk.pod.host=localhost",

            "bdk.agent.scheme=http",
            "bdk.agent.host=localhost",

            "bdk.keyManager.scheme=http",
            "bdk.keyManager.host=localhost",

            "bdk.bot.username=tibot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem",

            "bdk.app.appId=test-app",
            "bdk.app.privateKeyPath=classpath:/privatekey.pem",
            "bdk.app.auth.enabled=true",
            "bdk.app.auth.jwtCookies.enabled=true",
            "bdk.app.auth.jwtCookies.maxAge=86400"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAppAutoConfiguration.class));

    contextRunner.run(context -> {

      // verify that main beans have been injected
      assertThat(context).hasSingleBean(SymphonyBdkAppAutoConfiguration.class);
      assertThat(context).hasSingleBean(BdkExtAppControllerConfig.class);
    });
  }
}
