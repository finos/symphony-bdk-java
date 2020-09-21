package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;

import org.springframework.stereotype.Component;

/**
 * Simple form reply activity component.
 */
@Component
public class TestFormReplyActivity extends FormReplyActivity<FormReplyContext> {

  @Override
  protected ActivityMatcher<FormReplyContext> matcher() {
    return ActivityMatcher.always();
  }

  @Override
  protected void onActivity(FormReplyContext context) {
    // nothing to be done here
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.FORM);
  }
}
