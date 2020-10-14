package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration Properties for storing Jwt in cookie for the Extension App
 */
@Getter
@Setter
public class JwtCookieProperties {

  private Boolean enabled = false;
  private Integer maxAge = 24*60*60;
}
