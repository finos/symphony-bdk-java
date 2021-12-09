package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.Objects;

/**
 * Class representing a hashtag in a V4Message
 */
@API(status = API.Status.INTERNAL)
@Getter
public class Hashtag {

  private String text;
  private String value;

  /**
   *
   * @param text the text of the hashtag, e.g. "#tag"
   * @param value the value of the hashtag, e.g. "tag"
   */
  public Hashtag(String text, String value) {
    this.text = text;
    this.value = value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Hashtag hashtag = (Hashtag) o;
    return Objects.equals(text, hashtag.text) && Objects.equals(value, hashtag.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, value);
  }
}
