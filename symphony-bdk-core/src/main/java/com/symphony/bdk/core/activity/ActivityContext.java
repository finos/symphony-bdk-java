package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.service.datafeed.EventPayload;
import com.symphony.bdk.gen.api.model.V4Initiator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

/**
 * Base class for an activity context holder. Contains the mandatory context attributes:
 * <ul>
 *   <li>{@link ActivityContext#initiator} : user info that triggered the chat event</li>
 *   <li>{@link ActivityContext#sourceEvent} : chat event source object</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
@API(status = API.Status.STABLE)
public abstract class ActivityContext<E> {

  /** The activity initiator  (i.e. the Symphony that triggered an event in the chat)  */
  private final V4Initiator initiator;

  /**
   * The activity source real-time event. Possible classes:
   * <ul>
   *   <li>{@link com.symphony.bdk.gen.api.model.V4MessageSent}</li>
   *   <li>{@link com.symphony.bdk.gen.api.model.V4SymphonyElementsAction}</li>
   * </ul>
   */
  private final E sourceEvent;

  /**
   * The original event triggered timestamp
   */
  private final Long eventTimestamp;

  public ActivityContext(V4Initiator initiator, E sourceEvent) {
    this.initiator = initiator;
    this.sourceEvent = sourceEvent;
    if (EventPayload.class.isAssignableFrom(sourceEvent.getClass())) {
      this.eventTimestamp = ((EventPayload) sourceEvent).getEventTimestamp();
    } else {
      this.eventTimestamp = null;
    }
  }

}
