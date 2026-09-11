package com.symphony.bdk.test.spring;

import static org.assertj.core.api.Assertions.assertThat;

import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.test.spring.annotation.SymphonyBdkSpringBootTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SymphonyBdkSpringBootTest
public class SymphonyBdkSpringBootTestSmokeTest {

  @Autowired
  private MessageService messageService;

  @Test
  void testStackWorks() {
    assertThat(messageService).isNotNull();
  }
}
