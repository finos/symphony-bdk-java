package com.symphony.ms.songwriter.internal.notification.model;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Notification request
 *
 * @author Marcus Secato
 *
 */
@Data
public class NotificationRequest {

  private Map<String, String> headers;
  private String payload;
  private String identifier;
  private String streamId;
  private Map<String, Object> attributes;

  public NotificationRequest(Map<String, String> headers,
      String payload, String identifier) {
    this.headers = headers;
    this.payload = payload;
    this.identifier = identifier;
    this.streamId = identifier;
    this.attributes = new HashMap<>();
  }

}
