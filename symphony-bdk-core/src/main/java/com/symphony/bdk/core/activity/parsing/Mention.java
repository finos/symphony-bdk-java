package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;

@Getter
public class Mention {

  private String text;
  private String userDisplayName;
  private Long userId;

  public Mention(String text, Long userId) {
    this.text = text;
    this.userDisplayName = text.substring(1);
    this.userId = userId;
  }
}
