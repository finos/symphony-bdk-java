package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.regex.Pattern;

/**
 * CommandToken which matches a word in a {@link com.symphony.bdk.gen.api.model.V4Message} if it mathces a given regex.
 */
@API(status = API.Status.INTERNAL)
public interface RegexCommandToken extends CommandToken {

  /**
   *
   * @return the regex pattern the word must follow in order to match.
   */
  Pattern getRegexPattern();

  default boolean matches(Object inputToken) {
    return getRegexPattern().matcher(inputToken.toString()).matches();
  }
}
