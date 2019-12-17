package com.symphony.ms.bot.sdk.internal.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.notification.model.NotificationRequest;

/**
 * Base class for intercepting and processing incoming requests from external
 * sources. Provides mechanisms to automatically register child classes to
 * {@link InterceptorChain}.
 *
 * @author Marcus Secato
 *
 */
public abstract class NotificationInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationInterceptor.class);

  protected InterceptorChain interceptorChain;

  /**
   * Registers the NotificationInterceptor to {@link InterceptorChain}
   */
  public void register() {
    interceptorChain.register(this);
  }

  /**
   * Intercepts an incoming request
   *
   * @param notificationRequest
   * @param notificationMessage
   * @return true if request processing should proceed, false if request should
   *         be discarded
   */
  public boolean intercept(NotificationRequest notificationRequest,
      SymphonyMessage notificationMessage) {
    LOGGER.debug("Processing notification request");

    try {
      return process(notificationRequest, notificationMessage);
    } catch (Exception e) {
      LOGGER.error("Error processing notification request\n{}", e);
      return false;
    }
  }


  /**
   * Processes the incoming request
   *
   * @param notificationRequest
   * @param notificationMessage
   * @return true if request processing should proceed, false if request should
   *         be discarded
   */
  public abstract boolean process(NotificationRequest notificationRequest,
      SymphonyMessage notificationMessage);

  public void setInterceptorChain(InterceptorChain interceptorChain) {
    this.interceptorChain = interceptorChain;
  }

}
