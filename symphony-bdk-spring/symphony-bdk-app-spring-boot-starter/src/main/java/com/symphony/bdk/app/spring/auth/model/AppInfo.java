package com.symphony.bdk.app.spring.auth.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AppInfo {

  @NotBlank(message = "App Token is mandatory")
  private String appToken;
}
