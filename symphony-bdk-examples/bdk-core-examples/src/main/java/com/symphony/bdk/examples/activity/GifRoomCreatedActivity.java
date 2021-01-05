package com.symphony.bdk.examples.activity;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.activity.room.RoomCreatedActivity;
import com.symphony.bdk.core.activity.room.RoomCreatedContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GifRoomCreatedActivity extends RoomCreatedActivity<RoomCreatedContext> {

  @Override
  protected ActivityMatcher<RoomCreatedContext> matcher() {
    return context -> context.getRoom().getRoomName().contains("Gif-room");
  }

  @Override
  protected void onActivity(RoomCreatedContext context) {
    log.info("Gif Room is just created!");
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.ROOM_CREATED).name("Room created activity info");
  }
}
