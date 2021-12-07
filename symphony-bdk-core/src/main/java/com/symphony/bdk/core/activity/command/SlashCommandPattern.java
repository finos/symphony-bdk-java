package com.symphony.bdk.core.activity.command;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SlashCommandPattern {

  private final String pattern;
  private final List<CommandToken> tokens;

  public SlashCommandPattern(String pattern) {
    this.pattern = pattern;
    this.tokens = buildTokens();
  }

  public List<CommandToken> getTokens() {
    return tokens;
  }

  public boolean matches(String input) {
    if (getTokens().isEmpty()) {
      return isBlank(input);
    }

    final List<String> inputTokens = Arrays.stream(input.trim().split("\\s+"))
        .filter(s -> isNotBlank(s))
        .collect(Collectors.toList());

    if (inputTokens.size() != tokens.size()) {
      return false;
    }

    for (int i = 0; i < tokens.size(); i++) {
      if (!tokens.get(i).matches(inputTokens.get(i))) {
        return false;
      }
    }
    return true;
  }

  private List<CommandToken> buildTokens() {
    if (isBlank(pattern)) {
      return Collections.emptyList();
    }

    final String[] tokens = pattern.trim().split("\\s+");
    return Arrays.stream(tokens).map(CommandToken::new).collect(Collectors.toList());
  }
}
