package com.symphony.bdk.core.service.datafeed.util;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.gen.api.model.V4UserJoinedRoom;

import org.apiguardian.api.API;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

/**
 * Utility class used to attach a method call (defined by a {@link BiConsumer}) to a specific real-time event.
 */
@API(status = API.Status.INTERNAL)
public class RealTimeEventsBinder {

  public RealTimeEventsBinder() {
    // nothing to be done here
  }

  /**
   * Bind "onMessageSent" real-time event to a target method.
   *
   * @param subscriber The Datafeed real-time events subscriber.
   * @param target     Target method.
   * @param activity   The activity that creates the listener. It can be null, but in that case it will not be possible
   *                   to unsubscribe the activity later.
   */
  public static void bindOnMessageSent(Consumer<RealTimeEventListener> subscriber,
      BiConsumer<V4Initiator, V4MessageSent> target, @Nullable AbstractActivity<V4MessageSent, ?> activity) {
    subscriber.accept(new OnMessageSent(activity, target));
  }

  /**
   * Bind "onSymphonyElementsAction" real-time event to a target method.
   *
   * @param subscriber The Datafeed real-time events subscriber.
   * @param target     Target method.
   * @param activity   The activity that creates the listener. It can be null, but in that case it will not be possible
   *                   to unsubscribe the activity later.
   */
  public static void bindOnSymphonyElementsAction(Consumer<RealTimeEventListener> subscriber,
      BiConsumer<V4Initiator, V4SymphonyElementsAction> target,
      @Nullable AbstractActivity<V4SymphonyElementsAction, ?> activity) {
    subscriber.accept(new OnSymphonyElementsAction(activity, target));
  }

  /**
   * Bind "onUserJoinedRoom" real-time event to a target method.
   *
   * @param subscriber The Datafeed real-time events subscriber.
   * @param target     Target method.
   * @param activity   The activity that creates the listener. It can be null, but in that case it will not be possible
   *                   to unsubscribe the activity later.
   */
  public static void bindOnUserJoinedRoom(Consumer<RealTimeEventListener> subscriber,
      BiConsumer<V4Initiator, V4UserJoinedRoom> target, @Nullable AbstractActivity<V4UserJoinedRoom, ?> activity) {
    subscriber.accept(new OnUserJoinedRoom(activity, target));
  }

  /**
   * Bind a {@link RealTimeEventListener} to datafeed loop method.
   *
   * @param consumer Datafeed Loop subscribe/unsubscribe method.
   * @param listener RealTime event listener.
   */
  public static void bindRealTimeListener(Consumer<RealTimeEventListener> consumer, RealTimeEventListener listener) {
    consumer.accept(listener);
  }

  /**
   * Internal private records used to create listeners from an {@link AbstractActivity}.
   * They keep a reference to the source {@link AbstractActivity}, which is used only for equality validation.
   * This ensures that multiple identical listeners from the same {@link AbstractActivity} are not registered,
   * and also allows the {@link AbstractActivity} to be unsubscribed later.
   */

  private record OnMessageSent(AbstractActivity<V4MessageSent, ?> activity,
                               BiConsumer<V4Initiator, V4MessageSent> target)
      implements RealTimeEventListener {

    @Override
    public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
      target.accept(initiator, event);
    }

    @Override
    public boolean equals(Object o) {
      // If the listener is created with a null activity, there is no way to identify it later.
      // For this reason, equals will always return false.
      if (!(o instanceof OnMessageSent that) || this.activity == null) {return false;}
      return activity.equals(that.activity);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(activity);
    }
  }


  private record OnSymphonyElementsAction(AbstractActivity<V4SymphonyElementsAction, ?> activity,
                                          BiConsumer<V4Initiator, V4SymphonyElementsAction> target)
      implements RealTimeEventListener {

    @Override
    public void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {
      target.accept(initiator, event);
    }

    @Override
    public boolean equals(Object o) {
      // If the listener is created with a null activity, there is no way to identify it later.
      // For this reason, equals will always return false.
      if (!(o instanceof OnSymphonyElementsAction that) || this.activity == null) {return false;}
      return activity.equals(that.activity);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(activity);
    }
  }


  private record OnUserJoinedRoom(AbstractActivity<V4UserJoinedRoom, ?> activity,
                                  BiConsumer<V4Initiator, V4UserJoinedRoom> target) implements RealTimeEventListener {

    @Override
    public void onUserJoinedRoom(V4Initiator initiator, V4UserJoinedRoom event) {
      target.accept(initiator, event);
    }

    @Override
    public boolean equals(Object o) {
      // If the listener is created with a null activity, there is no way to identify it later.
      // For this reason, equals will always return false.
      if (!(o instanceof OnUserJoinedRoom that) || this.activity == null) {return false;}
      return activity.equals(that.activity);
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(activity);
    }
  }
}
