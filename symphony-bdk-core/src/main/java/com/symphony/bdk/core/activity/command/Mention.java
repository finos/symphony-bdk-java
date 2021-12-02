package com.symphony.bdk.core.activity.command;

import lombok.Getter;

@Getter
public class Mention {
  private String text;
  private String userDisplayName;
  private long userId;

  public Mention(String text, long userId) {
    this.text = text;
    this.userDisplayName = text.substring(1);
    this.userId = userId;
  }
}
