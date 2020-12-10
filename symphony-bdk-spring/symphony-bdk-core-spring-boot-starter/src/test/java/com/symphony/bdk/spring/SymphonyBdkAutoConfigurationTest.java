package com.symphony.bdk.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.client.loadbalancing.DatafeedLoadBalancedApiClient;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.spring.annotation.SlashAnnotationProcessor;
import com.symphony.bdk.spring.service.DatafeedAsyncLauncherService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

/**
 * This class allows to verify is the SpringContext has successfully been initialized and all expected beans loaded.
 */
class SymphonyBdkAutoConfigurationTest {

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

      //verify that bean for OBO authentication has not been injected
      assertThat(context).doesNotHaveBean("oboAuthenticator");
    });
  }

  @Test
  void shouldInitializeOboAuthenticatorIfAppIdSet() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.scheme=http",
            "bdk.host=localhost",
            "bdk.context=context",

            "bdk.bot.username=testbot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem",

            "bdk.app.appId=testapp",
            "bdk.app.privateKeyPath=classpath:/privatekey.pem"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasBean("oboAuthenticator");
      assertThat(context).hasSingleBean(OboAuthenticator.class);
    });
  }

  @Test
  void shouldFailOnOboAuthenticatorInitializationIfNotProperlyConfigured() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.scheme=http",
            "bdk.host=localhost",
            "bdk.context=context",

            "bdk.bot.username=testbot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem",

            "bdk.app.appId=testapp"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasFailed();
      assertThat(context).getFailure().hasRootCauseInstanceOf(AuthInitializationException.class);
    });
  }

  @Test
  void shouldLoadParentConfigurationIfSet() {

    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.scheme=http",
            "bdk.host=localhost",
            "bdk.context=context",

            "bdk.bot.username=testbot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      final SymphonyBdkCoreProperties config = context.getBean(SymphonyBdkCoreProperties.class);

      assertThat(config.getAgent().getBasePath()).isEqualTo("http://localhost:443/context");
    });
  }

  @Test
  void shouldInstantiateLoadBalancedApiClientIfConfigured() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.scheme=http",
            "bdk.host=localhost",
            "bdk.context=context",

            "bdk.bot.username=testbot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem",

            "bdk.agent.loadBalancing.mode=roundRobin",
            "bdk.agent.loadBalancing.nodes[0].host=agent-lb"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      final SymphonyBdkCoreProperties config = context.getBean(SymphonyBdkCoreProperties.class);
      assertThat(context).hasBean("datafeedAgentApiClient");
      assertThat(context).getBean("datafeedAgentApiClient").isExactlyInstanceOf(DatafeedLoadBalancedApiClient.class);

      assertThat(config.getAgent().getBasePath()).isEqualTo("http://localhost:443/context");
    });
  }

  @Test
  void shouldNotInitializeDatafeedIfDisabled() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.host=localhost",

            "bdk.bot.username=testbot",
            "bdk.bot.privateKeyPath=classpath:/privatekey.pem",

            "bdk.datafeed.enabled=false"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasSingleBean(SymphonyBdkAutoConfiguration.class);

      final SymphonyBdkCoreProperties config = context.getBean(SymphonyBdkCoreProperties.class);
      assertThat(config.getAgent().getBasePath()).isEqualTo("https://localhost:443");

      assertThat(context).doesNotHaveBean(DatafeedLoop.class);
      assertThat(context).doesNotHaveBean(DatafeedAsyncLauncherService.class);
      assertThat(context).doesNotHaveBean(ActivityRegistry.class);
      assertThat(context).doesNotHaveBean(SlashAnnotationProcessor.class);
    });
  }
}
