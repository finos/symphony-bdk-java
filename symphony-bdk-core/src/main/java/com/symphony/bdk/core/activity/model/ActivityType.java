package com.symphony.bdk.core.activity.model;

import org.apiguardian.api.API;

/**
 * Type of an activity.
 */
@API(status = API.Status.EXPERIMENTAL)
public enum ActivityType {
  /**
   * Message sent in the chat.
   */
  COMMAND,
  /**
   * Form submitted.
   */
  FORM,
  /**
   * Room created.
   */
  ROOM_CREATED
}
