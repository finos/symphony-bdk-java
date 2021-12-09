package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.Objects;

/**
 * Class representing a cashtag in a V4Message
 */
@API(status = API.Status.INTERNAL)
@Getter
public class Cashtag {

  private String text;
  private String value;

  /**
   *
   * @param text text of the cashtag, e.g. "$tag"
   * @param value value of the cashtag, e.g. "tag"
   */
  public Cashtag(String text, String value) {
    this.text = text;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Cashtag cashtag = (Cashtag) o;
    return Objects.equals(text, cashtag.text) && Objects.equals(value, cashtag.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, value);
  }
}
