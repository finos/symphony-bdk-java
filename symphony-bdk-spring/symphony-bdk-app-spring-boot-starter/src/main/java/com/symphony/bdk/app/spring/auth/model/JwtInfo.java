package com.symphony.bdk.app.spring.auth.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * JSON Web Token used for verifying the user of the Extension App
 */
@Data
public class JwtInfo {

  @NotBlank(message = "Jwt is mandatory")
  private final String jwt;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public JwtInfo(@JsonProperty("jwt") String jwt) {
    this.jwt = jwt;
  }
}
