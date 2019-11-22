package com.symphony.ms.songwriter.internal.notification;

import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;
import com.symphony.ms.songwriter.internal.notification.model.NotificationRequest;

/**
 * Mechanism to chain multiple {@link NotificationInterceptor} together and
 * iterate over them.
 *
 * @author Marcus Secato
 *
 */
public interface InterceptorChain {

  /**
   * Registers a {@link NotificationInterceptor} to process incoming requests
   *
   * @param notificationInterceptor
   */
  void register(NotificationInterceptor notificationInterceptor);

  /**
   * Registers a {@link NotificationInterceptor} in a specific order
   *
   * @param index
   * @param notificationInterceptor
   */
  void register(int index, NotificationInterceptor notificationInterceptor);

  /**
   * Iterates over all registered interceptors retrieving the result once all
   * interceptors are done.
   *
   * @param notificationRequest
   * @param notificationMessage
   * @return true if all interceptors allowed the request to proceed, false if
   *         request should be discarded.
   */
  boolean execute(NotificationRequest notificationRequest,
      final SymphonyMessage notificationMessage);

}
