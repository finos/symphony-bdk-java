package com.symphony.bdk.bot.sdk.command.matcher;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Matcher for characters. The instances of this class represents regular expressions that match
 * with individual characters.
 *
 * @author Gabriel Berberian
 */
public class CharacterMatcher implements Regex {

  private String regex;

  protected CharacterMatcher(String regex) {
    this.regex = regex;
  }

  @Override
  public String regex() {
    return regex;
  }

  /**
   * Instantiates a matcher for a sequence of characters.
   *
   * @param chars the character sequence.
   * @return the matcher for the character sequence.
   */
  public static CharacterMatcher characterSet(EscapedCharacter... chars) {
    if (chars == null) {
      throw new IllegalArgumentException("Character set must have at least one element");
    }
    return new CharacterMatcher(
        "[" + Arrays.stream(chars).map(EscapedCharacter::regex).collect(Collectors.joining(""))
            + "]");
  }

  /**
   * Instantiates a matcher for a negated sequence of characters.
   *
   * @param chars the character sequence.
   * @return the matcher for the character negated sequence.
   */
  public static CharacterMatcher negatedSet(EscapedCharacter... chars) {
    if (chars == null) {
      throw new IllegalArgumentException("Character set must have at least one element");
    }
    return new CharacterMatcher(
        "[^" + Arrays.stream(chars).map(EscapedCharacter::regex).collect(Collectors.joining(""))
            + "]");
  }

  /**
   * Instantiates a matcher for a character range.
   *
   * @param begin the beginning character.
   * @param end   the ending character.
   * @return the matcher for the character range.
   */
  public static CharacterMatcher range(char begin, char end) {
    if (begin < end) {
      throw new IllegalArgumentException(
          "Begin character code must be greater than ending character");
    }
    return new CharacterMatcher("[" + begin + "-" + end + "]");
  }

  /**
   * Instantiates a matcher for any character except line breakers.
   *
   * @return the mather for any character except line breakers.
   */
  public static CharacterMatcher anyExceptLineBreakers() {
    return new CharacterMatcher(".");
  }

  /**
   * Instantiates a matcher for any character.
   *
   * @return the mather for any character.
   */
  public static CharacterMatcher any() {
    return new CharacterMatcher("[\\s\\S]");
  }

  /**
   * Instantiates a matcher for string beginning.
   *
   * @return the matcher for string beginning.
   */
  public static CharacterMatcher begin() {
    return new CharacterMatcher("^");
  }

  /**
   * Instantiates a matcher for string ending.
   *
   * @return the matcher for string ending.
   */
  public static CharacterMatcher end() {
    return new CharacterMatcher("$");
  }

}
