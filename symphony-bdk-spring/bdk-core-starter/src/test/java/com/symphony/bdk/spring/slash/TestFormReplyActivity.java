package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;

import org.springframework.stereotype.Component;

/**
 * Simple bean that contains a slash method.
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
    return ActivityInfo.of(ActivityType.FORM);
  }
}
