package com.symphony.bdk.core.command;

import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * TODO: add description here
 */
@RequiredArgsConstructor
public class BotCommandContext {

  @Getter private final V4MessageSent eventSource;

  @Getter private final String commandContent;
}
