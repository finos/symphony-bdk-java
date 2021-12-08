package com.symphony.bdk.core.service.message.util;

import org.apiguardian.api.API;

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

  public String getValue() {
    return value;
  }

}
