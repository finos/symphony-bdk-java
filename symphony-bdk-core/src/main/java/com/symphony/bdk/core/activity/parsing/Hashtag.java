package com.symphony.bdk.core.activity.parsing;

import lombok.Value;
import org.apiguardian.api.API;

/**
 * Class representing a hashtag in a V4Message
 */
@API(status = API.Status.STABLE)
@Value
public class Hashtag {

  /**
   * the text of the hashtag, e.g. "#tag"
   */
  String text;
  /**
   * the value of the hashtag, e.g. "tag"
   */
  String value;

  @Override
  public String toString() {
    return text;
  }
}
