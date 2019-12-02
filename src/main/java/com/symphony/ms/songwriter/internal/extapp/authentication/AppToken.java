package com.symphony.ms.songwriter.internal.extapp.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * App token
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppToken {
  private String appId;
  private String appToken;
  private String authToken;
  private String symphonyToken;

  public AppToken(String appId, String appToken, String symphonyToken) {
    this.appId = appId;
    this.appToken = appToken;
    this.symphonyToken = symphonyToken;
  }
}
