package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.Objects;

@API(status = API.Status.INTERNAL)
@Getter
public class Mention {

  private String text;
  private String userDisplayName;
  private Long userId;

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
