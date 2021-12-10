package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Class storing arguments matching a {@link SlashCommandPattern}
 */
@API(status = API.Status.STABLE)
public class Arguments {

  private Map<String, Object> arguments;

  public Arguments() {
    this.arguments = Collections.emptyMap();
  }

  public Arguments(Map<String, Object> arguments) {
    this.arguments = arguments;
  }

  /**
   *
   * @return the set of argument names
   */
  public Set<String> getArgumentNames() {
    return arguments.keySet();
  }

  /**
   *
   * @param argumentName the name of the argument to be retrieved
   * @return the argument value if present, null otherwise. Actual type of value can be {@link String}, {@link Mention},
   * {@link Cashtag}, {@link Hashtag} depending on the corresponding {@link SlashCommandPattern}.
   */
  public Object get(String argumentName) {
    return arguments.get(argumentName);
  }

  /**
   *
   * @param argumentName the name of the argument to be retrieved
   * @return the string representation of the argument if present, null otherwise
   */
  public String getAsString(String argumentName) {
    final Object argumentValue = arguments.get(argumentName);
    return argumentValue == null ? null : argumentValue.toString();
  }

  /**
   *
   * @param argumentName the name of the argument to be retrieved
   * @return the argument value if present and if is of type {@link String}, null otherwise
   */
  public String getString(String argumentName) {
    return get(String.class, argumentName);
  }


  /**
   *
   * @param argumentName the name of the argument to be retrieved
   * @return the argument value if present and if is of type {@link Hashtag}, null otherwise
   */
  public Hashtag getHashtag(String argumentName) {
    return get(Hashtag.class, argumentName);
  }

  /**
   *
   * @param argumentName the name of the argument to be retrieved
   * @return the argument value if present and if is of type {@link Cashtag}, null otherwise
   */
  public Cashtag getCashtag(String argumentName) {
    return get(Cashtag.class, argumentName);
  }

  /**
   *
   * @param argumentName the name of the argument to be retrieved
   * @return the argument value if present and if is of type {@link Mention}, null otherwise
   */
  public Mention getMention(String argumentName) {
    return get(Mention.class, argumentName);
  }

  private <T> T get(Class<T> clazz, String argumentName) {
    final Object argumentValue = arguments.get(argumentName);

    if (argumentValue != null && clazz.isAssignableFrom(argumentValue.getClass())) {
      return clazz.cast(argumentValue);
    }
    return null;
  }
}
