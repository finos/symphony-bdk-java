package com.symphony.bdk.vsm.command;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.http.api.ApiRuntimeException;
import com.symphony.bdk.spring.annotation.Slash;
import com.symphony.bdk.vsm.poll.Poll;
import com.symphony.bdk.vsm.poll.PollService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreatePollCommand {

  private final MessageService messageService;
  private final StreamService streamService;

  private final PollService pollService;

  @Slash("new")
  @Transactional
  public void createPoll(CommandContext context) {
    if (this.canCreatePoll(context)) {

      // create or retrieve IM for poll creation
      final Stream im = this.streamService.create(context.getInitiator().getUser().getUserId());
      final String streamId = context.getSourceEvent().getMessage().getStream().getStreamId();

      try {

        final V4Message creationMessage = this.messageService.sendStatefulMessage(
            im.getId(),
            Message.builder()
                .template(this.messageService.templates().newTemplateFromClasspath("/templates/create-step-1.ftl"))
                .build()
        );

        final Poll poll = this.pollService.createPoll(
            context.getInitiator().getUser(),
            this.streamService.getRoomInfo(streamId),
            creationMessage
        );

        log.info("Poll created with id {}", poll.getId());
      } catch (ApiRuntimeException ex) {
        if (ex.getCode() == 403) {
          this.messageService.send(streamId, "<emoji shortcode=\"warning\"/> VSM feature is not enabled. Please contact Symphony support.");
        } else {
          log.error("Cannot create stateful message", ex);
        }
      }
    }
  }

  private boolean canCreatePoll(CommandContext context) {

    final String streamId = context.getStreamId();
    final Long userId = context.getInitiator().getUser().getUserId();

    if (context.getSourceEvent().getMessage().getStream().getStreamType().equalsIgnoreCase("im")) {
      this.messageService.send(streamId, "This command can only be used from ROOM.");
      return false;
    }

    if (this.pollService.hasInProgressPoll(userId, streamId)) {
      this.messageService.send(streamId, "Sorry <mention uid=\"" + userId
          + "\"/> but you already have 1 active poll for this room.");
      return false;
    }

    return true;
  }
}
