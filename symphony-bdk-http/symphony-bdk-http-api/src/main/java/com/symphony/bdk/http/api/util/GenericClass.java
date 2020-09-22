package com.symphony.bdk.http.api.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Represents a generic response entity type {@code T}.
 * Supports in-line instantiation of objects that represent generic types with
 * actual type parameters. An object that represents any parameterized type may
 * be obtained by sub-classing {@code GenericClass}.
 *
 * @param <T> the generic type parameter.
 */
public abstract class GenericClass<T> {

  private final Type type;

  public GenericClass() {
    this.type = ((ParameterizedType)getClass()
            .getGenericSuperclass())
            .getActualTypeArguments()[0];
  }

  /**
   * Get type represented by the generic type instance.
   *
   * @return {@link Type} represented by the generic type instance.
   */
  public Type getType() {
    return this.type;
  }
}
