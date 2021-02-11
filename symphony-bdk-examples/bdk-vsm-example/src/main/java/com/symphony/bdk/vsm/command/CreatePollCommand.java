package com.symphony.bdk.vsm.command;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.V4Message;
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
    // TODO create ticket to add it to the SlashCommand class and @Slash annotation
    if (!context.getSourceEvent().getMessage().getStream().getStreamType().equalsIgnoreCase("im")) {

      final Stream im = this.streamService.create(context.getInitiator().getUser().getUserId());

      final V4Message creationMessage = this.messageService.sendStatefulMessage(
          im.getId(),
          Message.builder()
              .template(this.messageService.templates().newTemplateFromClasspath("/templates/create-step-1.ftl"))
              .build()
      );

      final Poll poll = this.pollService.createPoll(
          context.getInitiator().getUser(),
          this.streamService.getRoomInfo(context.getSourceEvent().getMessage().getStream().getStreamId()),
          creationMessage
      );
      log.info("Poll created with id {}", poll.getId());

    } else {
      this.messageService.send(context.getStreamId(), "This command can only be used from ROOM.");
    }
  }
}
