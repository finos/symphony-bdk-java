package com.symphony.ms.songwriter.internal.lib.webapi.appauth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtInfo {
  private String jwt;
}
