package com.symphony.bdk.app.spring.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserId(Long userId) {

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public UserId(@JsonProperty("userId") Long userId) {
    this.userId = userId;
  }
}
