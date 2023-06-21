package com.symphony.bdk.app.spring.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * JSON Web Token used for verifying the user of the Extension App
 */
public record JwtInfo(@NotBlank(message = "Jwt is mandatory") String jwt) {

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public JwtInfo(@JsonProperty("jwt") String jwt) {
    this.jwt = jwt;
  }
}
