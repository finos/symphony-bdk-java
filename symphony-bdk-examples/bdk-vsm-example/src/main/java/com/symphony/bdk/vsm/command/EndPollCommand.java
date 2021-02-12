package com.symphony.bdk.vsm.command;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.spring.annotation.Slash;
import com.symphony.bdk.vsm.poll.Poll;
import com.symphony.bdk.vsm.poll.PollService;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndPollCommand {

  private final MessageService messageService;
  private final StreamService streamService;

  private final PollService pollService;

  @Slash("end")
  @Transactional
  public void createPoll(CommandContext context) {
    if (this.canEndPoll(context)) {

      final String streamId = context.getStreamId();
      final Long userId = context.getInitiator().getUser().getUserId();

      final Poll poll = this.pollService.findActiveByUserAndStream(userId, streamId).get();

      final PollResult pollResult = new PollResult(
          poll.getTitle(),
          poll.getDescription(),
          this.pollService.end(poll),
          LocalDateTime.now().toString()
      );

      this.messageService.sendFacet(
          streamId,
          poll.getPollMessageId(),
          Collections.emptyList(),
          Message.builder().template(this.messageService.templates().newTemplateFromClasspath("/templates/poll-results.ftl"), pollResult).build()
      );
    }
  }

  @Data
  @RequiredArgsConstructor
  public static class PollResult {
    private final String title;
    private final String description;
    private final Map<String, Integer> results;
    private final String endTime;
  }

  private boolean canEndPoll(CommandContext context) {

    final String streamId = context.getStreamId();
    final Long userId = context.getInitiator().getUser().getUserId();

    if (context.getSourceEvent().getMessage().getStream().getStreamType().equalsIgnoreCase("im")) {
      this.messageService.send(streamId, "This command can only be used from ROOM.");
      return false;
    }

    if (!this.pollService.findActiveByUserAndStream(userId, streamId).isPresent()) {
      this.messageService.send(streamId, "Sorry <mention uid=\"" + userId
          + "\"/> but you don't have any active poll in this room. Please create a new one starting with the <b>new</b> command.");
      return false;
    }

    return true;
  }
}
