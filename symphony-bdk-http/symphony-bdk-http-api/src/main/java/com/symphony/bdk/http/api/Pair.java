package com.symphony.bdk.http.api;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apiguardian.api.API;

/**
 * Pair of string values. Used by generated code only.
 */
@Getter
@EqualsAndHashCode
@API(status = API.Status.INTERNAL)
public class Pair {

  private String name = "";
  private String value = "";

  public Pair(String name, String value) {
    this.setName(name);
    this.setValue(value);
  }

  public static Pair pair(String name, String value) {
    return new Pair(name, value);
  }

  private void setName(String name) {
    if (isInvalidString(name)) {
      return;
    }

    this.name = name;
  }

  private void setValue(String value) {
    if (isInvalidString(value)) {
      return;
    }

    this.value = value;
  }

  private static boolean isInvalidString(String arg) {
    return arg == null || arg.trim().isEmpty();
  }
}
