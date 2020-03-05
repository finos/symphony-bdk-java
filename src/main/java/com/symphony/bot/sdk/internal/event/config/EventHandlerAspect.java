package com.symphony.bot.sdk.internal.event.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import com.symphony.bot.sdk.internal.event.model.BaseEvent;
import com.symphony.bot.sdk.internal.feature.FeatureManager;
import com.symphony.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

import java.util.UUID;

/**
 * Logging aspect that adds transaction ID, stream ID and user ID (when
 * applicable) to the log context.
 *
 * @author Marcus Secato
 *
 */
@Component
@Aspect
public class EventHandlerAspect {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventHandlerAspect.class);

  private static final String TRANSACTION_ID = "transactionId";
  private static final String STREAM_ID = "streamId";
  private static final String USER_ID = "userId";

  private FeatureManager featureManager;

  private MessageClientImpl messageClient;

  public EventHandlerAspect(FeatureManager featureManager,
      MessageClientImpl messageClient) {
    this.featureManager = featureManager;
    this.messageClient = messageClient;
  }

  @Around("execution(public void com.symphony.bot.sdk.internal.event.InternalEventListenerImpl.*(..))")
  public void initEventHandling(ProceedingJoinPoint joinPoint) throws Throwable {
    BaseEvent event = (BaseEvent) joinPoint.getArgs()[0];

    MDC.put(TRANSACTION_ID, String.valueOf(UUID.randomUUID()));
    MDC.put(STREAM_ID, event.getStreamId());
    if (event.getUserId() != null) {
      MDC.put(USER_ID, event.getUserId().toString());
    }

    try {
      joinPoint.proceed();
    } catch (Exception e) {
      LOGGER.error("Unexpected error handling Symphony event: {}",
          event.getClass().getSimpleName(), e);

      if (featureManager.unexpectedErrorResponse() != null) {
        messageClient._sendMessage(event.getStreamId(),
            new SymphonyMessage(featureManager.unexpectedErrorResponse()));
      }
    }

    MDC.clear();
  }

}
