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
import com.symphony.bdk.vsm.poll.Vote;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OnPollStep1FormReply extends FormReplyActivity<FormReplyContext> {

  private final MessageService messageService;
  private final PollService pollService;

  @Override
  protected ActivityMatcher<FormReplyContext> matcher() {
    return c -> c.getFormId().equals("poll-step-1");
  }

  @Override
  protected void onActivity(FormReplyContext context) {

    final long voterId = context.getInitiator().getUser().getUserId();
    final String choice = context.getFormValue("choice");

    final Poll poll = this.pollService.findById(Long.parseLong(context.getFormValue("pollId")));
    final Vote vote = this.pollService.vote(poll, voterId, choice);

    final Message message = Message.builder()
        .template(
            this.messageService.templates().newTemplateFromClasspath("/templates/poll-step-final.ftl"),
            new VoteResult(poll.getTitle(), poll.getDescription(), this.pollService.getVoteValue(poll, vote)))
        .build();

    this.messageService.sendFacet(
        context.getSourceEvent().getStream().getStreamId(),
        context.getSourceEvent().getFormMessageId(),
        singletonList(context.getInitiator().getUser().getUserId()),
        message
    );
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.FORM).name("Poll Step 1 Form Reply");
  }

  @Data
  public static class VoteResult {
    private final String title;
    private final String description;
    private final String value;
  }
}
