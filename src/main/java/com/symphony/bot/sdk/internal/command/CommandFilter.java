package com.symphony.bot.sdk.internal.command;

import java.util.function.Predicate;

import com.symphony.bot.sdk.internal.event.model.MessageEvent;

/**
 * Filters Symphony chat messages looking for bot commands.
 *
 * @author Marcus Secato
 *
 */
public interface CommandFilter {

  /**
   * Registers a command filter
   *
   * @param commandName
   * @param filter
   */
  void addFilter(String commandName, Predicate<String> filter);

  /**
   * Registers a default command filter. Used to return standard messages when
   * bot is not fed with a valid command.
   *
   * @param commandName
   * @param defaultFilter
   */
  void setDefaultFilter(String commandName, Predicate<String> defaultFilter);

  /**
   * Performs the actual message filtering based on registered filters. If the
   * message corresponds to a valid command, {@link CommandDispatcher} will be
   * called.
   *
   * @param messageEvent
   */
  void filter(MessageEvent messageEvent);
}
