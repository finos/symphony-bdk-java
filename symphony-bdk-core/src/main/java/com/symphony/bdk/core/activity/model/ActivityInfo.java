package com.symphony.bdk.core.activity.model;

import lombok.Getter;
import lombok.Setter;
import org.apiguardian.api.API;

/**
 * {@link com.symphony.bdk.core.activity.Activity} information/documentation model.
 * <p>
 *   Note: this model is not complete enough yet, and must clearly provide a lot more features. Also, it would be nice
 *   to support templating for any text properties.
 * </p>
 */
@Getter
@Setter
@API(status = API.Status.EXPERIMENTAL)
public class ActivityInfo {

  /** Type of the activity */
  private ActivityType type;

  /** Name of the activity (must be short) */
  private String name;

  /** Description of the activity (can contain multiple lines) */
  private String description;

  /**
   * Convenient helper method for creating an {@link ActivityInfo} with a given {@link ActivityType}.
   *
   * @param type Type of the activity
   * @return a new {@link ActivityInfo} instance
   */
  public static ActivityInfo of(ActivityType type) {
    final ActivityInfo newInfo = new ActivityInfo();
    newInfo.setType(type);
    return newInfo;
  }
}
