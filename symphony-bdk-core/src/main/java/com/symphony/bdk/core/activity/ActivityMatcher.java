package com.symphony.bdk.core.activity;

import org.apiguardian.api.API;

/**
 * An activity matcher allows to check if an activity business logic has to be triggered or not.
 */
@FunctionalInterface
@API(status = API.Status.STABLE)
public interface ActivityMatcher<C extends ActivityContext<?>> {

  /**
   * Matches the {@link ActivityContext} to decide whether an {@link Activity} can be executed or not.
   *
   * @param context Current activity context.
   * @return true if {@link Activity#onActivity(ActivityContext)} can be triggered, false otherwise.
   */
  boolean matches(C context);
}
