package com.symphony.bdk.core;

import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OboServicesFacadeTest {

  private OboServicesFacade oboServicesFacade;

  @BeforeEach
  void setUp() {
    oboServicesFacade = new OboServicesFacade(new BdkConfig(), mock(AuthSession.class));
    Assertions.assertNotNull(oboServicesFacade.messages());
  }

  @Test
  void testOboStreams() {
    Assertions.assertNotNull(oboServicesFacade.streams());
  }

  @Test
  void testOboUsers() {
    Assertions.assertNotNull(oboServicesFacade.users());
  }

  @Test
  void testOboMessages() {
    Assertions.assertNotNull(oboServicesFacade.messages());
  }
}
