package com.symphony.bdk.core.activity.parsing;

import lombok.Value;
import org.apiguardian.api.API;

/**
 * Class representing a cashtag in a V4Message
 */
@API(status = API.Status.INTERNAL)
@Value
public class Cashtag {

  /**
   * text of the cashtag, e.g. "$tag"
   */
  String text;
  /**
   * value of the cashtag, e.g. "tag"
   */
  String value;

  @Override
  public String toString() {
    return text;
  }
}
