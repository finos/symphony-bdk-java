package com.symphony.bdk.core.service.datafeed.util;

import static org.junit.jupiter.api.Assertions.*;

import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Test class for the {@link RealTimeEventsBinder}.
 */
class RealTimeEventsBinderTest {

  private RealTimeEventsProvider realTimeEventsProvider;

  @BeforeEach
  void setUp() {
    this.realTimeEventsProvider = new RealTimeEventsProvider();
  }

  @Test
  void testBindOnMessageSent() {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final BiConsumer<V4Initiator, V4MessageSent> methodToBind = (initiator, v4MessageSent) -> methodCalled.set(true);
    RealTimeEventsBinder.bindOnMessageSent(this.realTimeEventsProvider::setListener, methodToBind);
    this.realTimeEventsProvider.trigger(l -> l.onMessageSent(new V4Initiator(), new V4MessageSent()));
    assertTrue(methodCalled.get());
  }

  @Test
  void testBindOnSymphonyElementsAction() {
    final AtomicBoolean methodCalled = new AtomicBoolean(false);
    final BiConsumer<V4Initiator, V4SymphonyElementsAction> methodToBind = (initiator, v4SymphonyElementsAction) -> methodCalled.set(true);
    RealTimeEventsBinder.bindOnSymphonyElementsAction(this.realTimeEventsProvider::setListener, methodToBind);
    this.realTimeEventsProvider.trigger(l -> l.onSymphonyElementsAction(new V4Initiator(), new V4SymphonyElementsAction()));
    assertTrue(methodCalled.get());
  }

  private static class RealTimeEventsProvider {

    private RealTimeEventListener listener;

    public void setListener(RealTimeEventListener listener) {
      this.listener = listener;
    }

    public void trigger(Consumer<RealTimeEventListener> consumer) {
      consumer.accept(this.listener);
    }
  }
}
