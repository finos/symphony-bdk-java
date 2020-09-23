package com.symphony.bdk.http.api.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Represents a response entity type reference {@code T}.
 * Supports in-line instantiation of objects that represent generic types with
 * actual type parameters. An object that represents any parameterized type may
 * be obtained by sub-classing {@code GenericClass}.
 *
 * @param <T> the type reference parameter.
 */
public abstract class TypeReference<T> {

  private final Type type;

  public TypeReference() {
    this.type = ((ParameterizedType)getClass()
            .getGenericSuperclass())
            .getActualTypeArguments()[0];
  }

  /**
   * Get type represented by the type reference instance.
   *
   * @return {@link Type} represented by the type reference instance.
   */
  public Type getType() {
    return this.type;
  }
}
