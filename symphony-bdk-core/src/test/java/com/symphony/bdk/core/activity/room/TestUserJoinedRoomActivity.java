package com.symphony.bdk.core.activity.room;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;

import com.symphony.bdk.core.activity.model.ActivityType;

import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Function;

public class TestUserJoinedRoomActivity extends UserJoinedRoomActivity<UserJoinedRoomContext> {

  @Setter
  private Consumer<UserJoinedRoomContext> onActivity = c -> {};
  @Setter
  private Function<UserJoinedRoomContext, Boolean> matcher = c -> true;
  @Setter
  private Consumer<UserJoinedRoomContext> beforeMatcher = c -> {};

  @Override
  protected ActivityMatcher<UserJoinedRoomContext> matcher() {
    return this.matcher::apply;
  }

  @Override
  protected void onActivity(UserJoinedRoomContext context) {
    this.onActivity.accept(context);
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.USER_JOINED_ROOM);
  }

  @Override
  protected void beforeMatcher(UserJoinedRoomContext context) {
    super.beforeMatcher(context);
    this.beforeMatcher.accept(context);
  }
}
