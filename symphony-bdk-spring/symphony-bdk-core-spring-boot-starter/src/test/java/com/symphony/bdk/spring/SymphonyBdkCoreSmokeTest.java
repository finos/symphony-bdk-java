package com.symphony.bdk.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest(classes = TestApplication.class)
public class SymphonyBdkCoreSmokeTest {

  @Autowired
  private ApplicationContext context;

  @Test
  void shouldAutoConfigureCoreServices() {
    assertThat(context.getBean(SymphonyBdkAutoConfiguration.class)).isNotNull();
    assertThat(context.getBean(HealthService.class)).isNotNull();
    assertThat(context.getBean(UserService.class)).isNotNull();
    assertThat(context.getBean(StreamService.class)).isNotNull();
    assertThat(context.getBean(MessageService.class)).isNotNull();
  }
}
