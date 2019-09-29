package com.symphony.ms.songwriter.internal.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;
import com.symphony.ms.songwriter.internal.notification.model.NotificationRequest;
import com.symphony.ms.songwriter.internal.webhook.BaseWebhookService;

public abstract class NotificationInterceptor {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotificationInterceptor.class);

  protected InterceptorChain interceptorChain;

  protected BaseWebhookService baseWebhookService;

  public void register() {
    interceptorChain.register(this);
  }

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

  public abstract boolean process(NotificationRequest notificationRequest,
      SymphonyMessage notificationMessage);

  public void setInterceptorChain(InterceptorChain interceptorChain) {
    this.interceptorChain = interceptorChain;
  }

  public void setBaseWebhookService(BaseWebhookService baseWebhookService) {
    this.baseWebhookService = baseWebhookService;
  }

}
