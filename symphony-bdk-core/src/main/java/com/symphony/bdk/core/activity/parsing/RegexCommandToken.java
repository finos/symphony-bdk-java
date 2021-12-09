package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.regex.Pattern;

@API(status = API.Status.INTERNAL)
public interface RegexCommandToken extends CommandToken {
  Pattern getRegexPattern();

  default boolean matches(Object inputToken) {
    return getRegexPattern().matcher(inputToken.toString()).matches();
  }
}
