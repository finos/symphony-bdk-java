package com.symphony.bdk.examples.activity;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.examples.activity.context.GifFormReplyContext;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class GifFormReplyActivity extends FormReplyActivity<GifFormReplyContext> {

  @Override
  public ActivityMatcher<GifFormReplyContext> matcher() {
    return context -> "gif-category-form".equals(context.getFormId())
        && "submit".equals(context.getFormValue("action"))
        && StringUtils.isNotEmpty(context.getFormValue("category"));
  }

  @Override
  public void onActivity(GifFormReplyContext context) {
    log.info("Gif category is \"{}\"", context.getFormValue("category"));
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.FORM)
        .name("Gif Display category form command")
        .description("Form handler for the Gif Category form");
  }
}
