package com.symphony.bdk.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;

import com.symphony.bdk.core.service.connection.OboConnectionService;
import com.symphony.bdk.core.service.message.OboMessageService;
import com.symphony.bdk.core.service.presence.OboPresenceService;
import com.symphony.bdk.core.service.session.OboSessionService;
import com.symphony.bdk.core.service.signal.OboSignalService;
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
    OboStreamService streamService = oboServices.streams();
    assertNotNull(streamService);
  }

  @Test
  void testOboUsers() {
    OboUserService userService = oboServices.users();
    assertNotNull(userService);
  }

  @Test
  void testOboMessages() {
    OboMessageService messageService = oboServices.messages();
    assertNotNull(messageService);
  }

  @Test
  void testOboPresences() {
    OboPresenceService presenceService = oboServices.presences();
    assertNotNull(presenceService);
  }

  @Test
  void testOboConnections() {
    OboConnectionService connectionService = oboServices.connections();
    assertNotNull(connectionService);
  }

  @Test
  void testOboSignals() {
    OboSignalService signalService = oboServices.signals();
    assertNotNull(signalService);
  }

  @Test
  void testOboSessions() {
    OboSessionService sessionService = oboServices.sessions();
    assertNotNull(sessionService);
  }
}
