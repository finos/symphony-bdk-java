package com.symphony.bdk.core.service.message.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class MessageUtilTest {

  @Test
  public void testEscapeSpecialCharsMatchFound() {
    String text = "\\.hello";
    String expectedText = "&#92;&#46;hello";
    assertEquals(expectedText, MessageUtil.escapeSpecialChars(text));
  }

  @Test
  public void testEscapeSpecialCharsNoMatchFound() {
    String text = "hello.";
    String expectedText = "hello&#46;";
    assertEquals(expectedText, MessageUtil.escapeSpecialChars(text));
  }

  @Test
  public void testEscapeSpecialCharsMultipleMatches() {
    String text = "Here's multiple chars <'\"$#=.[]";
    String expectedText = "Here&apos;s multiple chars &lt;&apos;&quot;&#36;&#35;&#61;&#46;&#91;&#93;";
    assertEquals(expectedText, MessageUtil.escapeSpecialChars(text));
  }

  @Test
  public void testEscapeSpecialCharsNoChange() {
    String text = "  This text will remain the same  ";
    assertEquals(text, MessageUtil.escapeSpecialChars(text));
  }
}
