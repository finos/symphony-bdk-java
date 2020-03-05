package com.symphony.bot.sdk.internal.event;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bot.sdk.internal.event.EventDispatcherImpl;
import com.symphony.bot.sdk.internal.event.EventHandler;
import com.symphony.bot.sdk.internal.event.model.BaseEvent;

@ExtendWith(MockitoExtension.class)
public class EventDispatcherTest {

  @InjectMocks
  private EventDispatcherImpl eventDispatcher;

  @Test
  public void dispatchSuccessTest() {
    EventHandler<BaseEvent> eventHandler = mock(EventHandler.class);
    BaseEvent event = mock(BaseEvent.class);

    eventDispatcher.register("TestEvent", eventHandler);
    eventDispatcher.push("TestEvent", event);

    verify(eventHandler, times(1)).onEvent(event);
  }

  @Test
  public void dispatchNoHandlerTest() {
    EventHandler<BaseEvent> eventHandler = mock(EventHandler.class);
    BaseEvent event = mock(BaseEvent.class);

    eventDispatcher.register("TestEvent1", eventHandler);
    eventDispatcher.push("TestEvent2", event);

    verify(eventHandler, never()).onEvent(event);
  }

}
