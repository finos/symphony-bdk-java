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

  private final ActivityInfo activityInfo;

  public TestCommandActivity(boolean isBotMentionRequired) {
    this.activityInfo =
        new ActivityInfo().type(ActivityType.COMMAND).name("/test").requiresBotMention(isBotMentionRequired);
  }

  public TestCommandActivity() {
    this(false);
  }

  @Setter private Function<CommandContext, Boolean> matcher = c -> true;

  @Override
  protected ActivityInfo info() {
    return this.activityInfo;
  }

  @Override
  public ActivityMatcher<CommandContext> matcher() {
    return this.matcher::apply;
  }

  @Override
  public void onActivity(CommandContext context) {

  }
}
