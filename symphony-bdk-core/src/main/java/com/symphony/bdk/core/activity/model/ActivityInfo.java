package com.symphony.bdk.core.activity.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

/**
 * {@link com.symphony.bdk.core.activity.AbstractActivity} information/documentation model.
 * <p>
 *   Note: this model is not complete enough yet, and must clearly provide a lot more features. Also, it would be nice
 *   to support templating for any text properties.
 * </p>
 */
@Getter
@Setter
@Accessors(fluent = true)
@API(status = API.Status.EXPERIMENTAL)
public class ActivityInfo {

  /** Type of the activity */
  private ActivityType type;

  /** Name of the activity (must be short) */
  private String name;

  /** Description of the activity (can contain multiple lines) */
  private String description;

}
