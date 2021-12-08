package com.symphony.bdk.core.activity.parsing;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.symphony.bdk.gen.api.model.V4Message;

import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@API(status = API.Status.INTERNAL)
public class SlashCommandPattern {

  // Checking for valid java variable names enclosed by braces, see https://stackoverflow.com/a/17564142
  private static final Pattern ARGUMENT_PATTERN = Pattern.compile("^\\{[a-zA-Z_$][a-zA-Z_$0-9]*\\}$");

  private final List<CommandToken> tokens;

  public SlashCommandPattern(String pattern) {
    this.tokens = buildTokens(pattern);
  }

  public List<CommandToken> getTokens() {
    return tokens;
  }

  public List<String> getArgumentNames() {
    return tokens.stream()
        .filter(t -> t instanceof ArgumentCommandToken)
        .map(t -> ((ArgumentCommandToken) t).getArgumentName())
        .collect(Collectors.toList());
  }

  public MatchResult getMatchResult(V4Message message) {
    final List<InputToken<?>> inputTokens = new InputTokenizer(message).getTokens();

    if (!matches(inputTokens)) {
      return new MatchResult(false);
    }
    return new MatchResult(true, getArguments(inputTokens));
  }

  private boolean matches(List<InputToken<?>> inputTokens) {
    if (tokens.isEmpty()) {
      return inputTokens.isEmpty();
    }

    if (inputTokens.size() != tokens.size()) {
      return false;
    }

    return matchesEveryToken(inputTokens);
  }

  private boolean matchesEveryToken(List<InputToken<?>> inputTokens) {
    for (int i = 0; i < tokens.size(); i++) {
      if (!tokens.get(i).matches(inputTokens.get(i).getContentAsString())) {
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

  private static CommandToken buildCommandToken(String t) {
    if (ARGUMENT_PATTERN.matcher(t).matches()) {
      return new ArgumentCommandToken(t);
    }
    return new StaticCommandToken(t);
  }

  private Map<String, String> getArguments(List<InputToken<?>> inputTokens) {
    // we assume inputTokens are matching
    Map<String, String> arguments = new HashMap<>();

    for (int i = 0; i < tokens.size(); i++) {
      if (tokens.get(i) instanceof ArgumentCommandToken) {
        arguments.put(((ArgumentCommandToken) tokens.get(i)).getArgumentName(), inputTokens.get(i).getContentAsString());
      }
    }
    return arguments;
  }
}
