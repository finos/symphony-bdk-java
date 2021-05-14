package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This class allows to bind an {@link AbstractActivity} to the Real Time Events source, or Datafeed.
 * It also maintains the list of registered activities.
 */
@Slf4j
@API(status = API.Status.STABLE)
public class ActivityRegistry {

  /** List of activities */
  private final List<AbstractActivity<?, ?>> activityList = new ArrayList<>();

  /** The bot session forwarded to command-based activities only */
  private final UserV2 botSession;

  /** The Datafeed real-time events source, or Datafeed listener */
  private final DatafeedLoop datafeedLoop;

  public ActivityRegistry(UserV2 botSession, DatafeedLoop datafeedLoop) {
    this.botSession = botSession;
    this.datafeedLoop = datafeedLoop;
  }

  /**
   * Registers an activity within the registry.
   *
   * @param activity An activity.
   */
  public void register(final AbstractActivity<?, ?> activity) {
    this.preProcessActivity(activity);
    this.activityList.add(activity);
  }

  public List<AbstractActivity<? ,?>> getActivityList() {
    return new ArrayList<>(activityList);
  }

  private void preProcessActivity(AbstractActivity<?, ?> activity) {

    // a command activity (potentially) needs the bot display name in order to parse the message text content
    // this way of passing this information is not very clean though, we should find something
    if (activity instanceof CommandActivity) {
      Optional<AbstractActivity<?, ?>> act = this.activityList.stream()
          .filter(a -> a.getInfo().type().equals(ActivityType.COMMAND)
              && a.getInfo().name() != null
              && a.getInfo().name().equals(activity.getInfo().name())
              && a.getInfo().requiresBotMention() == activity.getInfo().requiresBotMention())
          .findFirst();

      act.ifPresent(abstractActivity -> {
        abstractActivity.bindToRealTimeEventsSource(this.datafeedLoop::unsubscribe);
        this.activityList.remove(abstractActivity);
      });

      ((CommandActivity<?>) activity).setBotDisplayName(this.botSession.getDisplayName());
    }

    // make the activity to subscribe to its expected real-time event
    activity.bindToRealTimeEventsSource(this.datafeedLoop::subscribe);
  }
}
