package com.symphony.bdk.bot.sdk.command.matcher;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class EscapedCharacterTest {

  @Test
  public void characterTest() {
    EscapedCharacter matcher = EscapedCharacter.character('T');
    assertEquals("T", matcher.regex());
  }

  @Test
  public void characterWithReservedTest() {
    String reservedCharacters = "+*?^$\\.[]{}()|/";
    assertAll("All Reserved Character Escaped",
        () -> assertEquals("\\+", EscapedCharacter.character(reservedCharacters.charAt(0)).regex()),
        () -> assertEquals("\\*", EscapedCharacter.character(reservedCharacters.charAt(1)).regex()),
        () -> assertEquals("\\?", EscapedCharacter.character(reservedCharacters.charAt(2)).regex()),
        () -> assertEquals("\\^", EscapedCharacter.character(reservedCharacters.charAt(3)).regex()),
        () -> assertEquals("\\$", EscapedCharacter.character(reservedCharacters.charAt(4)).regex()),
        () -> assertEquals("\\\\", EscapedCharacter.character(reservedCharacters.charAt(5)).regex()),
        () -> assertEquals("\\.", EscapedCharacter.character(reservedCharacters.charAt(6)).regex()),
        () -> assertEquals("\\[", EscapedCharacter.character(reservedCharacters.charAt(7)).regex()),
        () -> assertEquals("\\]", EscapedCharacter.character(reservedCharacters.charAt(8)).regex()),
        () -> assertEquals("\\{", EscapedCharacter.character(reservedCharacters.charAt(9)).regex()),
        () -> assertEquals("\\}", EscapedCharacter.character(reservedCharacters.charAt(10)).regex()),
        () -> assertEquals("\\(", EscapedCharacter.character(reservedCharacters.charAt(11)).regex()),
        () -> assertEquals("\\)", EscapedCharacter.character(reservedCharacters.charAt(12)).regex()),
        () -> assertEquals("\\|", EscapedCharacter.character(reservedCharacters.charAt(13)).regex()),
        () -> assertEquals("\\/", EscapedCharacter.character(reservedCharacters.charAt(14)).regex())
      );
  }

  @Test
  public void wordTest() {
    EscapedCharacter matcher = EscapedCharacter.word();
    assertEquals("\\w", matcher.regex());
  }

  @Test
  public void notWordTest() {
    EscapedCharacter matcher = EscapedCharacter.notWord();
    assertEquals("\\W", matcher.regex());
  }

  @Test
  public void digitTest() {
    EscapedCharacter matcher = EscapedCharacter.digit();
    assertEquals("\\d", matcher.regex());
  }

  @Test
  public void notDigitTest() {
    EscapedCharacter matcher = EscapedCharacter.notDigit();
    assertEquals("\\D", matcher.regex());
  }

  @Test
  public void whiteSpaceTest() {
    EscapedCharacter matcher = EscapedCharacter.whiteSpace();
    assertEquals("\\s", matcher.regex());
  }

  @Test
  public void notWhiteSpaceTest() {
    EscapedCharacter matcher = EscapedCharacter.notWhiteSpace();
    assertEquals("\\S", matcher.regex());
  }

  @Test
  public void octalEscapeTest() {
    // & => Dec 38 / Octal \046
    EscapedCharacter matcher = EscapedCharacter.octalEscape('&');
    assertEquals("\\046", matcher.regex());
  }

  @Test
  public void octalEscapeFailedTest() {
    // ɸ => Dec 632
    assertThrows(IllegalArgumentException.class, () -> EscapedCharacter.octalEscape('ɸ'));
  }

  @Test
  public void hexadecimalEscapeTest() {
    EscapedCharacter matcher = EscapedCharacter.hexadecimalEscape('&');
    assertEquals("\\x26", matcher.regex());
  }

  @Test
  public void hexadecimalEscapeFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> EscapedCharacter.hexadecimalEscape('Ź'));
  }

  @Test
  public void unicodeEscapeTest() {
    EscapedCharacter matcher = EscapedCharacter.unicodeEscape('&');
    assertEquals("\\u0026", matcher.regex());
  }

  @Test
  public void controlCharacterEscapeTest() {
    EscapedCharacter matcher = EscapedCharacter.controlCharacterEscape('T');
    assertEquals("\\cT", matcher.regex());
  }

  @Test
  public void controlCharacterEscapeFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> EscapedCharacter.controlCharacterEscape('4'));
  }

  @Test
  public void tabTest() {
    EscapedCharacter matcher = EscapedCharacter.tab();
    assertEquals("\\t", matcher.regex());
  }

  @Test
  public void lineFeedTest() {
    EscapedCharacter matcher = EscapedCharacter.lineFeed();
    assertEquals("\\n", matcher.regex());
  }

  @Test
  public void verticalTabTest() {
    EscapedCharacter matcher = EscapedCharacter.verticalTab();
    assertEquals("\\v", matcher.regex());
  }

  @Test
  public void formFeedTest() {
    EscapedCharacter matcher = EscapedCharacter.formFeed();
    assertEquals("\\f", matcher.regex());
  }
}
