package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;

@Getter
public class Hashtag {

  private String text;
  private String value;

  public Hashtag(String text, String value) {
    this.text = text;
    this.value = value;
  }
}
