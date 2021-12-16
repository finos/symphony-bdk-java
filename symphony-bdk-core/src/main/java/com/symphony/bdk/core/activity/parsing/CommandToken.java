package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

/**
 * Interface representing one token in a slash command pattern
 */
@API(status = API.Status.INTERNAL)
public interface CommandToken {

  /**
   *
   * @param inputToken the input token, can be of type {@link String}, {@link Mention}, {@link Cashtag} or {@link Hashtag}
   * @return true if input matches the token
   */
  boolean matches(Object inputToken);

  /**
   *
   * @return the actual type accepted by this token: {@link String}, {@link Mention}, {@link Cashtag} or {@link Hashtag}
   */
  Class<?> getTokenType();
}
