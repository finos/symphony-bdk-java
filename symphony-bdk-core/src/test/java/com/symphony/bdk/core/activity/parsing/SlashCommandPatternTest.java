package com.symphony.bdk.core.activity.parsing;

import static com.symphony.bdk.core.activity.parsing.ArgumentCommandToken.ARGUMENT_VALUE_REGEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.gen.api.model.V4Message;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class SlashCommandPatternTest {

  @Test
  void emptyPattern() {
    SlashCommandPattern pattern = new SlashCommandPattern("");

    assertTrue(pattern.getTokens().isEmpty());

    assertTrue(getMatchResult(pattern,"").isMatching());
    assertFalse(getMatchResult(pattern,"d").isMatching());
  }

  @Test
  void oneStaticToken() {
    SlashCommandPattern pattern = new SlashCommandPattern("/command");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());

    assertFalse(getMatchResult(pattern,"").isMatching());
    assertFalse(getMatchResult(pattern,"a").isMatching());
    assertTrue(getMatchResult(pattern,"/command").isMatching());
    assertTrue(getMatchResult(pattern," /command  ").isMatching());
    assertFalse(getMatchResult(pattern," /command toto").isMatching());
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

    assertFalse(getMatchResult(pattern,"").isMatching());
    assertFalse(getMatchResult(pattern,"a").isMatching());
    assertFalse(getMatchResult(pattern,"/command").isMatching());
    assertFalse(getMatchResult(pattern," /command toto").isMatching());
    assertTrue(getMatchResult(pattern,"/command ab").isMatching());
    assertTrue(getMatchResult(pattern," /command  ab ").isMatching());
    assertFalse(getMatchResult(pattern,"/command ab toto").isMatching());
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

    final MatchResult matchResultEmptyInput = getMatchResult(pattern,"");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().isEmpty());

    assertFalse(getMatchResult(pattern,"ab cd").isMatching());

    final String input = "a1454#";
    final MatchResult matchResult = getMatchResult(pattern,input);
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

    final MatchResult matchResultEmptyInput = getMatchResult(pattern,"/command");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().isEmpty());

    final String input = "a1454#";
    final MatchResult matchResult = getMatchResult(pattern,"/command " + input);
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

    final MatchResult matchResultEmptyInput = getMatchResult(pattern,"/command");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().isEmpty());

    final String firstArgValue = "ab";
    final String secondArgValue = "def";
    final MatchResult matchResult = getMatchResult(pattern,"/command " + firstArgValue + " " + secondArgValue);
    assertTrue(matchResult.isMatching());
    assertEquals(2, matchResult.getArguments().size());
    assertEquals(firstArgValue, matchResult.getArguments().get(firstArgName));
    assertEquals(secondArgValue, matchResult.getArguments().get(secondArgName));
  }

  @Test
  void oneMention() {
    String argName = "myarg";
    SlashCommandPattern pattern = new SlashCommandPattern("{@" + argName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof TypedArgumentToken<?>);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertTrue(matchResult.isMatching());
    assertEquals(1, matchResult.getArguments().size());

    final Object actual = matchResult.getArguments().get(argName);
    assertTrue(actual instanceof Mention);

    final Mention mention = (Mention) actual;
    assertEquals(12345678L, mention.getUserId());
    assertEquals("jane-doe", mention.getUserDisplayName());
    assertEquals("@jane-doe", mention.getText());
  }

  @Test
  void oneMentionWithSpace() {
    String argName = "myarg";
    SlashCommandPattern pattern = new SlashCommandPattern("{@" + argName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof TypedArgumentToken<?>);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">@John Doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertTrue(matchResult.isMatching());
    assertEquals(1, matchResult.getArguments().size());

    final Object resultArg = matchResult.getArguments().get(argName);
    assertTrue(resultArg instanceof Mention);

    final Mention mention = (Mention) resultArg;
    assertEquals(12345678L, mention.getUserId());
    assertEquals("John Doe", mention.getUserDisplayName());
    assertEquals("@John Doe", mention.getText());
  }

  @Test
  void oneStaticTokenOneMentionOneArg() {
    String mentionArgName = "arg1";
    String stringArgName = "arg2";
    SlashCommandPattern pattern = new SlashCommandPattern("/command {@" + mentionArgName + "} {" + stringArgName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(3, tokens.size());
    assertEquals("^/command$", tokens.get(0).getRegexPattern().pattern());
    assertTrue(tokens.get(1) instanceof TypedArgumentToken<?>);
    assertEquals(ARGUMENT_VALUE_REGEX, tokens.get(2).getRegexPattern().pattern());

    V4Message message = buildMessage("/command <span class=\"entity\" data-entity-id=\"0\">@John Doe</span> argValue",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertTrue(matchResult.isMatching());
    assertEquals(2, matchResult.getArguments().size());

    final Object resultArg = matchResult.getArguments().get(mentionArgName);
    assertTrue(resultArg instanceof Mention);

    final Mention mention = (Mention) resultArg;
    assertEquals(12345678L, mention.getUserId());
    assertEquals("John Doe", mention.getUserDisplayName());
    assertEquals("@John Doe", mention.getText());

    assertEquals("argValue", matchResult.getArguments().get(stringArgName));
  }

  @Test
  void oneCashtag() {
    String argName = "myarg";
    SlashCommandPattern pattern = new SlashCommandPattern("{$" + argName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof TypedArgumentToken<?>);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">$mycashtag</span>",
        "{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.fin.security.id.ticker\",\"value\":\"mycashtag\"}],\"type\":\"org.symphonyoss.fin.security\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertTrue(matchResult.isMatching());
    assertEquals(1, matchResult.getArguments().size());

    final Object resultArg = matchResult.getArguments().get(argName);
    assertTrue(resultArg instanceof Cashtag);

    final Cashtag cashtag = (Cashtag) resultArg;
    assertEquals("$mycashtag", cashtag.getText());
    assertEquals("mycashtag", cashtag.getValue());
  }

  @Test
  void oneHashtag() {
    String argName = "myarg";
    SlashCommandPattern pattern = new SlashCommandPattern("{#" + argName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof TypedArgumentToken<?>);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">#myhashtag</span>",
        "{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.taxonomy.hashtag\",\"value\":\"myhashtag\"}],\"type\":\"org.symphonyoss.taxonomy\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertTrue(matchResult.isMatching());
    assertEquals(1, matchResult.getArguments().size());

    final Object resultArg = matchResult.getArguments().get(argName);
    assertTrue(resultArg instanceof Hashtag);

    final Hashtag cashtag = (Hashtag) resultArg;
    assertEquals("#myhashtag", cashtag.getText());
    assertEquals("myhashtag", cashtag.getValue());
  }

  private MatchResult getMatchResult(SlashCommandPattern pattern, String textContent) {
    return pattern.getMatchResult(buildMessage(textContent));
  }

  private V4Message buildMessage(String textContent) {
    return new V4Message().message("<div data-format=\"PresentationML\" data-version=\"2.0\" class=\"wysiwyg\"><p>" + textContent + "</p></div>");
  }

  private V4Message buildMessage(String textContent, String data) {
    return buildMessage(textContent).data(data);
  }
}
