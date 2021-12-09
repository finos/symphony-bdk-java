package com.symphony.bdk.core.activity.parsing.input;

import lombok.Getter;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
@Getter
public class Hashtag {

  private String text;
  private String value;

  public Hashtag(String text, String value) {
    this.text = text;
    this.value = value;
  }
}
