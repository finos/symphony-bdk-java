package com.symphony.bdk.core.activity.parsing;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.symphony.bdk.core.activity.exception.SlashCommandSyntaxException;
import com.symphony.bdk.gen.api.model.V4Message;

import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@API(status = API.Status.INTERNAL)
public class SlashCommandPattern {

  // Checking for valid java variable names enclosed by braces, see https://stackoverflow.com/a/17564142
  private static final Pattern TYPED_ARGUMENT_PATTERN = Pattern.compile("^\\{[@$#][a-zA-Z_$][a-zA-Z_$0-9]*\\}$");
  private static final Pattern STRING_ARGUMENT_PATTERN = Pattern.compile("^\\{[a-zA-Z_$][a-zA-Z_$0-9]*\\}$");

  private final List<CommandToken> tokens;

  public SlashCommandPattern(String pattern) {
    try {
      this.tokens = buildTokens(pattern);
    } catch(PatternSyntaxException e) {
      throw new SlashCommandSyntaxException("Bad slash command pattern."
          + "Slash command pattern must be either words separated by spaces "
          + "or aguments in the format {argumentName}, {@mentionArg}, {#hashtagArg}, {$cashtagArg} separated by spaces", e);
    }
  }

  public List<CommandToken> getTokens() {
    return tokens;
  }

  public void prependToken(CommandToken token) {
    tokens.add(0, token);
  }

  public List<String> getArgumentNames() {
    return tokens.stream()
        .filter(t -> t instanceof StringArgumentCommandToken)
        .map(t -> ((StringArgumentCommandToken) t).getArgumentName())
        .collect(Collectors.toList());
  }

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
      return Collections.emptyList();
    }

    final String[] tokens = pattern.trim().split("\\s+");
    return Arrays.stream(tokens).map(SlashCommandPattern::buildCommandToken).collect(Collectors.toList());
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
}
