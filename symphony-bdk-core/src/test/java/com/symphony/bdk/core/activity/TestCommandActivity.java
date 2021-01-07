package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;

import lombok.Setter;

import java.util.function.Function;

/**
 * Dummy {@link CommandActivity} implementation for testing purpose.
 */
public class TestCommandActivity extends CommandActivity<CommandContext> {

  @Setter private Function<CommandContext, Boolean> matcher = c -> true;

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo(ActivityType.COMMAND, "", "");
  }

  @Override
  public ActivityMatcher<CommandContext> matcher() {
    return this.matcher::apply;
  }

  @Override
  public void onActivity(CommandContext context) {

  }
}
