package com.symphony.bdk.app.spring.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * JSON Web Token used for verifying the user of the Extension App
 */
@Data
@NoArgsConstructor
public class JwtInfo {

  @NotBlank(message = "Jwt is mandatory")
  private String jwt;
}
