package com.symphony.bdk.examples.activity;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Example {

  public static void main(String[] args) throws Exception {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));
    // register Hello FormReply Activity within the registry
    bdk.activities().register(new HelloFormReplyActivity(bdk.messages()));
    // finally, start the datafeed loop
    bdk.datafeed().start();
  }
}

@Slf4j
class HelloFormReplyActivity extends FormReplyActivity<FormReplyContext> {

  private final MessageService messageService;

  public HelloFormReplyActivity(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  protected ActivityMatcher<FormReplyContext> matcher() {
    return c -> "hello-form".equals(c.getFormId()) && "submit".equals(c.getFormValue("action"));
  }

  @Override
  protected void onActivity(FormReplyContext context) {
    final String message = "Hello, " + context.getFormValue("name") + "!";
    this.messageService.send(context.getSourceEvent().getStream(), "<messageML>" + message + "</messageML>");
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo(ActivityType.FORM, "Hello Form Reply Activity", "");
  }
}
