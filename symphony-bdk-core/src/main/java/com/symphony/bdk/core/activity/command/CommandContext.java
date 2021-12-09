package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import java.util.HashMap;
import java.util.Map;

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
   * Potential arguments if command is matching. Key is argument name, value is actual argument value.
   * Actual type of value can be:
   * <ul>
   *   <li>{@link String}</li>
   *   <li>{@link com.symphony.bdk.core.activity.parsing.input.Mention}</li>
   *   <li>{@link com.symphony.bdk.core.activity.parsing.input.Cashtag}</li>
   *   <li>{@link com.symphony.bdk.core.activity.parsing.input.Hashtag}</li>
   * </ul>
   * depending on the command definition.
   */
  private final Map<String, Object> arguments;

  public CommandContext(V4Initiator initiator, V4MessageSent eventSource) {
    super(initiator, eventSource);
    this.streamId = eventSource.getMessage().getStream().getStreamId();
    this.messageId = eventSource.getMessage().getMessageId();
    this.arguments = new HashMap<>();
  }
}
