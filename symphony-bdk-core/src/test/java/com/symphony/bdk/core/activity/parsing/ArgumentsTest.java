package com.symphony.bdk.core.activity.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.util.Collections;

class ArgumentsTest {

  @Test
  void testNoArgument() {
    Arguments arguments = new Arguments(Collections.emptyMap());

    assertTrue(arguments.getArgumentNames().isEmpty());

    assertNull(arguments.get(""));
    assertNull(arguments.getAsString(""));
    assertNull(arguments.getString(""));
    assertNull(arguments.getCashtag(""));
    assertNull(arguments.getHashtag(""));
    assertNull(arguments.getMention(""));
  }

  @Test
  void testStringArgument() {
    final String argName = "name";
    final String argValue = "value";
    Arguments arguments = new Arguments(Collections.singletonMap(argName, argValue));

    assertEquals(Collections.singleton(argName), arguments.getArgumentNames());

    assertTrue(arguments.get(argName) instanceof String);
    assertEquals(argValue, arguments.get(argName));
    assertEquals(argValue, arguments.getAsString(argName));
    assertEquals(argValue, arguments.getString(argName));
    assertNull(arguments.getCashtag(argName));
    assertNull(arguments.getHashtag(argName));
    assertNull(arguments.getMention(argName));
  }

  @Test
  void testMentionArgument() {
    final String argName = "name";
    final String mentionText = "@John Doe";
    final long mentionUserId = 12345L;
    final Mention argValue = new Mention(mentionText, mentionUserId);
    Arguments arguments = new Arguments(Collections.singletonMap(argName, argValue));

    assertEquals(Collections.singleton(argName), arguments.getArgumentNames());

    assertTrue(arguments.get(argName) instanceof Mention);
    assertEquals(argValue, arguments.get(argName));
    assertEquals(mentionText, arguments.getAsString(argName));
    assertNull(arguments.getString(argName));
    assertNull(arguments.getCashtag(argName));
    assertNull(arguments.getHashtag(argName));
    assertEquals(argValue, arguments.getMention(argName));
  }

  @Test
  void testCashtagArgument() {
    final String argName = "name";
    final String cashtagText = "$cash";
    final String cashtagValue = "cash";
    final Cashtag argValue = new Cashtag(cashtagText, cashtagValue);
    Arguments arguments = new Arguments(Collections.singletonMap(argName, argValue));

    assertEquals(Collections.singleton(argName), arguments.getArgumentNames());

    assertTrue(arguments.get(argName) instanceof Cashtag);
    assertEquals(argValue, arguments.get(argName));
    assertEquals(cashtagText, arguments.getAsString(argName));
    assertNull(arguments.getString(argName));
    assertEquals(argValue, arguments.getCashtag(argName));
    assertNull(arguments.getHashtag(argName));
    assertNull(arguments.getMention(argName));
  }

  @Test
  void testHashtagArgument() {
    final String argName = "name";
    final String hashtagText = "#hash";
    final String hashtagValue = "hash";
    final Hashtag argValue = new Hashtag(hashtagText, hashtagValue);
    Arguments arguments = new Arguments(Collections.singletonMap(argName, argValue));

    assertEquals(Collections.singleton(argName), arguments.getArgumentNames());

    assertTrue(arguments.get(argName) instanceof Hashtag);
    assertEquals(argValue, arguments.get(argName));
    assertEquals(hashtagText, arguments.getAsString(argName));
    assertNull(arguments.getString(argName));
    assertNull(arguments.getCashtag(argName));
    assertEquals(argValue, arguments.getHashtag(argName));
    assertNull(arguments.getMention(argName));
  }

}
