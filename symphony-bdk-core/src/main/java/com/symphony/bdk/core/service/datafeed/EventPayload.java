package com.symphony.bdk.core.service.datafeed;

import org.apiguardian.api.API;

/**
 * The real time event payload type. All events read through datafeed should implement this type.
 */
@API(status = API.Status.EXPERIMENTAL)
public interface EventPayload {
  /**
   * the real event timestamp, when the event is generated from the platform before pushing to datafeed.
   *
   * @return timestamp in long
   */
  Long getEventTimestamp();

  void setEventTimestamp(Long eventTimestamp);
}
