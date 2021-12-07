package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.regex.Pattern;

@API(status = API.Status.INTERNAL)
public class StaticCommandToken implements CommandToken {
  private Pattern pattern;

  /**
   *
   * @param pattern the string to be exactly matched. Begin and end anchors will be added to construct the regex pattern.
   */
  public StaticCommandToken(String pattern) {
    this.pattern = Pattern.compile("^" + pattern + "$");
  }

  public Pattern getRegexPattern() {
    return pattern;
  }
}
