package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.Objects;

/**
 * Class representing a mention in a {@link com.symphony.bdk.gen.api.model.V4Message}.
 */
@API(status = API.Status.INTERNAL)
@Getter
public class Mention {

  /**
   * the text of a mention, e.g. "@John Doe"
   */
  private String text;
  /**
   * the display name of the mentioned user, e.g. "John Doe"
   */
  private String userDisplayName;
  /**
   * the user ID of the mentioned user
   */
  private Long userId;

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Mention mention = (Mention) o;
    return Objects.equals(text, mention.text) && Objects.equals(userDisplayName, mention.userDisplayName)
        && Objects.equals(userId, mention.userId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(text, userDisplayName, userId);
  }
}
