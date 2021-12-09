package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

@API(status = API.Status.INTERNAL)
public class TypedArgumentToken<T> extends ArgumentCommandToken {

  private Class<T> type;

  public static <T> TypedArgumentToken<T> newInstance(Class<T> type, String pattern) {
    return new TypedArgumentToken<>(type, pattern.substring(2, pattern.length() - 1));
  }

  private TypedArgumentToken(Class<T> type, String pattern) {
    super(pattern);
    this.type = type;
  }

  @Override
  public boolean matches(Object inputToken) {
    return type.isAssignableFrom(inputToken.getClass());
  }
}
