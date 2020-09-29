package com.symphony.bdk.core.activity.exception;

import com.symphony.bdk.core.activity.model.ActivityInfo;

import lombok.Getter;
import org.apiguardian.api.API;

/**
 * This exception can be triggered when a fatal error occurred during an activity execution flow.
 */
@API(status = API.Status.STABLE)
public class FatalActivityExecutionException extends RuntimeException {

  @Getter private final ActivityInfo activityInfo;

  /** {@inheritDoc} */
  public FatalActivityExecutionException(final ActivityInfo activityInfo, final String message, final Throwable cause) {
    super(message, cause);
    this.activityInfo = activityInfo;
  }
}
