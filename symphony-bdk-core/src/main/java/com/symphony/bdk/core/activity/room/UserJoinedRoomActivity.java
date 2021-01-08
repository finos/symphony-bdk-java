package com.symphony.bdk.core.activity.room;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindOnUserJoinedRoom;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;

import org.apiguardian.api.API;

import java.util.function.Consumer;

/**
 * A user-joined-room activity corresponds to an User joined room event.
 */
@API(status = API.Status.STABLE)
public abstract class UserJoinedRoomActivity<C extends UserJoinedRoomContext>
    extends AbstractActivity<V4UserJoinedRoom, C> {

  /**
   * {@inheritDoc}
   */
  @Override
  protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
    bindOnUserJoinedRoom(realTimeEventsSource, this::processEvent);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void beforeMatcher(C context) {
    super.beforeMatcher(context);
    context.setRoomId(context.getSourceEvent().getStream().getStreamId());
    context.setUserId(context.getSourceEvent().getAffectedUser().getUserId());
  }
}
