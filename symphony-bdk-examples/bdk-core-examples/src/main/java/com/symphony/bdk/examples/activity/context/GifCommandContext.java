package com.symphony.bdk.examples.activity.context;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GifCommandContext extends CommandContext {

  /**
   * Gif category to look for.
   */
  private String category;

  /** Default required constructor */
  public GifCommandContext(V4Initiator initiator, V4MessageSent eventSource) {
    super(initiator, eventSource);
  }
}
