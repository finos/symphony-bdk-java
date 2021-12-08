package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;

@Getter
public class Mention {

  private String mentionText;
  private Long userId;

  public Mention(String mentionText, Long userId) {
    this.mentionText = mentionText;
    this.userId = userId;
  }
}
