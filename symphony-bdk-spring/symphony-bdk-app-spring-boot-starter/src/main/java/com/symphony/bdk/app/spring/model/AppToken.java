package com.symphony.bdk.app.spring.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppToken {

  private String appToken;
  private String symphonyToken;
}
