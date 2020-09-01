package com.symphony.bdk.core.command.event;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

/**
 * TODO: add description here
 */
public class OnMessageSent implements CommandEventType<V4MessageSent> {

  @Override
  public RealTimeEventListener listener() {
    return new RealTimeEventListener() {

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {

      }
    };
  }

  @Override
  public Class<V4MessageSent> eventPayloadType() {
    return V4MessageSent.class;
  }
}
