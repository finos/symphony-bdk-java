package com.symphony.bdk.core.activity.room;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindOnRoomCreated;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4RoomCreated;

import org.apiguardian.api.API;

import java.util.function.Consumer;

/**
 * A room created activity corresponds to an Room Created event.
 */
@API(status = API.Status.STABLE)
public abstract class RoomCreatedActivity<C extends RoomCreatedContext> extends AbstractActivity<V4RoomCreated, C> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
    bindOnRoomCreated(realTimeEventsSource, this::processEvent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void beforeMatcher(C context) {
    super.beforeMatcher(context);
    // copy room info at the context root level
    context.setRoom(context.getSourceEvent().getStream());
  }
}
