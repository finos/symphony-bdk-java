package com.symphony.bdk.core.activity.command;

import java.util.regex.Pattern;

public class CommandToken {
  private Pattern pattern;

  public CommandToken(String pattern) {
    this.pattern = Pattern.compile(pattern);
  }

  public Pattern getRegexPattern() {
    return pattern;
  }

  public boolean matches(String inputToken) {
    return pattern.matcher(inputToken).matches();
  }
}
