package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;

import org.apiguardian.api.API;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

/**
 * A help command listing all the commands that can be performed by an end-user through the chat.
 */
@API(status = API.Status.STABLE)
public class HelpCommand extends SlashCommand {

  private static final String HELP_COMMAND = "/help";
  private static final String DEFAULT_DESCRIPTION = "List available commands (mention required)";
  private final ActivityRegistry activityRegistry;
  private final MessageService messageService;

  public HelpCommand(@Nonnull ActivityRegistry activityRegistry, @Nonnull MessageService messageService) {
    super(HELP_COMMAND, true, c -> {}, DEFAULT_DESCRIPTION);
    this.activityRegistry = activityRegistry;
    this.messageService = messageService;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void onActivity(CommandContext context) {
    List<String> infos = this.activityRegistry.getActivityList()
        .stream()
        .map(AbstractActivity::getInfo)
        .filter(info -> info.type().equals(ActivityType.COMMAND))
        .map(info -> {
          String str = "<li>" + info.name() + "%s" + "</li>";
          return info.description().isEmpty() ? String.format(str, "") : String.format(str, " - " + info.description());
        })
        .collect(Collectors.toList());
    if (!infos.isEmpty()) {
      String message = "<ul>" + String.join("\n", infos) + "</ul>";
      this.messageService.send(context.getStreamId(), Message.builder().content(message).build());
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ActivityInfo info() {
    return new ActivityInfo()
        .type(ActivityType.COMMAND)
        .name(HELP_COMMAND)
        .description(DEFAULT_DESCRIPTION);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }

    if (o instanceof SlashCommand) {
      SlashCommand that = ((SlashCommand) o);
      return that.getInfo().name() != null && that.getInfo().name().equals(HELP_COMMAND);
    }

    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(activityRegistry, messageService);
  }
}
