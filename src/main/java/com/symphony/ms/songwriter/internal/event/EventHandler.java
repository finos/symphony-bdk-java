package com.symphony.ms.songwriter.internal.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import com.symphony.ms.songwriter.internal.event.model.BaseEvent;
import com.symphony.ms.songwriter.internal.feature.FeatureManager;
import com.symphony.ms.songwriter.internal.message.MessageService;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public abstract class EventHandler<E extends BaseEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

  private EventDispatcher eventDispatcher;

  private MessageService messageService;

  private FeatureManager featureManager;

  public void register() {
    ResolvableType type = ResolvableType.forRawClass(this.getClass());
    eventDispatcher.register(type.getSuperType().getGeneric(0).toString(), this);
  }

  public void onEvent(E event) {
    LOGGER.debug("Received event for stream: {}", event.getStreamId());

    final SymphonyMessage eventResponse = new SymphonyMessage();
    try {
      handle(event, eventResponse);

      if (eventResponse.hasContent()
          && featureManager.isCommandFeedbackEnabled()) {
        messageService.sendMessage(event.getStreamId(), eventResponse);
      }

    } catch (Exception e) {
      LOGGER.error("Error processing event {}", e);
      if (featureManager.unexpectedErrorResponse() != null) {
        messageService.sendMessage(event.getStreamId(),
            new SymphonyMessage(featureManager.unexpectedErrorResponse()));
      }
    }
  }

  public abstract void handle(E event, final SymphonyMessage eventResponse);

  public void setEventDispatcher(EventDispatcher eventDispatcher) {
    this.eventDispatcher = eventDispatcher;
  }

  public void setMessageService(MessageService messageService) {
    this.messageService = messageService;
  }

  public void setFeatureManager(FeatureManager featureManager) {
    this.featureManager = featureManager;
  }
}
