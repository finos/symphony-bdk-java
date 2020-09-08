package com.symphony.bdk.examples.activity.context;

import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GifFormReplyContext extends FormReplyContext {

  /**
   * Gif category to look for.
   */
  private String category;

  /** Default required constructor */
  public GifFormReplyContext(V4Initiator initiator, V4SymphonyElementsAction eventSource) {
    super(initiator, eventSource);
  }
}
