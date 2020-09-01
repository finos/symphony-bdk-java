package com.symphony.bdk.core.command;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TODO: add description here
 */
@Slf4j
public class BotCommandRegistry implements RealTimeEventListener {

  private final List<BotCommand> commands = new ArrayList<>();

  public <T extends BotCommand> void register(T... commands) {
    this.commands.addAll(Arrays.asList(commands));
  }

  @Override
  public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
    log.info("on event : {}", event.getMessage().getMessage());

    for (BotCommand command : this.commands) {
      final String commandContent = command.matcher().match(event.getMessage());

      if (commandContent != null) {
        command.onCommand(new BotCommandContext(event, commandContent));
      }
    }
  }
}
