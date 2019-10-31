package com.symphony.ms.songwriter.internal.event.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

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
