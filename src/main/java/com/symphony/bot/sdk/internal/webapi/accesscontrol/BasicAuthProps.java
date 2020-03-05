package com.symphony.bot.sdk.internal.webapi.accesscontrol;

import java.util.List;
import java.util.Set;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds basic authentication and IP whitelisting details
 *
 * @author Marcus Secato
 *
 */
@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "access-control")
public class BasicAuthProps {
  private String name;
  private String hashedPassword;
  private String salt;
  private Set<String> ipWhitelist;
  private List<String> urlMapping;

  public boolean isBasicAuth() {
    boolean isBasicAuth = false;
    if ((name != null && !name.isEmpty())
        && (hashedPassword != null && !hashedPassword.isEmpty())
        && (salt != null && !salt.isEmpty())
        && (urlMapping != null && !urlMapping.isEmpty())) {
      isBasicAuth = true;
    }
    return isBasicAuth;
  }

  public boolean isIpWhitelist() {
    boolean isIpWhitelist = false;
    if ((ipWhitelist != null && !ipWhitelist.isEmpty())
        && (urlMapping != null && !urlMapping.isEmpty())) {
      isIpWhitelist = true;
    }
    return isIpWhitelist;
  }

}
