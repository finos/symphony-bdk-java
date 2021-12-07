package com.symphony.bdk.core.activity.parsing;

import static com.symphony.bdk.core.activity.parsing.ArgumentCommandToken.ARGUMENT_VALUE_REGEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SlashCommandPatternTest {

  @Test
  void emptyPattern() {
    SlashCommandPattern pattern = new SlashCommandPattern("");

    assertTrue(pattern.getTokens().isEmpty());

    assertTrue(pattern.getMatchResult("").isMatching());
    assertFalse(pattern.getMatchResult("d").isMatching());
  }

  @Test
  void oneStaticToken() {
    SlashCommandPattern pattern = new SlashCommandPattern("/command");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());

    assertFalse(pattern.getMatchResult("").isMatching());
    assertFalse(pattern.getMatchResult("a").isMatching());
    assertTrue(pattern.getMatchResult("/command").isMatching());
    assertTrue(pattern.getMatchResult(" /command  ").isMatching());
    assertFalse(pattern.getMatchResult(" /command toto").isMatching());
  }

  @Test
  void oneStaticTokenWithSpaces() {
    SlashCommandPattern pattern = new SlashCommandPattern(" /command  ");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());
  }

  @Test
  void twoStaticTokens() {
    SlashCommandPattern pattern = new SlashCommandPattern("/command ab");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(2, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());
    assertEquals("^ab$", tokens.get(1).getRegexPattern().pattern());

    assertFalse(pattern.getMatchResult("").isMatching());
    assertFalse(pattern.getMatchResult("a").isMatching());
    assertFalse(pattern.getMatchResult("/command").isMatching());
    assertFalse(pattern.getMatchResult(" /command toto").isMatching());
    assertTrue(pattern.getMatchResult("/command ab").isMatching());
    assertTrue(pattern.getMatchResult(" /command  ab ").isMatching());
    assertFalse(pattern.getMatchResult("/command ab toto").isMatching());
  }

  @Test
  void twoStaticTokensWithSpaces() {
    SlashCommandPattern pattern = new SlashCommandPattern("  /command  ab  ");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(2, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());
    assertEquals("^ab$", tokens.get(1).getRegexPattern().pattern());
  }

  @Test
  void oneArgument() {
    final String argumentName = "arg";
    SlashCommandPattern pattern = new SlashCommandPattern("{" + argumentName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertEquals(ARGUMENT_VALUE_REGEX, tokens.get(0).getRegexPattern().pattern());

    assertEquals(Collections.singletonList(argumentName), pattern.getArgumentNames());

    final MatchResult matchResultEmptyInput = pattern.getMatchResult("");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().isEmpty());

    assertFalse(pattern.getMatchResult("ab cd").isMatching());

    final String input = "a1454#";
    final MatchResult matchResult = pattern.getMatchResult(input);
    assertTrue(matchResult.isMatching());
    assertEquals(Collections.singletonMap(argumentName, input), matchResult.getArguments());
  }

  @Test
  void oneStaticTokenAndOneArgument() {
    final String argumentName = "arg";
    SlashCommandPattern pattern = new SlashCommandPattern("/command {" + argumentName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(2, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());
    assertEquals(ARGUMENT_VALUE_REGEX, tokens.get(1).getRegexPattern().pattern());

    assertEquals(Collections.singletonList(argumentName), pattern.getArgumentNames());

    final MatchResult matchResultEmptyInput = pattern.getMatchResult("/command");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().isEmpty());

    final String input = "a1454#";
    final MatchResult matchResult = pattern.getMatchResult("/command " + input);
    assertTrue(matchResult.isMatching());
    assertEquals(Collections.singletonMap(argumentName, input), matchResult.getArguments());
  }

  @Test
  void oneStaticTokenAndTwoArguments() {
    final String firstArgName = "arg1";
    final String secondArgName = "arg2";
    SlashCommandPattern pattern = new SlashCommandPattern("/command {" + firstArgName + "} {" + secondArgName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(3, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());
    assertEquals(ARGUMENT_VALUE_REGEX, tokens.get(1).getRegexPattern().pattern());
    assertEquals(ARGUMENT_VALUE_REGEX, tokens.get(2).getRegexPattern().pattern());

    assertEquals(Arrays.asList(firstArgName, secondArgName), pattern.getArgumentNames());

    final MatchResult matchResultEmptyInput = pattern.getMatchResult("/command");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().isEmpty());

    final String firstArgValue = "ab";
    final String secondArgValue = "def";
    final MatchResult matchResult = pattern.getMatchResult("/command " + firstArgValue + " " + secondArgValue);
    assertTrue(matchResult.isMatching());
    assertEquals(2, matchResult.getArguments().size());
    assertEquals(firstArgValue, matchResult.getArguments().get(firstArgName));
    assertEquals(secondArgValue, matchResult.getArguments().get(secondArgName));
  }
}
