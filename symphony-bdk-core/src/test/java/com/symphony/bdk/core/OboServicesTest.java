package com.symphony.bdk.core;

import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OboServicesTest {

  private OboServices oboServices;

  @BeforeEach
  void setUp() {
    oboServices = new OboServices(new BdkConfig(), mock(AuthSession.class));
    Assertions.assertNotNull(oboServices.messages());
  }

  @Test
  void testOboStreams() {
    Assertions.assertNotNull(oboServices.streams());
  }

  @Test
  void testOboUsers() {
    Assertions.assertNotNull(oboServices.users());
  }

  @Test
  void testOboMessages() {
    Assertions.assertNotNull(oboServices.messages());
  }
}
