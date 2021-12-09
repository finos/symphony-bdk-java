package com.symphony.bdk.core.activity.parsing.input;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class StringInputToken implements InputToken<String> {

  private String content;

  public StringInputToken(String content) {
    this.content = content;
  }

  public String getContent() {
    return content;
  }

  public String getContentAsString() {
    return content;
  }
}
