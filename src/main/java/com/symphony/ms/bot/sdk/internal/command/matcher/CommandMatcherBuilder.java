package com.symphony.ms.bot.sdk.internal.command.matcher;

import org.springframework.util.StringUtils;
import com.symphony.ms.bot.sdk.internal.command.CommandFilter;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Builder for command matcher. Can be used to build the regular expressions that defines which
 * command will be selected by the command filtering process of {@link CommandFilter}.
 *
 * @author Gabriel Berberian
 */
public class CommandMatcherBuilder implements Regex {

  private static final String QUANTIFIERS = "+*";

  private String regex;

  public CommandMatcherBuilder() {
    this.regex = "";
  }

  private CommandMatcherBuilder(String regex) {
    this.regex = regex;
  }

  @Override
  public String regex() {
    return regex;
  }

  private void setRegex(String regex) {
    this.regex = regex;
  }

  private void setRegex(Regex regex) {
    this.regex = regex.regex();
  }

  /**
   * Make a pattern with the current regex of the command matcher builder.
   *
   * @return the command matcher pattern
   */
  public Pattern pattern() {
    return Pattern.compile(regex());
  }

  /**
   * Make a predicate with the current regex of the command matcher builder.
   *
   * @return the command matcher predicate
   */
  public Predicate<String> predicate() {
    return pattern().asPredicate();
  }

  /**
   * Appends a regex to the current command matcher builder regex.
   *
   * @param regex the regex to be appended.
   * @return the command matcher builder with the regex appended.
   */
  public CommandMatcherBuilder followedBy(String regex) {
    if (regex != null) {
      this.regex += regex;
    }
    return this;
  }

  /**
   * Appends a regex to the current command matcher builder regex.
   *
   * @param regex the regex to be appended.
   * @return the command matcher builder with the regex appended.
   */
  public CommandMatcherBuilder followedBy(Regex regex) {
    return followedBy(regex.regex());
  }

  /**
   * Instantiates a command matcher builder with the beginning pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the beginning pattern applied to the regex.
   */
  public static CommandMatcherBuilder beginsWith(String regex) {
    return new CommandMatcherBuilder("^" + regex);
  }

  /**
   * Instantiates a command matcher builder with the beginning pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the beginning pattern applied to the regex.
   */
  public static CommandMatcherBuilder beginsWith(Regex regex) {
    return beginsWith(regex.regex());
  }

  /**
   * Instantiates a command matcher builder with the ending pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the ending pattern applied to the regex.
   */
  public static CommandMatcherBuilder endsWith(String regex) {
    return new CommandMatcherBuilder(regex + "$");
  }

  /**
   * Instantiates a command matcher builder with the ending pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the ending pattern applied to the regex.
   */
  public static CommandMatcherBuilder endsWith(Regex regex) {
    return endsWith(regex.regex());
  }

  /**
   * Instantiates a command matcher builder with the beginning-and-ending pattern wrapping a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the beginning-and-ending pattern wrapping the regex.
   */
  public static CommandMatcherBuilder beginsAndEndsWith(String regex) {
    return new CommandMatcherBuilder("^" + regex + "$");
  }

  /**
   * Instantiates a command matcher builder with the beginning-and-ending pattern wrapping a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the beginning-and-ending pattern wrapping the regex.
   */
  public static CommandMatcherBuilder beginsAndEndsWith(Regex regex) {
    return beginsAndEndsWith(regex.regex());
  }

  /**
   * Instantiates a command matcher builder with the word-boundary pattern applied to a character
   * matcher.
   *
   * @param characterMatcher the character matcher.
   * @return the command matcher builder with the word-boundary pattern applied to a character
   * matcher.
   */
  public static CommandMatcherBuilder wordBoundary(CharacterMatcher characterMatcher) {
    return new CommandMatcherBuilder(characterMatcher.regex() + "\\b");
  }

  /**
   * Wraps the regex of a command matcher builder with the word-boundary pattern.
   *
   * @param commandMatcherBuilder the command matcher builder.
   * @return the command matcher builder with the word-boundary pattern wrapping the regex.
   */
  public static CommandMatcherBuilder wordBoundary(CommandMatcherBuilder commandMatcherBuilder) {
    commandMatcherBuilder.setRegex("(" + commandMatcherBuilder.regex() + ")\\b");
    return commandMatcherBuilder;
  }

