package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.regex.Pattern;

@API(status = API.Status.INTERNAL)
public interface CommandToken {

  Pattern getRegexPattern();

  default boolean matches(String inputToken) {
    return getRegexPattern().matcher(inputToken).matches();
  }
}
