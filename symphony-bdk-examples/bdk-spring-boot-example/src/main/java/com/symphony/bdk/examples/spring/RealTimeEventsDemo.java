package com.symphony.bdk.examples.spring;

import com.symphony.bdk.core.service.datafeed.EventPayload;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;
import com.symphony.bdk.gen.api.model.V4UserLeftRoom;
import com.symphony.bdk.spring.events.RealTimeEvent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Demo class showing how to subscribe to a specific {@link RealTimeEvent}.
 */
@Slf4j
@Component
public class RealTimeEventsDemo {

  @EventListener
  public void onMessageSent(RealTimeEvent<V4MessageSent> event) {
    log.info("{} received at {}", event.toString(), ((EventPayload) event.getSource()).getEventTimestamp());
  }

  @EventListener
  public void onUserJoined(RealTimeEvent<V4UserJoinedRoom> event) {
    log.info(event.toString());
  }

  @EventListener
  public void onUserLeft(RealTimeEvent<V4UserLeftRoom> event) {
    log.info(event.toString());
  }
}