  /**
   * Instantiates a command matcher builder with the not-word-boundary pattern applied to a
   * character matcher.
   *
   * @param characterMatcher the character matcher.
   * @return the command matcher builder with the not-word-boundary pattern wrapping the regex.
   */
  public static CommandMatcherBuilder notWordBoundary(CharacterMatcher characterMatcher) {
    return new CommandMatcherBuilder(characterMatcher.regex() + "\\B");
  }

  /**
   * Wraps the regex of a command matcher builder with the not-word-boundary pattern.
   *
   * @param commandMatcherBuilder the command matcher builder.
   * @return the command matcher builder with the not-word-boundary pattern wrapping the regex.
   */
  public static CommandMatcherBuilder notWordBoundary(CommandMatcherBuilder commandMatcherBuilder) {
    commandMatcherBuilder.setRegex("(" + commandMatcherBuilder.regex() + ")\\B");
    return commandMatcherBuilder;
  }

  /**
   * Instantiates a command matcher builder with a regex grouped.
   *
   * @param regex the regex.
   * @return the command matcher builder with the regex grouped.
   */
  public static CommandMatcherBuilder group(String regex) {
    return new CommandMatcherBuilder("(" + regex + ")");
  }

  /**
   * Instantiates a command matcher builder with a regex grouped.
   *
   * @param regex the regex.
   * @return the command matcher builder with the regex grouped.
   */
  public static CommandMatcherBuilder group(Regex regex) {
    return group(regex.regex());
  }

  /**
   * Instantiates a command matcher builder with the named-group pattern wrapping a regex.
   *
   * @param regex the regex.
   * @param name  the group name.
   * @return the command matcher builder with the named-group pattern wrapping the regex.
   */
  public static CommandMatcherBuilder namedGroup(String regex, String name) {
    if (StringUtils.isEmpty(name)) {
      throw new IllegalArgumentException("Empty name");
    }
    return new CommandMatcherBuilder("(?<" + name + ">" + regex + ")");
  }

  /**
   * Instantiates a command matcher builder with the named-group pattern wrapping a regex.
   *
   * @param regex the regex.
   * @param name  the group name.
   * @return the command matcher builder with the name group-pattern wrapping the regex.
   */
  public static CommandMatcherBuilder namedGroup(Regex regex, String name) {
    return namedGroup(regex.regex(), name);
  }

  /**
   * Instantiates a command matcher builder with the non-capturing-group pattern wrapping a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the non-capturing-group pattern wrapping the regex.
   */
  public static CommandMatcherBuilder nonCapturingGroup(String regex) {
    return new CommandMatcherBuilder("(?:" + regex + ")");
  }

  /**
   * Instantiates a command matcher builder with the non-capturing-group pattern wrapping a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the non-capturing-group pattern wrapping the regex.
   */
  public static CommandMatcherBuilder nonCapturingGroup(Regex regex) {
    return nonCapturingGroup(regex.regex());
  }

  /**
   * Instantiates a command matcher builder with the numeric-reference pattern applied to a regex.
   *
   * @param regex            the regex.
   * @param numericReference the numeric reference.
   * @return the command matcher builder with the numeric-reference pattern applied to the regex.
   */
  public static CommandMatcherBuilder numericReference(String regex, int numericReference) {
    if (numericReference < 1) {
      throw new IllegalArgumentException("Illegal numeric reference");
    }
    return new CommandMatcherBuilder(regex + "\\" + numericReference);
  }

  /**
   * Instantiates a command matcher builder with the numeric-reference pattern applied to a regex.
   *
   * @param regex            the regex.
   * @param numericReference the numeric reference.
   * @return the command matcher builder with the numeric-reference pattern applied to the regex.
   */
  public static CommandMatcherBuilder numericReference(Regex regex, int numericReference) {
    return numericReference(regex.regex(), numericReference);
  }

  /**
   * Instantiates a command matcher builder with the one-or-more pattern applied to the regex of a
   * character matcher.
   *
   * @param characterMatcher the character matcher.
   * @return the command matcher builder with the one-or-more pattern applied to the regex.
   */
  public static CommandMatcherBuilder oneOrMore(CharacterMatcher characterMatcher) {
    return new CommandMatcherBuilder(characterMatcher.regex() + "+");
  }

  /**
   * Wraps the regex of a command matcher builder with the one-or-more pattern.
   *
   * @param commandMatcherBuilder the command matcher builder.
   * @return the command matcher builder with the one-or-more pattern applied to the regex.
   */
  public static CommandMatcherBuilder oneOrMore(CommandMatcherBuilder commandMatcherBuilder) {
    commandMatcherBuilder.setRegex("(" + commandMatcherBuilder.regex() + ")+");
    return commandMatcherBuilder;
  }

