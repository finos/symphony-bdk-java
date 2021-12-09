package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.activity.command.HelpCommand;
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
 * <p>
 * If an activity to be registered is already existing in the registry, then the old one will be replaced.
 * In case of an activity of type {@link SlashCommand}, it will replace the old one if this latter has the same name and both require bot mention (or both don't).
 * If the activity has /help as name, then it will replace {@link HelpCommand} if it is already registered.
 */
@Slf4j
@API(status = API.Status.STABLE)
public class ActivityRegistry {

  /**
   * List of activities
   */
  private final List<AbstractActivity<?, ?>> activityList = new ArrayList<>();

  /**
   * The bot session forwarded to command-based activities only
   */
  private final UserV2 botSession;

  /**
   * The Datafeed real-time events source, or Datafeed listener
   */
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

  public List<AbstractActivity<?, ?>> getActivityList() {
    return new ArrayList<>(activityList);
  }

  private void preProcessActivity(AbstractActivity<?, ?> activity) {

    Optional<AbstractActivity<?, ?>> act = this.activityList.stream()
        .filter(a -> a.equals(activity))
        .findFirst();

    act.ifPresent(abstractActivity -> {
      abstractActivity.bindToRealTimeEventsSource(this.datafeedLoop::unsubscribe);
      this.activityList.remove(abstractActivity);
      log.debug("One activity '{}' has been removed/unsubscribed in order to be replaced",
          abstractActivity.getInfo().name());
    });

    // a command activity (potentially) needs the bot display name in order to parse the message text content
    // this way of passing this information is not very clean though, we should find something
    if (activity instanceof CommandActivity) {
      ((CommandActivity<?>) activity).setBotDisplayName(this.botSession.getDisplayName());
      ((CommandActivity<?>) activity).setBotUserId(this.botSession.getId());
    }

    // make the activity to subscribe to its expected real-time event
    activity.bindToRealTimeEventsSource(this.datafeedLoop::subscribe);
  }
}

