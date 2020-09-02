package com.symphony.bdk.core.service.datafeed.util;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Utility class used to attach a method call (defined by a {@link BiConsumer}) to a specific real-time event.
 */
public class RealTimeEventsBinder {

  /**
   * Bind "onMessageSent" real-time event to a target method.
   *
   * @param subscriber The Datafeed real-time events subscriber.
   * @param target Target method.
   */
  public static void bindOnMessageSent(Consumer<RealTimeEventListener> subscriber, BiConsumer<V4Initiator, V4MessageSent> target) {
    subscriber.accept(new RealTimeEventListener() {

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        target.accept(initiator, event);
      }
    });
  }

  /**
   * Bind "onSymphonyElementsAction" real-time event to a target method.
   *
   * @param subscriber The Datafeed real-time events subscriber.
   * @param target Target method.
   */
  public static void bindOnSymphonyElementsAction(Consumer<RealTimeEventListener> subscriber, BiConsumer<V4Initiator, V4SymphonyElementsAction> target) {
    subscriber.accept(new RealTimeEventListener() {

      @Override
      public void onSymphonyElementsAction(V4Initiator initiator, V4SymphonyElementsAction event) {
        target.accept(initiator, event);
      }
    });
  }
}
