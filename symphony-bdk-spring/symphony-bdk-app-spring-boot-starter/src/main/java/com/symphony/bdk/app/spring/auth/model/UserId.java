package com.symphony.bdk.app.spring.auth.model;

import lombok.Data;

/**
 * User Id returned after verifying the {@link JwtInfo}.
 */
@Data
public class UserId {

  private Long userId;
}
