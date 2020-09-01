package com.symphony.bdk.core.command;

import com.symphony.bdk.gen.api.model.V4Message;

/**
 * TODO: add description here
 */
public interface BotCommandMatcher {

  String match(V4Message incomingMessage);
}
