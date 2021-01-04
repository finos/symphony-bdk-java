package com.symphony.bdk.core.activity.room;

import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4RoomCreated;

import com.symphony.bdk.gen.api.model.V4Stream;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

/**
 * Default implementation of the {@link ActivityContext} handled by the {@link RoomCreatedActivity}.
 */
@Getter
@Setter
@API(status = API.Status.STABLE)
public class RoomCreatedContext extends ActivityContext<V4RoomCreated> {

  /** The room information extracted from event source */
  private V4Stream room;

  /**
   * Default constructor matching super.
   *
   * @param initiator Activity initiator.
   * @param sourceEvent Event source of the activity.
   */
  public RoomCreatedContext(V4Initiator initiator, V4RoomCreated sourceEvent) {
    super(initiator, sourceEvent);
  }
}
