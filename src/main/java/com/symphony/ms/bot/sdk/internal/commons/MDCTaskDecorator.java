package com.symphony.ms.bot.sdk.internal.commons;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

/**
 * Decorator used to copy log context between threads when commands and events
 * are dispatched.
 *
 * @author Marcus Secato
 *
 */
public class MDCTaskDecorator implements TaskDecorator {

  @Override
  public Runnable decorate(Runnable runnable) {

    final Map<String, String> contextMap;
    if (MDC.getCopyOfContextMap() == null) {
      contextMap = new HashMap<>();
    } else {
      contextMap = MDC.getCopyOfContextMap();
    }

    return () -> {
      try {
        MDC.setContextMap(contextMap);
        runnable.run();
      } finally {
        MDC.clear();
      }
    };
  }

}
