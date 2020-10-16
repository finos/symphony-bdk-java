package com.symphony.bdk.app.spring.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * User Id returned after verifying the {@link JwtInfo}.
 */
@Data
public class UserId {

  private final Long userId;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public UserId(@JsonProperty("userId") Long userId) {
    this.userId = userId;
  }
}
