package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.message.OboMessageService;
import com.symphony.bdk.core.service.stream.OboStreamService;
import com.symphony.bdk.core.service.user.OboUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OboServicesTest {

  private OboServices oboServices;

  @BeforeEach
  void setUp() {
    oboServices = new OboServices(new BdkConfig(), mock(AuthSession.class));
  }

  @Test
  void testOboStreams() {
    assertNotNull(oboServices.streams());
    assertTrue(oboServices.streams() instanceof OboStreamService);
  }

  @Test
  void testOboUsers() {
    assertNotNull(oboServices.users());
    assertTrue(oboServices.users() instanceof OboUserService);
  }

  @Test
  void testOboMessages() {
    assertNotNull(oboServices.messages());
    assertTrue(oboServices.messages() instanceof OboMessageService);
  }
}
