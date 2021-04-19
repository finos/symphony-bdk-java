package com.symphony.bdk.core.service.datafeed;

import org.apiguardian.api.API;

/**
 * A particular exception to throw from {@link RealTimeEventListener} implementations to explicitly indicate that the
 * event processing has failed and that all the events received in the datafeed read events call should be re-queued.
 * They will eventually get redispatched after some time (30s by default).
 * <p>
 * Only supported by DFv2.
 */
@API(status = API.Status.STABLE)
public class EventException extends RuntimeException {

  @SuppressWarnings("unused")
  public EventException(String message) {
    super(message);
  }

  @SuppressWarnings("unused")
  public EventException(String message, Throwable cause) {
    super(message, cause);
  }

  @SuppressWarnings("unused")
  public EventException(Throwable cause) {
    super(cause);
  }
}
