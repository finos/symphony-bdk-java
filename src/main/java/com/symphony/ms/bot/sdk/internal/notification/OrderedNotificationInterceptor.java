package com.symphony.ms.bot.sdk.internal.notification;

/**
 * Extends {@link NotificationInterceptor} to allow specifying the order in {@link InterceptorChain}
 * in which the notification interceptor should be invoked.
 *
 * @author Marcus Secato
 */
public abstract class OrderedNotificationInterceptor extends NotificationInterceptor {

  @Override
  public void register() {
    init();
    interceptorChain.register(getOrder(), this);
  }

  /**
   * Initializes the OrderedNotificationInterceptor dependencies. This method can be overridden by
   * the child classes if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * Specify the notification interceptor order in {@link InterceptorChain}
   *
   * @return
   */
  protected abstract int getOrder();

}
