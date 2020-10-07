package com.symphony.bdk.examples.spring;

import static java.util.Collections.emptyMap;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.spring.annotation.Slash;

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

  @Slash("/gif")
  public void displayGifForm(CommandContext context) {
    this.messageService.sendWithTemplate(context.getStreamId(), "/templates/gif.ftl", emptyMap());
  }

  @Override
  public ActivityMatcher<FormReplyContext> matcher() {
    return context -> "gif-category-form".equals(context.getFormId())
        && "submit".equals(context.getFormValue("action"))
        && StringUtils.isNotEmpty(context.getFormValue("category"));
  }

  @Override
  public void onActivity(FormReplyContext context) {
    log.info("Gif category is \"{}\"", context.getFormValue("category"));
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.FORM)
        .name("Gif Display category form command")
        .description("\"Form handler for the Gif Category form\"");
  }
}
