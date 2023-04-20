package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;

import com.symphony.bdk.core.activity.model.ActivityType;

import lombok.Setter;

import java.util.Objects;
import java.util.function.Function;

/**
 * Dummy {@link CommandActivity} implementation for testing purpose.
 */
public class TestCommandActivity extends CommandActivity<CommandContext> {

  private final ActivityInfo activityInfo;
  private final boolean requiresBotMention;

  public TestCommandActivity(String name) {
    this(name, true);
  }

  public TestCommandActivity(String name, boolean requiresBotMention) {
    this.activityInfo = new ActivityInfo().type(ActivityType.COMMAND).name(name);
    this.requiresBotMention = requiresBotMention;
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

  @Override
  public boolean isAsynchronous() {
    return false;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) { return true; }
    if (o == null || getClass() != o.getClass()) { return false; }
    TestCommandActivity that = (TestCommandActivity) o;
    return requiresBotMention == that.requiresBotMention && activityInfo.name().equals(that.activityInfo.name());
  }

  @Override
  public int hashCode() {
    return Objects.hash(activityInfo.name(), requiresBotMention);
  }
}