  /**
   * Instantiates a command matcher builder with the zero-or-more pattern applied to the regex of a
   * character matcher.
   *
   * @param characterMatcher the character matcher.
   * @return the command matcher builder with the zero-or-more pattern applied to the regex.
   */
  public static CommandMatcherBuilder zeroOrMore(CharacterMatcher characterMatcher) {
    return new CommandMatcherBuilder(characterMatcher.regex() + "*");
  }

  /**
   * Wraps the regex of a command matcher builder with the zero-or-more pattern.
   *
   * @param commandMatcherBuilder the command matcher builder.
   * @return the command matcher builder with the zero-or-more pattern applied to the regex.
   */
  public static CommandMatcherBuilder zeroOrMore(CommandMatcherBuilder commandMatcherBuilder) {
    commandMatcherBuilder.setRegex("(" + commandMatcherBuilder.regex() + ")*");
    return commandMatcherBuilder;
  }

  /**
   * Instantiates a command matcher builder with the times pattern applied to the regex of a
   * character matcher.
   *
   * @param characterMatcher the character matcher.
   * @param from             the minimal number of times.
   * @param to               the maximal number of times.
   * @return the command matcher builder with the times pattern applied to the regex.
   */
  public static CommandMatcherBuilder times(CharacterMatcher characterMatcher, int from, int to) {
    if (from < 0 || to < 0 || from > to) {
      throw new IllegalArgumentException("Illegal number of times");
    }
    return new CommandMatcherBuilder(characterMatcher.regex() + "{" + from + "," + to + "}");
  }

  /**
   * Wraps the regex of a command matcher builder with the times pattern.
   *
   * @param commandMatcherBuilder the command matcher builder.
   * @param from                  the minimal number of times.
   * @param to                    the maximal number of times.
   * @return the command matcher builder with the times pattern applied to the regex.
   */
  public static CommandMatcherBuilder times(CommandMatcherBuilder commandMatcherBuilder, int from,
      int to) {
    if (from < 0 || to < 0 || from > to) {
      throw new IllegalArgumentException("Illegal number of times");
    }
    commandMatcherBuilder.setRegex(
        "(" + commandMatcherBuilder.regex() + "){" + from + "," + to + "}");
    return commandMatcherBuilder;
  }

  /**
   * Instantiates a command matcher builder with the optional pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the optional pattern applied to the regex.
   */
  public static CommandMatcherBuilder optional(Regex regex) {
    return new CommandMatcherBuilder(regex.regex() + "?");
  }

  /**
   * Instantiates a command matcher builder with the lazy pattern applied to the regex of a
   * character matcher.
   *
   * @param characterMatcher the character matcher.
   * @param quantifier       the quantifier.
   * @return the command matcher builder with the lazy pattern applied to the regex.
   */
  public static CommandMatcherBuilder lazy(CharacterMatcher characterMatcher, char quantifier) {
    if (QUANTIFIERS.contains(Character.toString(quantifier))) {
      return new CommandMatcherBuilder(characterMatcher.regex() + quantifier + "?");
    }
    throw new IllegalArgumentException("Illegal quantifier.");
  }

  /**
   * Wraps the regex of a command matcher builder with the lazy pattern.
   *
   * @param commandMatcherBuilder the command matcher builder.
   * @param quantifier            the quantifier.
   * @return the command matcher builder with the lazy pattern applied to the regex.
   */
  public static CommandMatcherBuilder lazy(CommandMatcherBuilder commandMatcherBuilder,
      char quantifier) {
    if (QUANTIFIERS.contains(Character.toString(quantifier))) {
      commandMatcherBuilder.setRegex("(" + commandMatcherBuilder.regex() + ")" + quantifier + "?");
      return commandMatcherBuilder;
    }
    throw new IllegalArgumentException("Illegal quantifier.");
  }

  /**
   * Instantiates a command matcher builder with the alternation pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the alternation pattern applied to the regex.
   */
  public static CommandMatcherBuilder alternation(String... regex) {
    return new CommandMatcherBuilder("(" + String.join("|", Arrays.asList(regex)) + ")");
  }

  /**
   * Instantiates a command matcher builder with the alternation pattern applied to a regex.
   *
   * @param regex the regex.
   * @return the command matcher builder with the alternation pattern applied to the regex.
   */
  public static CommandMatcherBuilder alternation(Regex... regex) {
    return new CommandMatcherBuilder("(" +
        String.join("|", Arrays.stream(regex).map(Regex::regex).collect(Collectors.toList()))
        + ")");
  }

}
