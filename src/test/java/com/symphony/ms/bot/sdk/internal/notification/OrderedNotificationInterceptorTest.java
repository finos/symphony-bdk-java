package com.symphony.ms.bot.sdk.internal.notification;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.symphony.ms.bot.sdk.internal.notification.model.NotificationRequest;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

@ExtendWith(MockitoExtension.class)
public class OrderedNotificationInterceptorTest {

  @Mock
  private InterceptorChain interceptorChain;

  @InjectMocks
  private TestNotificationInterceptor notificationInterceptor;

  static class TestNotificationInterceptor extends OrderedNotificationInterceptor {

    @Override
    public boolean process(NotificationRequest notificationRequest,
        SymphonyMessage notificationMessage) {
      return false;
    }

    @Override
    protected int getOrder() {
      return 0;
    }
  }

  @Test
  public void registerTest() {
    OrderedNotificationInterceptor spyNotificationInterceptor = spy(notificationInterceptor);

    spyNotificationInterceptor.register();

    verify(spyNotificationInterceptor, times(1)).getOrder();
    verify(interceptorChain, times(1)).register(0, spyNotificationInterceptor);
  }

}
