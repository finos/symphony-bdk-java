package com.symphony.bdk.examples.ai.context;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AskAiContext extends CommandContext {

  /**
   * The user's question, with the leading bot mention stripped out.
   */
  private String prompt;

  public AskAiContext(V4Initiator initiator, V4MessageSent eventSource) {
    super(initiator, eventSource);
  }
}
