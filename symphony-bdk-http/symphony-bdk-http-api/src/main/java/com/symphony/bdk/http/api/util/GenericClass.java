package com.symphony.bdk.http.api.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class GenericClass<T> {

  private final Type type;

  public GenericClass() {
    this.type = ((ParameterizedType)getClass()
            .getGenericSuperclass())
            .getActualTypeArguments()[0];
  }

  public Type getType() {
    return this.type;
  }
}
