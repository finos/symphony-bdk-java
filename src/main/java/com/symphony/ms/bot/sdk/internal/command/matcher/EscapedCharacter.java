package com.symphony.ms.bot.sdk.internal.command.matcher;

/**
 * Matcher for escaped characters. It can be used to match with reserved, special, and unicode
 * characters. All escaped characters begin with the \ character.
 *
 * @author Gabriel Berberian
 */
public class EscapedCharacter extends CharacterMatcher {

  private static final String RESERVED_CHARACTERS = "+*?^$\\.[]{}()|/";

  private EscapedCharacter(String regex) {
    super(regex);
  }

  /**
   * Instantiates a matcher for a character.
   *
   * @param c the character.
   * @return the matcher for the character.
   */
  public static EscapedCharacter character(char c) {
    if (RESERVED_CHARACTERS.contains(Character.toString(c))) {
      return new EscapedCharacter("\\" + c);
    }
    return new EscapedCharacter(new Character(c).toString());
  }

  /**
   * Instantiates a matcher for word.
   *
   * @return the matcher for word.
   */
  public static EscapedCharacter word() {
    return new EscapedCharacter("\\w");
  }

  /**
   * Instantiates a matcher for not-word.
   *
   * @return the matcher for not-word.
   */
  public static EscapedCharacter notWord() {
    return new EscapedCharacter("\\W");
  }

  /**
   * Instantiates a matcher for digit.
   *
   * @return the matcher for digit.
   */
  public static EscapedCharacter digit() {
    return new EscapedCharacter("\\d");
  }

  /**
   * Instantiates a matcher for not-digit.
   *
   * @return the matcher for not-digit.
   */
  public static EscapedCharacter notDigit() {
    return new EscapedCharacter("\\D");
  }

  /**
   * Instantiates a matcher for white-space.
   *
   * @return the matcher for white-space.
   */
  public static EscapedCharacter whiteSpace() {
    return new EscapedCharacter("\\s");
  }

  /**
   * Instantiates a matcher for not-white-space.
   *
   * @return the matcher for not-white-space.
   */
  public static EscapedCharacter notWhiteSpace() {
    return new EscapedCharacter("\\S");
  }

  /**
   * Instantiates a matcher for a octal escaped character.
   *
   * @param charCode the character code.
   * @return the matcher for the octal escaped character.
   */
  public static EscapedCharacter octalEscape(char charCode) {
    if (charCode >= 0 && charCode <= 377) {
      return new EscapedCharacter("\\" + String.format("%03d", new Integer(charCode)));
    }
    throw new IllegalArgumentException("Octal escaped character must be between 0 an 377");
  }

  /**
   * Instantiates a matcher for a hexadecimal escaped character.
   *
   * @param charCode the character code.
   * @return the matcher for the hexadecimal escaped character.
   */
  public static EscapedCharacter hexadecimalEscape(char charCode) {
    if (charCode >= 0 && charCode <= 255) {
      return new EscapedCharacter("\\x" + Integer.toHexString(charCode));
    }
    throw new IllegalArgumentException("Escaped character must be between 0 an 255");
  }

  /**
   * Instantiates a matcher for a unicode escaped character.
   *
   * @param charCode the character code.
   * @return the matcher for the octal unicode escaped character.
   */
  public static EscapedCharacter unicodeEscape(char charCode) {
    String hexString = Integer.toHexString(charCode);
    return new EscapedCharacter("\\u0000".substring(0, 6 - hexString.length()) + hexString);
  }

  /**
   * Instantiates a matcher for escaped control character.
   *
   * @param c the escaped control character code. This can range from A to Z.
   * @return the matcher for the escaped control character.
   */
  public static EscapedCharacter controlCharacterEscape(char c) {
    if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
      return new EscapedCharacter("\\c" + c);
    }
    throw new IllegalArgumentException("Escaped character must be between A an Z");
  }

  /**
   * Instantiates a matcher for tab.
   *
   * @return the matcher for tab.
   */
  public static EscapedCharacter tab() {
    return new EscapedCharacter("\\t");
  }

  /**
   * Instantiates a matcher for line-feed.
   *
   * @return the matcher for line-feed.
   */
  public static EscapedCharacter lineFeed() {
    return new EscapedCharacter("\\n");
  }

  /**
   * Instantiates a matcher for vertical-tab.
   *
   * @return the matcher for vertical-tab.
   */
  public static EscapedCharacter verticalTab() {
    return new EscapedCharacter("\\v");
  }

  /**
   * Instantiates a matcher for form-feed.
   *
   * @return the matcher for form-feed.
   */
  public static EscapedCharacter formFeed() {
    return new EscapedCharacter("\\f");
  }
}
