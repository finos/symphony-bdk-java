package com.symphony.bdk.core.service.datafeed.impl;

import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Event;

import org.apiguardian.api.API;

/**
 * Internal exception to convey the message that ack id should not be updated thus re-queuing events.
 */
@API(status = API.Status.INTERNAL)
public class RequeueEventException extends RuntimeException {
  public RequeueEventException(V4Event event, RealTimeEventListener listener, EventException e) {
    super(String.format("Listener %s failed to process event %s with type %s, events will be re-queued",
        listener, event.getId(), event.getType()), e);
  }
}
