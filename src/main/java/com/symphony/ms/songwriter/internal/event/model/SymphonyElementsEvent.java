package com.symphony.ms.songwriter.internal.event.model;

import java.util.Map;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;
import model.events.SymphonyElementsAction;

/**
 * Symphony elements event
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
public class SymphonyElementsEvent extends BaseEvent {

  private String streamType;
  private String formId;
  private Map<String, Object> formValues;
  private String userDisplayName;

  public SymphonyElementsEvent(User initiator, SymphonyElementsAction action) {
    this.streamId = action.getStreamId();
    this.streamType = action.getStreamType();
    this.formId = action.getFormId();
    this.formValues = action.getFormValues();
    this.userId = initiator.getUserId().toString();
    this.userDisplayName = initiator.getDisplayName();
  }

}
