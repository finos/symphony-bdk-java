package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtCookieProperties {

  private Boolean enabled = false;
  private Integer maxAge = 24*60*60;
}
