package com.symphony.ms.bot.sdk.internal.notification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.symphony.ms.bot.sdk.internal.notification.model.NotificationRequest;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

@ExtendWith(MockitoExtension.class)
public class NotificationInterceptorTest {

  @Mock
  private InterceptorChain interceptorChain;

  @InjectMocks
  private TestNotificationInterceptor notificationInterceptor;

  static class TestNotificationInterceptor extends NotificationInterceptor {

    private BiFunction<NotificationRequest, SymphonyMessage, Boolean> internalProcess;

    @Override
    public boolean process(NotificationRequest notificationRequest,
        SymphonyMessage notificationMessage) {
      if (internalProcess != null) {
        return internalProcess.apply(notificationRequest, notificationMessage);
      }
      return false;
    }

    // Helper to ease changing the behavior of intercept method on each test
    private void setInternalProcess(
        BiFunction<NotificationRequest, SymphonyMessage, Boolean> func) {
      this.internalProcess = func;
    }
  }

  @Test
  public void registerTest() {
    notificationInterceptor.register();

    verify(interceptorChain, times(1)).register(notificationInterceptor);
  }

  @Test
  public void interceptTest() {
    NotificationInterceptor spyNotificationInterceptor = spy(notificationInterceptor);
    NotificationRequest request = mock(NotificationRequest.class);
    SymphonyMessage response = mock(SymphonyMessage.class);

    spyNotificationInterceptor.intercept(request, response);

    verify(spyNotificationInterceptor, times(1)).process(request, response);
  }

  @Test
  public void allowRequestTest() {
    notificationInterceptor.setInternalProcess((req, res) -> true);
    NotificationRequest request = mock(NotificationRequest.class);
    SymphonyMessage response = mock(SymphonyMessage.class);

    assertTrue(notificationInterceptor.intercept(request, response));
  }

  @Test
  public void denyRequestTest() {
    notificationInterceptor.setInternalProcess((req, res) -> false);
    NotificationRequest request = mock(NotificationRequest.class);
    SymphonyMessage response = mock(SymphonyMessage.class);

    assertFalse(notificationInterceptor.intercept(request, response));
  }

  @Test
  public void errorProcessingRequestTest() {
    NotificationInterceptor spyNotificationInterceptor = spy(notificationInterceptor);
    NotificationRequest request = mock(NotificationRequest.class);
    SymphonyMessage response = mock(SymphonyMessage.class);
    doThrow(new RuntimeException())
      .when(spyNotificationInterceptor)
      .process(request, response);

    assertFalse(spyNotificationInterceptor.intercept(request, response));
  }

}
