package com.symphony.bdk.core.activity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.List;

class SlashCommandPatternTest {

  @Test
  void emptyPattern() {
    SlashCommandPattern pattern = new SlashCommandPattern("");

    assertTrue(pattern.getTokens().isEmpty());

    assertTrue(pattern.matches(""));
    assertFalse(pattern.matches("d"));
  }

  @Test
  void oneStaticToken() {
    SlashCommandPattern pattern = new SlashCommandPattern("/command");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertEquals("/command", tokens.get(0).getRegexPattern().pattern());

    assertFalse(pattern.matches(""));
    assertFalse(pattern.matches("a"));
    assertTrue(pattern.matches("/command"));
    assertTrue(pattern.matches(" /command  "));
  }

  @Test
  void oneStaticTokenWithSpaces() {
    SlashCommandPattern pattern = new SlashCommandPattern(" /command  ");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertEquals("/command", tokens.get(0).getRegexPattern().pattern());
  }

  @Test
  void twoStaticTokens() {
    SlashCommandPattern pattern = new SlashCommandPattern("/command echo");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(2, tokens.size());
    assertEquals("/command", tokens.get(0).getRegexPattern().pattern());
    assertEquals("echo", tokens.get(1).getRegexPattern().pattern());

    assertFalse(pattern.matches(""));
    assertFalse(pattern.matches("a"));
    assertFalse(pattern.matches("/command"));
    assertFalse(pattern.matches(" /command toto"));
    assertTrue(pattern.matches("/command echo"));
    assertTrue(pattern.matches(" /command  echo "));
  }
}
