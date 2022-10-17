package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.Map;

/**
 * Class representing the outcome of a matching between a {@link SlashCommandPattern} and a {@link com.symphony.bdk.gen.api.model.V4Message}.
 * It can contain the map of arguments if applicable. Key is argument name, value is the actual value in the message.
 * Argument value can be of type {@link String}, {@link Mention}, {@link Cashtag} or {@link Hashtag}.
 */
@API(status = API.Status.INTERNAL)
@Getter
public class MatchResult {

  private boolean isMatching;
  private Arguments arguments;

  /**
   *
   * @param isMatching whether the message is matching
   */
  public MatchResult(boolean isMatching) {
    this(isMatching, Collections.emptyMap());
  }

  /**
   *
   * @param isMatching whether the message is matching
   * @param arguments the map of arguments
   */
  public MatchResult(boolean isMatching, Map<String, Object> arguments) {
    this.isMatching = isMatching;
    this.arguments = new Arguments(arguments);
  }
}
