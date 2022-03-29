package com.symphony.bdk.core.auth.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apiguardian.api.API;

/**
 * Class to represent the claim "user" in the
 * <a href="https://docs.developers.symphony.com/building-extension-applications-on-symphony/app-authentication/circle-of-trust-authentication#verifying-decoding-and-using-the-jwt">jwt</a>
 * used in extension app authentication.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@API(status = API.Status.STABLE)
public class UserClaim {
  private Long id;
  private String emailAddress;
  private String username;
  private String firstName;
  private String lastName;
  private String displayName;
  private String title;
  private String company;
  private String companyId;
  private String location;
  private String avatarUrl;
  private String avatarSmallUrl;
}
