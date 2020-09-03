package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import org.apiguardian.api.API;

import java.util.function.Consumer;

/**
 * Contract definition for an Activity that can be registered via the {@link ActivityRegistry}.
 * <p>
 *   An activity is an abstraction over the Datafeed real-times events focused on action that can be triggered in the
 *   chat by an end-user.
 * </p>
 * <p>
 *   At the moment, 2 different kind of activities are provided by the Core layer:
 *   <ul>
 *     <li>the command activity (refer to {@link CommandActivity})</li>
 *     <li>the form activity (refer to {@link FormReplyActivity})</li>
 *   </ul>
 * </p>
 */
@API(status = API.Status.STABLE)
public interface Activity<C extends ActivityContext<?>> {

  /**
   * Attaches an Activity to its dedicated real-time event.
   *
   * @param subscriber The real-time event subscriber, issued from the {@link com.symphony.bdk.core.service.datafeed.DatafeedService}.
   */
  void subscribe(Consumer<RealTimeEventListener> subscriber);

  /**
   * Any kind of activity must provide an {@link ActivityMatcher} in order to detect if it can be applied to a certain
   * user input.
   *
   * @return an {@link ActivityMatcher} implementation.
   */
  ActivityMatcher<C> matcher();

  /**
   * Contain the activity business logic. Executed only if the {@link ActivityMatcher#matches(ActivityContext)} retured
   * a true value.
   *
   * @param context The activity context object.
   */
  void onActivity(C context);

  /**
   * Retrieve activity information.
   *
   * @return activity info
   */
  @API(status = API.Status.EXPERIMENTAL)
  ActivityInfo getInfo();
}
