package com.symphony.bdk.core.auth.obo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apiguardian.api.API;

/**
 *
 */
@API(status = API.Status.EXPERIMENTAL)
public class Obo {

  /**
   *
   * @param username
   * @return
   */
  public static Handle username(String username) {
    return new Handle(username, null);
  }

  /**
   *
   * @param userId
   * @return
   */
  public static Handle userId(long userId) {
    return new Handle(null, userId);
  }

  /**
   *
   */
  @Getter
  @AllArgsConstructor
  @API(status = API.Status.EXPERIMENTAL)
  public static class Handle {

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
}
