package com.symphony.bdk.core.activity.parsing;

import lombok.Getter;
import org.apiguardian.api.API;

import java.util.Collections;
import java.util.Map;

@API(status = API.Status.INTERNAL)
@Getter
public class MatchResult {

  private boolean isMatching;
  private Map<String, String> arguments;

  public MatchResult(boolean isMatching) {
    this(isMatching, Collections.emptyMap());
  }

  public MatchResult(boolean isMatching, Map<String, String> arguments) {
    this.isMatching = isMatching;
    this.arguments = arguments;
  }
}
