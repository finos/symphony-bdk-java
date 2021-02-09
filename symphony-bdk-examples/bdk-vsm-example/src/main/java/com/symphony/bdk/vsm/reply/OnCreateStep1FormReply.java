package com.symphony.bdk.vsm.reply;

import static java.util.Collections.singletonList;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.form.FormReplyActivity;
import com.symphony.bdk.core.activity.form.FormReplyContext;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.vsm.poll.Poll;
import com.symphony.bdk.vsm.poll.PollService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnCreateStep1FormReply extends FormReplyActivity<FormReplyContext> {

  private final MessageService messageService;
  private final PollService pollService;

  @Override
  protected ActivityMatcher<FormReplyContext> matcher() {
    return c -> c.getFormId().equals("create-step-1");
  }

  @Override
  @Transactional
  protected void onActivity(FormReplyContext context) {

    final Poll poll = this.pollService.findFromCreationMessage(context.getSourceEvent().getFormMessageId());

    poll.setTitle(context.getFormValue("title"));
    poll.setDescription(context.getFormValue("description"));

    final Message message = Message.builder()
        .template(this.messageService.templates().newTemplateFromClasspath("/templates/create-step-2.ftl"), poll)
        .build();

    this.messageService.sendFacet(
        context.getSourceEvent().getStream().getStreamId(),
        context.getSourceEvent().getFormMessageId(),
        singletonList(context.getInitiator().getUser().getUserId()),
        message
    );

    this.pollService.save(poll);
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.COMMAND).name("Step 1 Form Reply");
  }
}
