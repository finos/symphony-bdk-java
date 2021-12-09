package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class TypedArgumentToken<T> implements ArgumentCommandToken {

  private final Class<T> type;
  private final String argumentName;

  public static <T> TypedArgumentToken<T> newInstance(Class<T> type, String pattern) {
    return new TypedArgumentToken<>(type, pattern.substring(2, pattern.length() - 1));
  }

  private TypedArgumentToken(Class<T> type, String argumentName) {
    this.argumentName = argumentName;
    this.type = type;
  }

  @Override
  public String getArgumentName() {
    return argumentName;
  }

  @Override
  public boolean matches(Object inputToken) {
    return type.isAssignableFrom(inputToken.getClass());
  }
}
