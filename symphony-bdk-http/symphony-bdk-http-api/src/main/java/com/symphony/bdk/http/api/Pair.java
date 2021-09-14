package com.symphony.bdk.http.api;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.List;

/**
 * Pair of string values. Used by generated code only.
 */
@Getter
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

  public static List<Pair> pairs(String... pairs) {

    if (pairs.length % 2 != 0) {
      throw new IllegalArgumentException("Length of arguments should be a multiple of 2.");
    }

    List<Pair> result = new ArrayList<>();
    for (int i = 0; i < pairs.length; i+=2) {
      result.add(pair(pairs[i], pairs[i + 1]));
    }
    return result;
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
