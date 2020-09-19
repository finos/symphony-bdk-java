package com.symphony.bdk.core.activity;

import org.apiguardian.api.API;

/**
 * An activity matcher allows to check if an activity business logic has to be triggered or not.
 */
@FunctionalInterface
@API(status = API.Status.STABLE)
public interface ActivityMatcher<C extends ActivityContext<?>> {

  /**
   * Matches the {@link ActivityContext} to decide whether an {@link AbstractActivity} can be executed or not.
   *
   * @param context Current activity context.
   * @return true if {@link AbstractActivity#onActivity(ActivityContext)} can be triggered, false otherwise.
   */
  boolean matches(C context);

  /**
   * Returns a matcher that always returns true.
   *
   * @param <C> the type of the
   * @return a matcher that always returns true.
   */
  static <C extends ActivityContext<?>> ActivityMatcher<C> always() {
    return context -> true;
  }
}
