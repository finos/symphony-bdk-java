package com.symphony.ms.songwriter.internal.notification;

public abstract class OrderedNotificationInterceptor extends NotificationInterceptor {

  @Override
  public void register() {
    interceptorChain.register(getOrder(), this);
  }

  protected abstract int getOrder();

}
