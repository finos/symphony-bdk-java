package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.regex.Pattern;

@API(status = API.Status.INTERNAL)
public class StringArgumentCommandToken implements RegexCommandToken, ArgumentCommandToken {

  public static final String ARGUMENT_VALUE_REGEX = "^\\S+$"; // at least one non-whitespace character
  private static final Pattern ARGUMENT_VALUE_PATTERN = Pattern.compile(ARGUMENT_VALUE_REGEX);

  protected String argumentName;

  /**
   * Creates new instance of ArgumentCommandToken with pattern in the format: "{argName}"
   * @param pattern the pattern in the format: "{argName}"
   * @return a new instance of an ArgumentCommandToken
   */
  public static StringArgumentCommandToken newInstance(String pattern) {
    return new StringArgumentCommandToken(pattern.substring(1, pattern.length() - 1));
  }

  protected StringArgumentCommandToken(String argumentName) {
    this.argumentName = argumentName;
  }

  @Override
  public Pattern getRegexPattern() {
    return ARGUMENT_VALUE_PATTERN;
  }

  public String getArgumentName() {
    return argumentName;
  }
}
