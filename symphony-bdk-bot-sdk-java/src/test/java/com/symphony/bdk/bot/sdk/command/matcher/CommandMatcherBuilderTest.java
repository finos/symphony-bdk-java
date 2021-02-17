package com.symphony.bdk.bot.sdk.command.matcher;

import org.junit.jupiter.api.*;

import java.util.function.Predicate;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

public class CommandMatcherBuilderTest {
  CharacterMatcher characterMatcher;

  @BeforeEach
  public void init() {
    characterMatcher = new CharacterMatcher("test");
  }

  @Test
  public void patternTest() {
    Pattern pattern = CommandMatcherBuilder.beginsAndEndsWith("test [0-9]{2}").pattern();
    assertEquals("^test [0-9]{2}$", pattern.toString());
  }

  @Test
  public void predicateTest() {
    Predicate<String> predicate = CommandMatcherBuilder.beginsAndEndsWith("test [0-9]{2}").predicate();
    assertTrue(predicate.test("test 25"));
  }

  @Test
  public void followedByTest() {
    CommandMatcherBuilder matcherBuilder = new CommandMatcherBuilder();
    assertEquals("", matcherBuilder.regex());
    matcherBuilder.followedBy(this.characterMatcher);
    assertEquals("test", matcherBuilder.regex());
  }
  @Test
  public void beginsWithTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.beginsWith(this.characterMatcher);
    assertEquals("^test", matcherBuilder.regex());
  }

  @Test
  public void endsWithTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.endsWith(this.characterMatcher);
    assertEquals("test$", matcherBuilder.regex());
  }

  @Test
  public void beginsAndEndsWithTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.beginsAndEndsWith(this.characterMatcher);
    assertEquals("^test$", matcherBuilder.regex());
  }

  @Test
  public void wordBoundaryCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.wordBoundary(this.characterMatcher);
    assertEquals("test\\b", matcherBuilder.regex());
  }

  @Test
  public void wordBoundaryCommandMatcherTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.wordBoundary(commandMatcherBuilder);
    assertEquals("(test)\\b", matcherBuilder.regex());
  }

  @Test
  public void notWordBoundaryCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.notWordBoundary(this.characterMatcher);
    assertEquals("test\\B", matcherBuilder.regex());
  }

  @Test
  public void notWordBoundaryCommandMatcherTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.notWordBoundary(commandMatcherBuilder);
    assertEquals("(test)\\B", matcherBuilder.regex());
  }

  @Test
  public void groupTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.group(this.characterMatcher);
    assertEquals("(test)", matcherBuilder.regex());
  }

  @Test
  public void namedGroupTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.namedGroup(this.characterMatcher, "TestGroup");
    assertEquals("(?<TestGroup>test)", matcherBuilder.regex());
  }

  @Test
  public void namedGroupFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> CommandMatcherBuilder.namedGroup("test", null));
  }

  @Test
  public void nonCapturingGroupTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.nonCapturingGroup(this.characterMatcher);
    assertEquals("(?:test)", matcherBuilder.regex());
  }

  @Test
  public void numericReferenceTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.numericReference(this.characterMatcher, 2);
    assertEquals("test\\2", matcherBuilder.regex());
  }

  @Test
  public void numericReferenceFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> CommandMatcherBuilder.numericReference("test", 0));
  }

  @Test
  public void oneOrMoreCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.oneOrMore(this.characterMatcher);
    assertEquals("test+", matcherBuilder.regex());
  }

  @Test
  public void oneOrMoreCommandMatcherTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.oneOrMore(commandMatcherBuilder);
    assertEquals("(test)+", matcherBuilder.regex());
  }

  @Test
  public void zeroOrMoreCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.zeroOrMore(this.characterMatcher);
    assertEquals("test*", matcherBuilder.regex());
  }

  @Test
  public void zeroOrMoreCommandMatcherTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.zeroOrMore(commandMatcherBuilder);
    assertEquals("(test)*", matcherBuilder.regex());
  }

  @Test
  public void timesCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.times(this.characterMatcher, 2, 5);
    assertEquals("test{2,5}", matcherBuilder.regex());
  }

  @Test
  public void timesCommandMatcherTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.times(commandMatcherBuilder, 2, 5);
    assertEquals("(test){2,5}", matcherBuilder.regex());
  }

  @Test
  public void timesCharacterMatcherFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> CommandMatcherBuilder.times(this.characterMatcher, 5, 2));
  }

  @Test
  public void timesCommandMatcherFailedTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    assertThrows(IllegalArgumentException.class, () -> CommandMatcherBuilder.times(commandMatcherBuilder, 5, 2));
  }

  @Test
  public void optionalCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.optional(this.characterMatcher);
    assertEquals("test?", matcherBuilder.regex());
  }

  @Test
  public void optionalRegexTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.optional(commandMatcherBuilder);
    assertEquals("(test)?", matcherBuilder.regex());
  }

  @Test
  public void lazyCharacterMatcherTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.lazy(this.characterMatcher, '+');
    assertEquals("test+?", matcherBuilder.regex());
  }

  @Test
  public void lazyCommandMatcherTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.lazy(commandMatcherBuilder, '*');
    assertEquals("(test)*?", matcherBuilder.regex());
  }

  @Test
  public void lazyCharacterMatcherFailedTest() {
    assertThrows(IllegalArgumentException.class, () -> CommandMatcherBuilder.lazy(this.characterMatcher, '-'));
  }

  @Test
  public void lazyCommandMatcherFailedTest() {
    CommandMatcherBuilder commandMatcherBuilder = new CommandMatcherBuilder();
    commandMatcherBuilder.followedBy(this.characterMatcher);
    assertThrows(IllegalArgumentException.class, () -> CommandMatcherBuilder.lazy(commandMatcherBuilder, '='));
  }

  @Test
  public void alternationStringTest() {
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.alternation("test", "regex", "matcher");
    assertEquals("(test|regex|matcher)", matcherBuilder.regex());
  }

  @Test
  public void alternationCharacterMatcherTest() {
    CharacterMatcher matcher2 = new CharacterMatcher("regex");
    CharacterMatcher matcher3 = new CharacterMatcher("matcher");
    CommandMatcherBuilder matcherBuilder = CommandMatcherBuilder.alternation(this.characterMatcher, matcher2, matcher3);
    assertEquals("(test|regex|matcher)", matcherBuilder.regex());
  }
}
