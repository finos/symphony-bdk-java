package com.symphony.bdk.core.activity.command;

import java.util.List;
import java.util.Map;

public class CommandParser {

  private String commandPattern;

  public CommandParser(String commandPattern) {
    this.commandPattern = commandPattern;
  }

  public List<String> getArgumentNames() {
    return null;
  }

  public Map<String, String> getArguments() {
    return null;
  }
}
