package com.symphony.bdk.bot.sdk.command.matcher;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class CharacterMatcherTest {

  @Test
  public void regexTest() {
    CharacterMatcher matcher = new CharacterMatcher("Test");
    assertEquals("Test", matcher.regex());
  }

  @Test
  public void characterSetTest() {
    CharacterMatcher matcher = CharacterMatcher.characterSet(
        EscapedCharacter.character('T'), EscapedCharacter.character('e'),
        EscapedCharacter.character('s'), EscapedCharacter.character('t')
    );
    assertEquals("[Test]", matcher.regex());
  }

  @Test
  public void characterSetNullTest() {
    assertThrows(IllegalArgumentException.class, CharacterMatcher::characterSet);
  }

  @Test
  public void negatedSetTest() {
    CharacterMatcher matcher = CharacterMatcher.negatedSet(
        EscapedCharacter.character('T'), EscapedCharacter.character('e'),
        EscapedCharacter.character('s'), EscapedCharacter.character('t')
    );
    assertEquals("[^Test]", matcher.regex());
  }

  @Test
  public void negatedSetNullTest() {
    assertThrows(IllegalArgumentException.class, CharacterMatcher::negatedSet);
  }

  @Test
  public void rangeTest() {
    CharacterMatcher matcher = CharacterMatcher.range('1', '9');
    assertEquals("[1-9]", matcher.regex());
  }

  @Test
  public void rangeFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> CharacterMatcher.range('9', '1'));
  }

  @Test
  public void anyExceptLineBreakersTest() {
    CharacterMatcher matcher = CharacterMatcher.anyExceptLineBreakers();
    assertEquals(".", matcher.regex());
  }

  @Test
  public void anyTest() {
    CharacterMatcher matcher = CharacterMatcher.any();
    assertEquals("[\\s\\S]", matcher.regex());
  }

  @Test
  public void beginTest() {
    CharacterMatcher matcher = CharacterMatcher.begin();
    assertEquals("^", matcher.regex());
  }

  @Test
  public void endTest() {
    CharacterMatcher matcher = CharacterMatcher.end();
    assertEquals("$", matcher.regex());
  }
}
