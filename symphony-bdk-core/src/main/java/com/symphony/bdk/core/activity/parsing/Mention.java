package com.symphony.bdk.core.activity.parsing;

import lombok.Value;
import org.apiguardian.api.API;

/**
 * Class representing a mention in a {@link com.symphony.bdk.gen.api.model.V4Message}.
 */
@API(status = API.Status.STABLE)
@Value
public class Mention {

  /**
   * the text of a mention, e.g. "@John Doe"
   */
  String text;
  /**
   * the display name of the mentioned user, e.g. "John Doe"
   */
  String userDisplayName;
  /**
   * the user ID of the mentioned user
   */
  Long userId;

  /**
   *
   * @param text the text of a mention, e.g. "@John Doe"
   * @param userId the user ID of the mentioned user
   */
  public Mention(String text, Long userId) {
    this.text = text;
    this.userDisplayName = text.substring(1);
    this.userId = userId;
  }

  @Override
  public String toString() {
    return text;
  }
}
