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

import com.symphony.bdk.vsm.poll.PollStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnCreateStep2FormReply extends FormReplyActivity<FormReplyContext> {

  private final MessageService messageService;
  private final PollService pollService;

  @Override
  protected ActivityMatcher<FormReplyContext> matcher() {
    return c -> c.getFormId().equals("create-step-2");
  }

  @Override
  @Transactional
  protected void onActivity(FormReplyContext context) {

    final Poll poll = this.pollService.findFromCreationMessage(context.getSourceEvent().getFormMessageId());

    final Message message = Message.builder()
        .content("Your poll has successfully been sent to room <b>" + poll.getRoomName() + "</b>")
        .build();

    this.messageService.sendFacet(
        context.getSourceEvent().getStream().getStreamId(),
        context.getSourceEvent().getFormMessageId(),
        singletonList(context.getInitiator().getUser().getUserId()),
        message
    );

    poll.setType(context.getFormValue("type"));
    poll.setOption1(context.getFormValue("option1"));
    poll.setOption2(context.getFormValue("option2"));
    poll.setOption3(context.getFormValue("option3"));
    poll.setOption4(context.getFormValue("option4"));
    poll.setStatus(PollStatus.PENDING);

    this.pollService.save(poll);
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.COMMAND).name("Step 2 Form Reply");
  }
}
