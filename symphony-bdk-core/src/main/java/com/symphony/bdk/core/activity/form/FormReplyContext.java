package com.symphony.bdk.core.activity.form;

import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

/**
 * Default implementation of the {@link ActivityContext} handled by the {@link FormReplyActivity}.
 */
@Getter
@Setter
@API(status = API.Status.STABLE)
public class FormReplyContext extends ActivityContext<V4SymphonyElementsAction> {

  /** The formId extracted from event source */
  private String formId;

  /** Form values as a JsonNode */
  private JsonNode formValues;

  /**
   * Default constructor matching super.
   *
   * @param initiator Activity initiator.
   * @param eventSource Event source of the activity.
   */
  public FormReplyContext(V4Initiator initiator, V4SymphonyElementsAction eventSource) {
    super(initiator, eventSource);
  }

  @API(status = API.Status.EXPERIMENTAL)
  public String getFormValue(String fieldName) {
    return this.formValues.has(fieldName) ?
        this.formValues.get(fieldName).asText() : null;
  }
}
