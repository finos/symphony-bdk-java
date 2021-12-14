package com.symphony.bdk.core.activity.parsing;

import static com.symphony.bdk.core.activity.parsing.StringArgumentCommandToken.ARGUMENT_VALUE_REGEX;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.activity.exception.SlashCommandSyntaxException;
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
    assertIsRegexToken("^/command$", tokens.get(0));

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
    assertIsRegexToken("^/command$", tokens.get(0));
  }

  @Test
  void twoStaticTokens() {
    SlashCommandPattern pattern = new SlashCommandPattern("/command ab");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(2, tokens.size());
    assertIsRegexToken("^/command$", tokens.get(0));
    assertIsRegexToken("^ab$", tokens.get(1));

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
    assertIsRegexToken("^/command$", tokens.get(0));
    assertIsRegexToken("^ab$", tokens.get(1));
  }

  @Test
  void oneArgument() {
    final String argumentName = "arg";
    SlashCommandPattern pattern = new SlashCommandPattern("{" + argumentName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertIsStringArgumentToken(tokens.get(0));

    assertEquals(Collections.singletonList(argumentName), pattern.getArgumentNames());

    final MatchResult matchResultEmptyInput = getMatchResult(pattern,"");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().getArgumentNames().isEmpty());

    assertFalse(getMatchResult(pattern,"ab cd").isMatching());

    final String input = "a1454#";
    final MatchResult matchResult = getMatchResult(pattern,input);
    assertTrue(matchResult.isMatching());

    final Arguments arguments = matchResult.getArguments();
    assertEquals(Collections.singleton(argumentName), arguments.getArgumentNames());
    assertEquals(input, arguments.get(argumentName));
  }

  @Test
  void twiceTheSameArgumentShouldThrowException() {
    assertThrows(SlashCommandSyntaxException.class, () -> new SlashCommandPattern("{arg} {arg}"));
  }

  @Test
  void oneStaticTokenAndOneArgument() {
    final String argumentName = "arg";
    SlashCommandPattern pattern = new SlashCommandPattern("/command {" + argumentName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(2, tokens.size());
    assertIsRegexToken("^/command$", tokens.get(0));
    assertIsStringArgumentToken(tokens.get(1));

    assertEquals(Collections.singletonList(argumentName), pattern.getArgumentNames());

    final MatchResult matchResultEmptyInput = getMatchResult(pattern,"/command");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().getArgumentNames().isEmpty());

    final String input = "a1454#";
    final MatchResult matchResult = getMatchResult(pattern,"/command " + input);
    assertTrue(matchResult.isMatching());

    final Arguments arguments = matchResult.getArguments();
    assertEquals(Collections.singleton(argumentName), arguments.getArgumentNames());
    assertEquals(input, arguments.get(argumentName));
  }

  @Test
  void oneStaticTokenAndTwoArguments() {
    final String firstArgName = "arg1";
    final String secondArgName = "arg2";
    SlashCommandPattern pattern = new SlashCommandPattern("/command {" + firstArgName + "} {" + secondArgName + "}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(3, tokens.size());
    assertIsRegexToken("^/command$", tokens.get(0));
    assertIsStringArgumentToken(tokens.get(1));
    assertIsStringArgumentToken(tokens.get(2));

    assertEquals(Arrays.asList(firstArgName, secondArgName), pattern.getArgumentNames());

    final MatchResult matchResultEmptyInput = getMatchResult(pattern,"/command");
    assertFalse(matchResultEmptyInput.isMatching());
    assertTrue(matchResultEmptyInput.getArguments().getArgumentNames().isEmpty());

    final String firstArgValue = "ab";
    final String secondArgValue = "def";
    final MatchResult matchResult = getMatchResult(pattern,"/command " + firstArgValue + " " + secondArgValue);
    assertTrue(matchResult.isMatching());
    assertEquals(2, matchResult.getArguments().getArgumentNames().size());
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
    assertEquals(1, matchResult.getArguments().getArgumentNames().size());

    final Mention mention = matchResult.getArguments().getMention(argName);
    assertEquals(12345678L, mention.getUserId());
    assertEquals("jane-doe", mention.getUserDisplayName());
    assertEquals("@jane-doe", mention.getText());
  }

  @Test
  void twiceTheSameArgumentWithDifferentTypesShouldThrowException() {
    assertThrows(SlashCommandSyntaxException.class, () -> new SlashCommandPattern("{arg} {@arg}"));
  }

  @Test
  void oneMentionIsNotMatchingStringPattern() {
    SlashCommandPattern pattern = new SlashCommandPattern("{myarg}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof StringArgumentCommandToken);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">@jane-doe</span>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertFalse(matchResult.isMatching());
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
    assertEquals(1, matchResult.getArguments().getArgumentNames().size());

    final Mention mention = matchResult.getArguments().getMention(argName);
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
    assertIsRegexToken("^/command$", tokens.get(0));
    assertTrue(tokens.get(1) instanceof TypedArgumentToken<?>);
    assertIsRegexToken(ARGUMENT_VALUE_REGEX, tokens.get(2));

    V4Message message = buildMessage("/command <span class=\"entity\" data-entity-id=\"0\">@John Doe</span> argValue",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345678\"}],\"type\":\"com.symphony.user.mention\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertTrue(matchResult.isMatching());
    assertEquals(2, matchResult.getArguments().getArgumentNames().size());

    final Mention mention = matchResult.getArguments().getMention(mentionArgName);
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
    assertEquals(1, matchResult.getArguments().getArgumentNames().size());

    final Cashtag cashtag = matchResult.getArguments().getCashtag(argName);
    assertEquals("$mycashtag", cashtag.getText());
    assertEquals("mycashtag", cashtag.getValue());
  }

  @Test
  void oneCashtagIsNotMatchingStringPattern() {
    SlashCommandPattern pattern = new SlashCommandPattern("{myarg}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof StringArgumentCommandToken);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">$mycashtag</span>",
        "{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.fin.security.id.ticker\",\"value\":\"mycashtag\"}],\"type\":\"org.symphonyoss.fin.security\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertFalse(matchResult.isMatching());
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
    assertEquals(1, matchResult.getArguments().getArgumentNames().size());

    final Hashtag cashtag = matchResult.getArguments().getHashtag(argName);
    assertEquals("#myhashtag", cashtag.getText());
    assertEquals("myhashtag", cashtag.getValue());
  }

  @Test
  void oneHashtagIsNotMatchingStringPattern() {
    SlashCommandPattern pattern = new SlashCommandPattern("{myarg}");

    final List<CommandToken> tokens = pattern.getTokens();
    assertEquals(1, tokens.size());
    assertTrue(tokens.get(0) instanceof StringArgumentCommandToken);

    V4Message message = buildMessage("<span class=\"entity\" data-entity-id=\"0\">#myhashtag</span>",
        "{\"0\":{\"id\":[{\"type\":\"org.symphonyoss.taxonomy.hashtag\",\"value\":\"myhashtag\"}],\"type\":\"org.symphonyoss.taxonomy\"}}");

    final MatchResult matchResult = pattern.getMatchResult(message);
    assertFalse(matchResult.isMatching());
  }

  @Test
  void oneStaticAndOneArgumentGluedShouldBeInvalid() {
    assertThrows(SlashCommandSyntaxException.class, () -> new SlashCommandPattern("/command{arg}"));
  }

  @Test
  void twoGluedArgumentsShouldBeInvalid() {
    assertThrows(SlashCommandSyntaxException.class, () -> new SlashCommandPattern("{arg1}{arg2}"));
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

  private void assertIsRegexToken(String expectedRegex, CommandToken actualToken) {
    assertTrue(actualToken instanceof RegexCommandToken);
    assertEquals(expectedRegex, ((RegexCommandToken) actualToken).getRegexPattern().pattern());
  }

  private void assertIsStringArgumentToken(CommandToken actualToken) {
    assertTrue(actualToken instanceof StringArgumentCommandToken);
    final StringArgumentCommandToken argumentToken = (StringArgumentCommandToken) actualToken;
    assertEquals(ARGUMENT_VALUE_REGEX, argumentToken.getRegexPattern().pattern());
  }
}
