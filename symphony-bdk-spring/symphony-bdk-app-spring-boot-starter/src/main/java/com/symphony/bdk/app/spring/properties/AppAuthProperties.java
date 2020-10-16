package com.symphony.bdk.app.spring.properties;

import lombok.Getter;
import lombok.Setter;

/**
 * Configuration Properties for enabling Application Authentication APIs.
 */
@Getter
@Setter
public class AppAuthProperties {

  /**
   * The flag to enable the Circle of Trust authentication for extension application
   */
  private Boolean enabled = true;

  /**
   * The properties to configure the JWT cookie storage.
   */
  private JwtCookieProperties jwtCookie = new JwtCookieProperties();
}
