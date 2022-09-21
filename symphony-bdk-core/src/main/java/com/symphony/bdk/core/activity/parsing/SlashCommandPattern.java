package com.symphony.bdk.core.activity.parsing;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.symphony.bdk.core.activity.exception.SlashCommandSyntaxException;
import com.symphony.bdk.gen.api.model.V4Message;

import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/**
 * Class representing the pattern of a {@link com.symphony.bdk.core.activity.command.SlashCommand}.
 * The string should be a list of tokens separated by whitespaces. Each token can be:
 * <ul>
 *   <li>a regular static word like "/command". This will only match the same string</li>
 *   <li>a string argument like "{argument}". This will match a single word (with no whitespaces)</li>
 *   <li>a mention argument like "{{@literal @}mention}. This will match a mention</li>
 *   <li>a cashtag argument like "{$cashtag}" which will match a cashtag</li>
 *   <li>a hashtag argument like "{#cashtag}" which will match a hashtag</li>
 * </ul>
 */
@API(status = API.Status.INTERNAL)
public class SlashCommandPattern {

  // Checking for valid java variable names enclosed by braces, see https://stackoverflow.com/a/17564142
  private static final Pattern TYPED_ARGUMENT_PATTERN = Pattern.compile("^\\{[@$#][a-zA-Z_$][a-zA-Z_$0-9]*\\}$");
  private static final Pattern STRING_ARGUMENT_PATTERN = Pattern.compile("^\\{[a-zA-Z_$][a-zA-Z_$0-9]*\\}$");

  private final List<CommandToken> tokens;

  /**
   * @param pattern the slash command pattern
   * @throws {@link SlashCommandSyntaxException} if the pattern is not well formatted
   */
  public SlashCommandPattern(String pattern) {
    try {
      this.tokens = buildTokens(pattern);
    } catch (PatternSyntaxException e) {
      throw new SlashCommandSyntaxException("Bad slash command pattern."
          + "Slash command pattern must be either words separated by spaces "
          + "or aguments in the format {argumentName}, {@mentionArg}, {#hashtagArg}, {$cashtagArg} separated by spaces",
          e);
    }
  }

  /**
   * @return the list of command tokens
   */
  public List<CommandToken> getTokens() {
    return tokens;
  }

  /**
   * Adds a specific token at the beginning of the slash command pattern
   *
   * @param token the command token to add
   */
  public void prependToken(CommandToken token) {
    tokens.add(0, token);
  }

  /**
   * @return the map of key-value pairs (argument name, argument type)
   */
  public Map<String, ? extends Class<?>> getArgumentDefinitions() {
    return tokens.stream()
        .filter(t -> t instanceof ArgumentCommandToken)
        .collect(Collectors.toMap(t -> ((ArgumentCommandToken) t).getArgumentName(), t -> t.getTokenType()));
  }

  /**
   * @param message the input message to be matched against the {@link SlashCommandPattern}
   * @return the {@link MatchResult} object containing the status (matches or not) and the potential arguments.
   */
  public MatchResult getMatchResult(V4Message message) {
    final List<Object> inputTokens = new InputTokenizer(message).getTokens();

    if (!matches(inputTokens)) {
      return new MatchResult(false);
    }
    return new MatchResult(true, getArguments(inputTokens));
  }

  private boolean matches(List<Object> inputTokens) {
    if (tokens.isEmpty()) {
      return inputTokens.isEmpty();
    }

    if (inputTokens.size() != tokens.size()) {
      return false;
    }

    return matchesEveryToken(inputTokens);
  }

  private boolean matchesEveryToken(List<Object> inputTokens) {
    for (int i = 0; i < tokens.size(); i++) {
      if (!tokens.get(i).matches(inputTokens.get(i))) {
        return false;
      }
    }
    return true;
  }

  private List<CommandToken> buildTokens(String pattern) {
    if (isBlank(pattern)) {
      return new ArrayList<>();
    }

    final String[] tokens = pattern.trim().split("\\s+");
    final List<CommandToken> commandTokens = Arrays.stream(tokens).map(SlashCommandPattern::buildCommandToken).collect(Collectors.toList());

    checkArgumentNamesUniqueness(commandTokens);

    return commandTokens;
  }

  private static CommandToken buildCommandToken(String token) {
    if (TYPED_ARGUMENT_PATTERN.matcher(token).matches()) {
      if (token.startsWith("{@")) {
        return TypedArgumentToken.newInstance(Mention.class, token);
      } else if (token.startsWith("{$")) {
        return TypedArgumentToken.newInstance(Cashtag.class, token);
      } else if (token.startsWith("{#")) {
        return TypedArgumentToken.newInstance(Hashtag.class, token);
      }
    }
    if (STRING_ARGUMENT_PATTERN.matcher(token).matches()) {
      return StringArgumentCommandToken.newInstance(token);
    }
    return new StaticCommandToken(token);
  }

  private Map<String, Object> getArguments(List<Object> inputTokens) {
    // we assume inputTokens are matching
    Map<String, Object> arguments = new HashMap<>();

    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i) instanceof ArgumentCommandToken) {
        arguments.put(((ArgumentCommandToken) tokens.get(i)).getArgumentName(), inputTokens.get(i));
      }
    }
    return arguments;
  }

  private void checkArgumentNamesUniqueness(List<CommandToken> tokens) {
    final List<String> argumentNames = tokens.stream()
        .filter(t -> t instanceof ArgumentCommandToken)
        .map(t -> ((ArgumentCommandToken) t).getArgumentName())
        .collect(Collectors.toList());

    final Set<String> uniqueArgumentNames = new HashSet<>(argumentNames);

    if (argumentNames.size() != uniqueArgumentNames.size()) {
      throw new SlashCommandSyntaxException("Argument names must be unique");
    }
  }
}
