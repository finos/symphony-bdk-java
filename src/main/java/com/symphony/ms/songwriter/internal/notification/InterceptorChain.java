package com.symphony.ms.songwriter.internal.notification;

import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;
import com.symphony.ms.songwriter.internal.notification.model.NotificationRequest;

public interface InterceptorChain {

  void register(NotificationInterceptor notificationInterceptor);

  void register(int index, NotificationInterceptor notificationInterceptor);

  boolean execute(NotificationRequest notificationRequest,
      final SymphonyMessage notificationMessage);

}
