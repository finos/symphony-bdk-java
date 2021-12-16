package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.core.activity.parsing.Arguments;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

/**
 * Default implementation of the {@link ActivityContext} handled by the {@link CommandActivity}.
 */
@Getter
@Setter
@API(status = API.Status.STABLE)
public class CommandContext extends ActivityContext<V4MessageSent> {

  /** Raw text content of the user command */
  private String textContent;

  /** Shortcut to the command streamId value issued form the {@link V4MessageSent} event source */
  private final String streamId;

  /** Shortcut to the command messageId value issued form the {@link V4MessageSent} event source */
  private final String messageId;

  /**
   * Potential arguments if command is matching.
   */
  private Arguments arguments;

  public CommandContext(V4Initiator initiator, V4MessageSent eventSource) {
    super(initiator, eventSource);
    this.streamId = eventSource.getMessage().getStream().getStreamId();
    this.messageId = eventSource.getMessage().getMessageId();
    this.arguments = new Arguments();
  }
}
