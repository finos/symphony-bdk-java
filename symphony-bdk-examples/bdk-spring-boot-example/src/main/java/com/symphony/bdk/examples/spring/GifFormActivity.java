package com.symphony.bdk.examples.spring;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.spring.annotation.Slash;
import com.symphony.bdk.template.api.Template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Sample Gif Form Activity:
 * <ul>
 *   <li>send Elements form on slash command <code>@BotMention /gif</code></li>
 *   <li>handle form reply when user submits the form</li>
 * </ul>
 */
@Slf4j
@Component
public class GifFormActivity extends FormReplyActivity<FormReplyContext> {

  @Autowired
  private MessageService messageService;

  @Slash(value = "/gif")
  public void displayGifForm(CommandContext context) {
    Template template = this.messageService.templates().newTemplateFromClasspath("/templates/gif.ftl");
    Message message = Message.builder().template(template).build();
    this.messageService.send(context.getStreamId(), message);
  }

  @Override
  public ActivityMatcher<FormReplyContext> matcher() {
    return context -> "gif-category-form".equals(context.getFormId())
        && "submit".equals(context.getFormValue("action"))
        && StringUtils.isNotEmpty(context.getFormValue("category"));
  }

  @Override
  public void onActivity(FormReplyContext context) {
    this.messageService.send(context.getStreamId(),
        String.format("Gif category is \"%s\"", context.getFormValue("category")));
  }

  @Override
  public boolean isAsynchronous() {
    return true;
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo()
        .type(ActivityType.FORM)
        .name("Gif Display category form command")
        .description("\"Form handler for the Gif Category form\"");
  }
}
