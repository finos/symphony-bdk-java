package com.symphony.bdk.core.activity.form;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;

import lombok.Setter;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Dummy {@link FormReplyActivity} implementation for testing purpose.
 */
public class TestFormReplyActivity extends FormReplyActivity<FormReplyContext> {

  @Setter private Function<FormReplyContext, Boolean> matcher = c -> true;
  @Setter private Consumer<FormReplyContext> beforeMatcher = c -> {};
  @Setter private Consumer<FormReplyContext> onActivity = c -> {};

  @Override
  protected ActivityInfo info() {
    return ActivityInfo.of(ActivityType.FORM);
  }

  @Override
  protected void beforeMatcher(FormReplyContext context) {
    super.beforeMatcher(context);
    this.beforeMatcher.accept(context);
  }

  @Override
  public ActivityMatcher<FormReplyContext> matcher() {
    return this.matcher::apply;
  }

  @Override
  public void onActivity(FormReplyContext context) {
    this.onActivity.accept(context);
  }
}
