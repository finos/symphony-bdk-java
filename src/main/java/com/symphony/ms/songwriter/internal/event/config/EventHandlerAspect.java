package com.symphony.ms.songwriter.internal.event.config;

import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import com.symphony.ms.songwriter.internal.event.model.BaseEvent;
import com.symphony.ms.songwriter.internal.feature.FeatureManager;
import com.symphony.ms.songwriter.internal.message.MessageService;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

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

  private MessageService messageService;

  public EventHandlerAspect(FeatureManager featureManager,
      MessageService messageService) {
    this.featureManager = featureManager;
    this.messageService = messageService;
  }

  @Around("execution(public void com.symphony.ms.songwriter.internal.event.InternalEventListenerImpl.*(..))")
  public void initEventHandling(ProceedingJoinPoint joinPoint) throws Throwable {
    BaseEvent event = (BaseEvent) joinPoint.getArgs()[0];

    MDC.put(TRANSACTION_ID, String.valueOf(UUID.randomUUID()));
    MDC.put(STREAM_ID, event.getStreamId());
    if (event.getUserId() != null && !event.getUserId().isEmpty()) {
      MDC.put(USER_ID, event.getUserId());
    }

    try {
      joinPoint.proceed();
    } catch (Exception e) {
      LOGGER.error("Unexpected error handling Symphony event: {}",
          event.getClass().getSimpleName(), e);

      if (featureManager.unexpectedErrorResponse() != null) {
        messageService.sendMessage(event.getStreamId(),
            new SymphonyMessage(featureManager.unexpectedErrorResponse()));
      }
    }

    MDC.clear();
  }

}
