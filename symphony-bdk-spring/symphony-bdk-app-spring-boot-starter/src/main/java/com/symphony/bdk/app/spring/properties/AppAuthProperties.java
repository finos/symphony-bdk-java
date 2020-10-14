package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration Properties for enabling Application Authentication APIs.
 */
@Getter
@Setter
public class AppAuthProperties {

  private Boolean enabled = true;
  private JwtCookieProperties jwtCookie = new JwtCookieProperties();
}
