package com.symphony.bdk.core.activity;

import com.symphony.bdk.gen.api.model.V4Initiator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apiguardian.api.API;

/**
 * Base class for an activity context holder. Contains the mandatory context attributes:
 * <ul>
 *   <li>{@link ActivityContext#initiator} : user info that triggered the chat event</li>
 *   <li>{@link ActivityContext#eventSource} : chat event source object</li>
 * </ul>
 */
@Getter
@RequiredArgsConstructor
@API(status = API.Status.STABLE)
public abstract class ActivityContext<E> {

  /** The activity initiator  (i.e. the Symphony that triggered an event in the chat)  */
  private final V4Initiator initiator;

  /**
   * The activity real-time event source. Possible classes:
   * <ul>
   *   <li>{@link com.symphony.bdk.gen.api.model.V4MessageSent}</li>
   *   <li>{@link com.symphony.bdk.gen.api.model.V4SymphonyElementsAction}</li>
   * </ul>
   */
  private final E eventSource;
}
