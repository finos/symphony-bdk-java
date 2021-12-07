package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.regex.Pattern;

@API(status = API.Status.INTERNAL)
public class ArgumentCommandToken implements CommandToken {

  public static final String ARGUMENT_VALUE_REGEX = "^\\S+$"; // at least one non-whitespace character

  private final Pattern pattern;
  private final String argumentName;

  public ArgumentCommandToken(String pattern) {
    this.pattern = Pattern.compile(ARGUMENT_VALUE_REGEX);
    this.argumentName = pattern.substring(1, pattern.length() - 1);
  }

  @Override
  public Pattern getRegexPattern() {
    return pattern;
  }

  public String getArgumentName() {
    return argumentName;
  }
}
