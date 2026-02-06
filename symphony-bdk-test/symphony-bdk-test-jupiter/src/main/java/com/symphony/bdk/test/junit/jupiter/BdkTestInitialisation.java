package com.symphony.bdk.test.junit.jupiter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.test.SymphonyBdkTestMock;
import com.symphony.bdk.test.annotation.SymphonyBdkTest;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class BdkTestInitialisation implements BeforeAllCallback {
  private final SymphonyBdkTestMock bdkTestMock;

  public BdkTestInitialisation(SymphonyBdkTestMock bdkTestMock) {
    this.bdkTestMock = bdkTestMock;
  }

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    final Class<?> clz = context.getElement()
        .map(e -> (Class<?>) e)
        .orElseThrow(() -> new RuntimeException("Fatal error, could initialize story context."));
    final SymphonyBdkTest annotation = clz.getAnnotation(SymphonyBdkTest.class);
    SymphonyBdk symphonyBdk = bdkTestMock.getSymphonyBdk();
    UserV2 botUser = new UserV2().id(annotation.botId()).username(annotation.botName()).displayName(
        annotation.botDisplayName());
    when(symphonyBdk.botInfo()).thenReturn(botUser);
    initAuthSessionMock(symphonyBdk);
    initMessageServiceMock(symphonyBdk);
    initSessionServiceMock(symphonyBdk, botUser);
    initOtherMocks(symphonyBdk);
    final ActivityRegistry activityRegistry = spy(new ActivityRegistry(botUser, bdkTestMock.getDatafeedLoop()));
    when(symphonyBdk.activities()).thenReturn(activityRegistry);
  }

  private void initOtherMocks(SymphonyBdk symphonyBdk) {
    when(symphonyBdk.health()).thenReturn(bdkTestMock.getHealthService());
    when(symphonyBdk.applications()).thenReturn(bdkTestMock.getApplicationService());
    when(symphonyBdk.connections()).thenReturn(bdkTestMock.getConnectionService());
    when(symphonyBdk.datafeed()).thenReturn(bdkTestMock.getDatafeedLoop());
    when(symphonyBdk.disclaimers()).thenReturn(bdkTestMock.getDisclaimerService());
    when(symphonyBdk.messages()).thenReturn(bdkTestMock.getMessageService());
    when(symphonyBdk.extensions()).thenReturn(bdkTestMock.getExtensionService());
    when(symphonyBdk.users()).thenReturn(bdkTestMock.getUserService());
    when(symphonyBdk.signals()).thenReturn(bdkTestMock.getSignalService());
    when(symphonyBdk.presences()).thenReturn(bdkTestMock.getPresenceService());
    when(symphonyBdk.streams()).thenReturn(bdkTestMock.getStreamService());
  }

  private void initSessionServiceMock(SymphonyBdk symphonyBdk, UserV2 botUser) {
    SessionService sessionService = bdkTestMock.getSessionService();
    when(sessionService.getSession()).thenReturn(botUser);
    when(symphonyBdk.sessions()).thenReturn(sessionService);
  }

  private void initAuthSessionMock(SymphonyBdk symphonyBdk) {
    AuthSession authSession = bdkTestMock.getBotSession();
    when(authSession.getSessionToken()).thenReturn("sessionToken");
    when(authSession.getKeyManagerToken()).thenReturn("kmToken");
    when(symphonyBdk.botSession()).thenReturn(authSession);
  }

  private void initMessageServiceMock(SymphonyBdk symphonyBdk) {
    MessageService messageService = bdkTestMock.getMessageService();
    TemplateEngine templateEngine = mock(TemplateEngine.class);
    when(messageService.templates()).thenReturn(templateEngine);
    when(symphonyBdk.messages()).thenReturn(messageService);
  }
}
