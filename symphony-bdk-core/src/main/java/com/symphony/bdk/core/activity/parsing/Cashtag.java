package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
@Getter
public class Cashtag {

  private String text;
  private String value;

  public Cashtag(String text, String value) {
    this.text = text;
    this.value = value;
  }
}
