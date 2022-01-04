package com.symphony.bdk.spring.events;

import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;
import com.symphony.bdk.http.api.tracing.DistributedTracingContext;

import org.slf4j.MDC;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RealTimeEventsAsyncTestListener {

  @EventListener
  public void onSymphonyElementsAction(RealTimeEvent<V4SymphonyElementsAction> event) {
    Objects.requireNonNull(event.getInitiator());
    Objects.requireNonNull(event.getSource());
    // Test the MDC values have been copied from the parent thread
    Objects.requireNonNull(MDC.get(DistributedTracingContext.TRACE_ID));
    // Make sure the current thread is different from the parent thread, so child thread
    if (Thread.currentThread().getName().equals(MDC.get("thread_name"))) {
      throw new IllegalStateException("test failed - thread name must be different.");
    }
  }
}
