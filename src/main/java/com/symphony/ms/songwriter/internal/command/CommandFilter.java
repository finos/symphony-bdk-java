package com.symphony.ms.songwriter.internal.command;

import java.util.function.Predicate;
import com.symphony.ms.songwriter.internal.event.model.MessageEvent;

public interface CommandFilter {

  void addFilter(String commandName, Predicate<String> filter);

  void setDefaultFilter(String commandName, Predicate<String> defaultFilter);

  void filter(MessageEvent messageEvent);
}
