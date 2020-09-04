package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class allows to bind an {@link AbstractActivity} to the Real Time Events source, or Datafeed.
 * It also maintains the list of registered activities.
 */
@Slf4j
@RequiredArgsConstructor
@API(status = API.Status.STABLE)
public class ActivityRegistry {

  /** List of activities */
  @Getter private final List<AbstractActivity<?, ?>> activityList = new ArrayList<>();

  /** The bot session forwarded to command-based activities only */
  private final UserV2 botSession;

  /** The Datafeed real-time events source, or Datafeed listener */
  private final Consumer<RealTimeEventListener> realTimeEventsSource;

  /**
   * Registers an activity within the registry.
   *
   * @param activity An activity.
   */
  public void register(final AbstractActivity<?, ?> activity) {
    this.preProcessActivity(activity);
    this.activityList.add(activity);
  }

  private void preProcessActivity(AbstractActivity<?, ?> activity) {

    // a command activity (potentially) needs the bot display name in order to parse the message text content
    // this way of passing this information is not very clean though, we should find something
    if (activity instanceof CommandActivity) {
      ((CommandActivity<?>) activity).setBotDisplayName(this.botSession.getDisplayName());
    }

    // make the activity to subscribe to its expected real-time event
    activity.bindToRealTimeEventsSource(this.realTimeEventsSource);
  }
}
