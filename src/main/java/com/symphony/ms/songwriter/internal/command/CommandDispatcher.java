package com.symphony.ms.songwriter.internal.command;

import com.symphony.ms.songwriter.internal.event.model.MessageEvent;

public interface CommandDispatcher {

  void register(String channel, CommandHandler handler);

  void push(String channel, MessageEvent command);

}
