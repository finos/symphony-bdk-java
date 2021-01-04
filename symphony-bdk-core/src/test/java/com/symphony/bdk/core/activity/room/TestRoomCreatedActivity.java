package com.symphony.bdk.core.activity.room;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;

import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Function;

public class TestRoomCreatedActivity extends RoomCreatedActivity<RoomCreatedContext> {

  @Setter
  private Consumer<RoomCreatedContext> onActivity = c -> {};
  @Setter
  private Function<RoomCreatedContext, Boolean> matcher = c -> true;
  @Setter
  private Consumer<RoomCreatedContext> beforeMatcher = c -> {};

  @Override
  protected ActivityMatcher<RoomCreatedContext> matcher() {
    return this.matcher::apply;
  }

  @Override
  protected void onActivity(RoomCreatedContext context) {
    this.onActivity.accept(context);
  }

  @Override
  protected void beforeMatcher(RoomCreatedContext context) {
    super.beforeMatcher(context);
    this.beforeMatcher.accept(context);
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.ROOM_CREATED);
  }
}
