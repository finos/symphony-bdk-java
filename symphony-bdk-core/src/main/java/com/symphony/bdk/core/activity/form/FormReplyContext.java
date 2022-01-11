package com.symphony.bdk.core.activity.form;

import com.symphony.bdk.core.activity.ActivityContext;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

import javax.annotation.Nullable;

/**
 * Default implementation of the {@link ActivityContext} handled by the {@link FormReplyActivity}.
 */
@Getter
@Setter
@API(status = API.Status.STABLE)
public class FormReplyContext extends ActivityContext<V4SymphonyElementsAction> {

  /** The "form" messageId (different from the "reply" messageId) extracted from event source */
  private String formMessageId;

  /** The streamId extracted from event source */
  private String streamId;

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

  /**
   * Get the value of specified form field
   *
   * @param fieldName the form field name
   * @return the form field value. If the form reply does not contain the specified field, it returns null
   */
  @API(status = API.Status.STABLE)
  @Nullable
  public String getFormValue(String fieldName) {
    return this.formValues.has(fieldName) ?
        this.formValues.get(fieldName).asText() : null;
  }
}
