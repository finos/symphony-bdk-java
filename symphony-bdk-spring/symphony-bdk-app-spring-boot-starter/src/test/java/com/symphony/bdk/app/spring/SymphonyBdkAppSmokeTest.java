package com.symphony.bdk.app.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.app.spring.auth.CircleOfTrustController;
import com.symphony.bdk.app.spring.config.BdkExtAppControllerConfig;
import com.symphony.bdk.app.spring.service.SymphonyBdkHealthIndicator;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = TestApplication.class)
public class SymphonyBdkAppSmokeTest {

  @Autowired
  private ApplicationContext context;

  @Test
  void shouldAutoConfigureAppServices() {
    assertThat(context.getBean(SymphonyBdkAppAutoConfiguration.class)).isNotNull();
    assertThat(context.getBean(BdkExtAppControllerConfig.class)).isNotNull();
    assertThat(context.getBean(CircleOfTrustController.class)).isNotNull();
    assertThat(context.getBean(SymphonyBdkHealthIndicator.class)).isNotNull();
  }
}
