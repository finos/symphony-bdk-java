package com.symphony.bdk.core.command.event;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

/**
 * TODO: add description here
 */
public class OnFormReply implements CommandEventType<V4SymphonyElementsAction> {

  @Override
  public RealTimeEventListener listener() {
    return new RealTimeEventListener() {

      @Override
      public void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {

      }
    };
  }

  @Override
  public Class<V4SymphonyElementsAction> eventPayloadType() {
    return V4SymphonyElementsAction.class;
  }
}
