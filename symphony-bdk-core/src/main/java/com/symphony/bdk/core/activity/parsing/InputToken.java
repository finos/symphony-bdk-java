package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class InputToken {

  @Getter
  private String content;

  private boolean isMention;

  public InputToken(String content) {
    this(content, false);
  }

  public InputToken(String content, boolean isMention) {
    this.content = content;
    this.isMention = isMention;
  }

  public boolean isMention() {
    return isMention;
  }
}
