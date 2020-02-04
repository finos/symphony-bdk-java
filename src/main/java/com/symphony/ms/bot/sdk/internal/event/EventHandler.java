package com.symphony.ms.bot.sdk.internal.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import com.symphony.ms.bot.sdk.internal.event.model.BaseEvent;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

/**
 * Base class for Symphony events handling. Provides mechanisms to
 * automatically register child classes to {@link EventDispatcher}.
 *
 * @author Marcus Secato
 *
 */
public abstract class EventHandler<E extends BaseEvent> implements BaseEventHandler<E> {
  private static final Logger LOGGER = LoggerFactory.getLogger(EventHandler.class);

  protected EventDispatcher eventDispatcher;

  private MessageClientImpl messageClient;

  private FeatureManager featureManager;

  /**
   * Registers the EventHandler to {@link EventDispatcher} so that it can
   * listen to and handle the specified Symphony event.
   *
   */
  public void register() {
    init();
    ResolvableType type = ResolvableType.forRawClass(this.getClass());
    eventDispatcher.register(
        type.getSuperType().getGeneric(0).toString(), this);
  }

  /**
   * Initializes the ElementsHandler dependencies. This method can be overridden by the child
   * classes if the developers want to implement initialization logic.
   */
  protected void init() {
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onEvent(E event) {
    LOGGER.debug("Received event for stream: {}", event.getStreamId());

    final SymphonyMessage eventResponse = new SymphonyMessage();
    try {
      handle(event, eventResponse);

      if (eventResponse.hasContent()
          && featureManager.isCommandFeedbackEnabled()) {
        messageClient._sendMessage(event.getStreamId(), eventResponse);
      }

    } catch (Exception e) {
      LOGGER.error("Error processing event {}", e);
    }
  }

  /**
   * Handles the Symphony event
   *
   * @param event
   * @param eventResponse the response to be sent to Symphony chat
   */
  public abstract void handle(E event, final SymphonyMessage eventResponse);

  public void setEventDispatcher(EventDispatcher eventDispatcher) {
    this.eventDispatcher = eventDispatcher;
  }

  public void setMessageClient(MessageClientImpl messageClient) {
    this.messageClient = messageClient;
  }

  public void setFeatureManager(FeatureManager featureManager) {
    this.featureManager = featureManager;
  }
}
