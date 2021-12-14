package com.symphony.bdk.core.service.message.util;

import com.symphony.bdk.gen.api.model.V4Message;

import org.apiguardian.api.API;

/**
 * Enum representing the different entity types in the {@link V4Message#getData()}.
 */
@API(status = API.Status.INTERNAL)
public enum EntityTypeEnum {
  HASHTAG("org.symphonyoss.taxonomy"),
  CASHTAG("org.symphonyoss.fin.security"),
  MENTION("com.symphony.user.mention"),
  EMOJI("com.symphony.emoji");

  private final String value;

  EntityTypeEnum(String value) {
    this.value = value;
  }

  /**
   *
   * @return the entity value
   */
  public String getValue() {
    return value;
  }

}
