package com.symphony.bdk.app.spring.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class TokenPair {

  @NotBlank(message = "App Token is mandatory")
  private String appToken;
  @NotBlank(message = "Symphony Token is mandatory")
  private String symphonyToken;
}
