package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * TODO: add description here
 */
@Slf4j
@RequiredArgsConstructor
@API(status = API.Status.STABLE)
public class ActivityRegistry {

  /** List of activities */
  @Getter private final List<Activity<?>> activityList = new ArrayList<>();

  /** The bot display name forwarded to command-based activities only */
  private final String botDisplayName;

  /** The Datafeed real-time events subscriber */
  private final Consumer<RealTimeEventListener> subscriber;

  /**
   * Registers an activity within the registry.
   *
   * @param activity Any kind of activity.
   */
  public void register(Activity<?> activity) {
    this.preProcessActivity(activity);
    this.activityList.add(activity);
  }

  private void preProcessActivity(Activity<?> activity) {

    // a command activity (potentially) needs the bot display name in order to parse the message text content
    // this way of passing this information is not very clean though, we should find something
    if (activity instanceof CommandActivity) {
      ((CommandActivity<?>) activity).setBotDisplayName(this.botDisplayName);
    }

    // make the activity to subscribe to its expected real-time event
    activity.subscribe(this.subscriber);
  }
}
