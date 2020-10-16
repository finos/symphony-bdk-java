package com.symphony.bdk.app.spring.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Id returned after verifying the {@link JwtInfo}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserId {

  private Long userId;
}
