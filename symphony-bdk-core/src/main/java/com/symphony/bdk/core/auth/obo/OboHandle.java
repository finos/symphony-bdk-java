package com.symphony.bdk.core.auth.obo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 *
 */
@Getter
@AllArgsConstructor
public class OboHandle {

  /**
   *
   */
  private final String username;

  /**
   *
   */
  private final Long userId;

  /**
   *
   * @return
   */
  public boolean hasUsername() {
    return this.username!= null;
  }
}
