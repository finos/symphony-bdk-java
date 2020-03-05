package com.symphony.bot.sdk.internal.extapp.authentication;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JWT info
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwtInfo {
  private String jwt;
}
