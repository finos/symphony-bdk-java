package com.symphony.bdk.core.auth.jwt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Class to represent the claim "user" in the
 * <a href="https://developers.symphony.com/extension/docs/application-authentication#section-verifying-decoding-and-using-the-jwt">jwt</a>
 * used in extension app authentication.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UserClaim {
  private String id;
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
