package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.User;

/**
 * Symphony user details
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
public class UserDetails {

  private String userId;
  private String email;
  private String firstName;
  private String lastName;
  private String displayName;
  private String username;

  public UserDetails(User user) {
    this.userId = user.getUserId().toString();
    this.email = user.getEmail();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.displayName = user.getDisplayName();
    this.username = user.getUsername();
  }

}
