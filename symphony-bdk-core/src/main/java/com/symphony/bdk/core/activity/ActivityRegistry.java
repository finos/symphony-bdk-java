package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.CommandActivityInfo;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.UserV2;

import lombok.Getter;
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
@API(status = API.Status.STABLE)
public class ActivityRegistry {

  /** List of activities */
  @Getter private final List<AbstractActivity<?, ?>> activityList = new ArrayList<>();

  /** The bot session forwarded to command-based activities only */
  private final UserV2 botSession;

  private final MessageService messageService;

  private SlashCommand helpCommand;

  /** The Datafeed real-time events source, or Datafeed listener */
  private final Consumer<RealTimeEventListener> realTimeEventsSource;

  public ActivityRegistry(UserV2 botSession, Consumer<RealTimeEventListener> realTimeEventsSource , MessageService messageService) {
    this.botSession = botSession;
    this.realTimeEventsSource = realTimeEventsSource;
    this.messageService = messageService;

    this.registerHelpCommand();
  }

  private void registerHelpCommand() {
    this.helpCommand = SlashCommand.slash("/help", commandContext -> {
      List<String> commands = new ArrayList<>();
      for (AbstractActivity<?, ?> activity : activityList) {
        ActivityInfo info = activity.getInfo();
        if (info instanceof CommandActivityInfo) {
          CommandActivityInfo commandInfo = (CommandActivityInfo) info;
          commands.add("<li>" + commandInfo.commandName() + ": " + commandInfo.summary() + "</li>");
        }
      }
      String message = "<ul>" + String.join("\n", commands) + "</ul>";
      this.messageService.send(commandContext.getStreamId(), Message.builder().content(message).build());
    }, "Bdk Help Command");
    this.register(this.helpCommand);
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
