package com.symphony.bdk.bot.sdk.notification;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bdk.bot.sdk.notification.InterceptorChainImpl;
import com.symphony.bdk.bot.sdk.notification.NotificationInterceptor;
import com.symphony.bdk.bot.sdk.notification.model.NotificationRequest;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

@ExtendWith(MockitoExtension.class)
public class InterceptorChainTest {

  @InjectMocks
  private InterceptorChainImpl interceptorChain;

  @Mock
  private NotificationRequest request = mock(NotificationRequest.class);

  @Mock
  private SymphonyMessage response = mock(SymphonyMessage.class);

  @Test
  public void executeNoInterceptorTest() {
    assertTrue(interceptorChain.execute(request, response));
  }

  @Test
  public void executeAcceptRequestTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    when(interceptorA.intercept(request, response)).thenReturn(true);

    interceptorChain.register(interceptorA);

    assertTrue(interceptorChain.execute(request, response));
    verify(interceptorA, times(1)).intercept(request, response);
  }

  @Test
  public void executeRejectRequestTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    when(interceptorA.intercept(request, response)).thenReturn(false);

    interceptorChain.register(interceptorA);

    assertFalse(interceptorChain.execute(request, response));
    verify(interceptorA, times(1)).intercept(request, response);
  }

  @Test
  public void executeAcceptNoOrderTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    when(interceptorA.intercept(request, response)).thenReturn(true);
    NotificationInterceptor interceptorB = mock(NotificationInterceptor.class);
    when(interceptorB.intercept(request, response)).thenReturn(true);

    interceptorChain.register(interceptorA);
    interceptorChain.register(interceptorB);

    assertTrue(interceptorChain.execute(request, response));
    verify(interceptorA, times(1)).intercept(request, response);
    verify(interceptorB, times(1)).intercept(request, response);
  }

  @Test
  public void executeRejectNoOrderTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    when(interceptorA.intercept(request, response)).thenReturn(true);
    NotificationInterceptor interceptorB = mock(NotificationInterceptor.class);
    when(interceptorB.intercept(request, response)).thenReturn(false);

    interceptorChain.register(interceptorA);
    interceptorChain.register(interceptorB);

    assertFalse(interceptorChain.execute(request, response));
    verify(interceptorA, times(1)).intercept(request, response);
    verify(interceptorB, times(1)).intercept(request, response);
  }

  @Test
  public void executeAcceptOrderedTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    when(interceptorA.intercept(request, response)).thenReturn(true);
    NotificationInterceptor interceptorB = mock(NotificationInterceptor.class);
    when(interceptorB.intercept(request, response)).thenReturn(true);
    InOrder inOrder = inOrder(interceptorB, interceptorA);

    interceptorChain.register(interceptorA);
    interceptorChain.register(0, interceptorB);

    assertTrue(interceptorChain.execute(request, response));
    inOrder.verify(interceptorB).intercept(request, response);
    inOrder.verify(interceptorA).intercept(request, response);
  }

  @Test
  public void executeRejectOrderedTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    when(interceptorA.intercept(request, response)).thenReturn(false);
    NotificationInterceptor interceptorB = mock(NotificationInterceptor.class);
    when(interceptorB.intercept(request, response)).thenReturn(true);
    InOrder inOrder = inOrder(interceptorB, interceptorA);

    interceptorChain.register(interceptorA);
    interceptorChain.register(0, interceptorB);

    assertFalse(interceptorChain.execute(request, response));
    inOrder.verify(interceptorB).intercept(request, response);
    inOrder.verify(interceptorA).intercept(request, response);
  }

  @Test
  public void executeEarlyRejectOrderedTest() {
    NotificationInterceptor interceptorA = mock(NotificationInterceptor.class);
    NotificationInterceptor interceptorB = mock(NotificationInterceptor.class);
    when(interceptorB.intercept(request, response)).thenReturn(false);

    interceptorChain.register(interceptorA);
    interceptorChain.register(0, interceptorB);

    assertFalse(interceptorChain.execute(request, response));
    verify(interceptorB, times(1)).intercept(request, response);
    verify(interceptorA, never()).intercept(request, response);
  }
}
