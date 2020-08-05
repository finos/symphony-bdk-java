package com.symphony.bdk.core.auth.obo;

/**
 *
 */
public class Obo {

  /**
   *
   * @param username
   * @return
   */
  public static OboHandle username(String username) {
    return new OboHandle(username, null);
  }

  /**
   *
   * @param userId
   * @return
   */
  public static OboHandle userId(long userId) {
    return new OboHandle(null, userId);
  }
}
