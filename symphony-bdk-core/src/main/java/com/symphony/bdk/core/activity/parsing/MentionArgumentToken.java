package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class MentionArgumentToken extends ArgumentCommandToken {

  public MentionArgumentToken(String pattern) {
    // pattern in the form {@argname}
    super(pattern);
    this.argumentName = this.argumentName.substring(1); // we have to remove the heading "@"
  }

  @Override
  public boolean matches(Object inputToken) {
    return inputToken instanceof Mention;
  }
}
