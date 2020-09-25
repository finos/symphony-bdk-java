package com.symphony.bdk.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * This class allows to verify is the SpringContext has successfully been initialized and all expected beans loaded.
 */
class SymphonyBdkAutoConfigurationTest {

  @Test
  public void shouldLoadContextWithSuccess() {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.pod.scheme=http",
            "bdk.pod.host=localhost",

            "bdk.agent.scheme=http",
            "bdk.agent.host=localhost",

            "bdk.keyManager.scheme=http",
            "bdk.keyManager.host=localhost",

            "bdk.bot.username=tibot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {

      // verify that main beans have been injected
      assertThat(context).hasSingleBean(SymphonyBdkAutoConfiguration.class);
      assertThat(context).hasSingleBean(DatafeedAsyncLauncherService.class);

      // verify that beans for cert auth have not been injected
      assertThat(context).doesNotHaveBean("keyAuthApiClient");
      assertThat(context).doesNotHaveBean("sessionAuthApiClient");
    });
  }
}
