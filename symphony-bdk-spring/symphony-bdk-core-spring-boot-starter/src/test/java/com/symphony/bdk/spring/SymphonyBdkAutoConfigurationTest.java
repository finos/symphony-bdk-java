package com.symphony.bdk.spring;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.client.loadbalancing.DatafeedLoadBalancedApiClient;
import com.symphony.bdk.core.extension.ExtensionService;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.http.api.ApiClient;
import com.symphony.bdk.spring.annotation.SlashAnnotationProcessor;
import com.symphony.bdk.spring.config.BdkActivityConfig;
import com.symphony.bdk.spring.config.BdkOboServiceConfig;
import com.symphony.bdk.spring.config.BdkServiceConfig;
import com.symphony.bdk.spring.extension.TestExtension;
import com.symphony.bdk.spring.extension.TestExtensionService;
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
            "bdk.bot.privateKey.path=classpath:/privatekey.pem"
        )
        .withBean(TestExtension.class)
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

      // verify extension service
      assertThat(context).hasSingleBean(ExtensionService.class);
      assertThat(context).hasSingleBean(TestExtension.class);
      assertThat(context).hasSingleBean(TestExtensionService.class);
      assertThat(context.getBean(ExtensionService.class).service(TestExtension.class)).isEqualTo(context.getBean(TestExtensionService.class));
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
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

            "bdk.app.appId=testapp",
            "bdk.app.privateKey.path=classpath:/privatekey.pem"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasBean("oboAuthenticator");
      assertThat(context).hasSingleBean(SymphonyBdkAutoConfiguration.class);
      assertThat(context).hasSingleBean(OboAuthenticator.class);
      assertThat(context).hasSingleBean(ExtensionAppAuthenticator.class);

      assertThat(context).hasBean("botSession");
      assertThat(context).hasSingleBean(BdkServiceConfig.class);
      assertThat(context).hasSingleBean(DatafeedLoop.class);
      assertThat(context).hasSingleBean(DatafeedAsyncLauncherService.class);
      assertThat(context).hasSingleBean(BdkActivityConfig.class);

      assertThat(context).hasBean("sessionService");
      assertThat(context).hasBean("streamService");
      assertThat(context).hasBean("userService");
      assertThat(context).hasBean("presenceService");
      assertThat(context).hasBean("connectionService");
      assertThat(context).hasBean("signalService");
      assertThat(context).hasBean("messageService");

      assertThat(context).doesNotHaveBean("oboSessionService");
      assertThat(context).doesNotHaveBean("oboStreamService");
      assertThat(context).doesNotHaveBean("oboUserService");
      assertThat(context).doesNotHaveBean("oboPresenceService");
      assertThat(context).doesNotHaveBean("oboConnectionService");
      assertThat(context).doesNotHaveBean("oboSignalService");
      assertThat(context).doesNotHaveBean("oboMessageService");
    });
  }

  @Test
  void shouldInitializeCustomAuthenticatorsIfTheyExist() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.scheme=http",
            "bdk.host=localhost",
            "bdk.context=context",

            "bdk.bot.username=testbot",
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

            "bdk.app.appId=testapp",
            "bdk.app.privateKey.path=classpath:/privatekey.pem"
        )
        .withBean("existingMockedOboAuthenticator", OboAuthenticator.class, ()-> mock(OboAuthenticator.class))
        .withBean("existingMockedExtensionAppAuthenticator", ExtensionAppAuthenticator.class, ()-> mock(ExtensionAppAuthenticator.class))
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasBean("existingMockedOboAuthenticator");
      assertThat(context).hasBean("existingMockedExtensionAppAuthenticator");
      assertThat(context).hasSingleBean(OboAuthenticator.class);
      assertThat(context).hasSingleBean(ExtensionAppAuthenticator.class);
    });
  }

  @Test
  void shouldAddAuthenticationIfCommonJwtEnabled() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.pod.scheme=http",
            "bdk.pod.host=localhost",

            "bdk.agent.scheme=http",
            "bdk.agent.host=localhost",

            "bdk.keyManager.scheme=http",
            "bdk.keyManager.host=localhost",

            "bdk.bot.username=testBot",
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

            "bdk.commonJwt.enabled=true"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasBean("podApiClient");
      ApiClient podClient = (ApiClient) context.getBean("podApiClient");
      assertThat(podClient.getAuthentications()).isNotEmpty();
      assertThat(podClient.getAuthentications()).containsKey("bearerAuth");
    });
  }

  @Test
  void shouldFailOnOboWithCommonJwtEnabled() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.host=localhost",

            "bdk.bot.username=testBot",
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

            "bdk.app.appId=my-app",
            "bdk.app.privateKey.path=classpath:/privatekey.pem",

            "bdk.commonJwt.enabled=true"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));


    contextRunner.run(context -> {
      assertThat(context).hasFailed();
      assertThat(context).getFailure().hasRootCauseInstanceOf(UnsupportedOperationException.class);
    });
  }

  @Test
  void shouldFailOnOboOnlyWithCommonJwtEnabled() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.host=localhost",

            "bdk.app.appId=my-app",
            "bdk.app.privateKey.path=classpath:/privatekey.pem",

            "bdk.commonJwt.enabled=true"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));


    contextRunner.run(context -> {
      assertThat(context).hasFailed();
      assertThat(context).getFailure().hasRootCauseInstanceOf(UnsupportedOperationException.class);
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
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

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
            "bdk.bot.privateKey.path=classpath:/privatekey.pem"
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
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

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
            "bdk.bot.privateKey.path=classpath:/privatekey.pem",

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

  @Test
  void shouldNotInitializeBotSessionWhenOboOnly() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withPropertyValues(
            "bdk.host=localhost",

            "bdk.app.appId=my-app",
            "bdk.app.privateKey.path=classpath:/privatekey.pem"
        )
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      assertThat(context).hasSingleBean(SymphonyBdkAutoConfiguration.class);
      assertThat(context).hasSingleBean(BdkOboServiceConfig.class);

      assertThat(context).doesNotHaveBean("botSession");
      assertThat(context).doesNotHaveBean(BdkServiceConfig.class);
      assertThat(context).doesNotHaveBean(DatafeedLoop.class);
      assertThat(context).doesNotHaveBean(DatafeedAsyncLauncherService.class);
      assertThat(context).doesNotHaveBean(BdkActivityConfig.class);
    });
  }

  @Test
  void systemApiClientShouldTargetAgentBasePath() {
    final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
        .withUserConfiguration(SymphonyBdkMockedConfiguration.class)
        .withConfiguration(AutoConfigurations.of(SymphonyBdkAutoConfiguration.class));

    contextRunner.run(context -> {
      String actualBasePath = context.getBeansOfType(SystemApi.class).get("systemApi").getApiClient().getBasePath();
      assertThat(actualBasePath).isEqualTo("/agent");
    });
  }

}
