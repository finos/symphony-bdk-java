package com.symphony.bdk.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.extension.ExtensionService;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.disclaimer.DisclaimerService;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.signal.SignalService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;

import lombok.Data;

import java.time.Instant;

@Data
public class SymphonyBdkTestMock {
  private final SymphonyBdk symphonyBdk = mock(SymphonyBdk.class);
  private final OboAuthenticator oboAuthenticator = mock(OboAuthenticator.class);
  private final ExtensionAppAuthenticator extensionAppAuthenticator = mock(ExtensionAppAuthenticator.class);
  private final AuthSession botSession = mock(AuthSession.class);
  private final StreamService streamService = mock(StreamService.class);
  private final UserService userService = mock(UserService.class);
  private final MessageService messageService = mock(MessageService.class);
  private final PresenceService presenceService = mock(PresenceService.class);
  private final ConnectionService connectionService = mock(ConnectionService.class);
  private final SignalService signalService = mock(SignalService.class);
  private final ApplicationService applicationService = mock(ApplicationService.class);
  private final DisclaimerService disclaimerService = mock(DisclaimerService.class);
  private final SessionService sessionService = mock(SessionService.class);
  private final HealthService healthService = mock(HealthService.class);
  private final ExtensionService extensionService = mock(ExtensionService.class);
  private final MockDatafeedLoop datafeedLoop = spy(new MockDatafeedLoop());


  @Data
  private static class MockDatafeedLoop implements DatafeedLoop {

    public MockDatafeedLoop() {
      SymphonyBdkTestUtils.clearListeners();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }

    @Override
    public void subscribe(RealTimeEventListener realTimeEventListener) {
      SymphonyBdkTestUtils.addListener(realTimeEventListener);
    }

    @Override
    public void unsubscribe(RealTimeEventListener realTimeEventListener) {
      SymphonyBdkTestUtils.removeListener(realTimeEventListener);
    }

    @Override
    public long lastPullTimestamp() {
      return Instant.now().minusSeconds(5).toEpochMilli();
    }
  }
}
