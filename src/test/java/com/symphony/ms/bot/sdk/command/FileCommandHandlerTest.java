package com.symphony.ms.bot.sdk.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachment;
import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachmentFile;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.MessageClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class FileCommandHandlerTest {

  @Mock
  private MessageClient messageClient;

  @InjectMocks
  private FileCommandHandler fileCommandHandler;

  @Test
  public void shouldSetNoAttachment() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setUserId("userId");
    BotCommand command = mock(BotCommand.class);
    when(command.getMessageEvent()).thenReturn(messageEvent);
    SymphonyMessage commandResponse = new SymphonyMessage();

    fileCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertEquals("<mention uid=\"userId\"/> message has no attachment",
        commandResponse.getMessage());
    assertNull(commandResponse.getAttachments());
  }

  @Test
  public void shouldSetOneAttachment() throws SymphonyClientException {
    MessageEvent message = new MessageEvent();
    message.setUserId("userId");
    message.setAttachments(Collections.singletonList(new MessageAttachment()));
    BotCommand command = mock(BotCommand.class);
    when(command.getMessageEvent()).thenReturn(message);
    SymphonyMessage commandResponse = new SymphonyMessage();
    when(messageClient.downloadMessageAttachments(any(MessageEvent.class)))
        .thenReturn(Collections.singletonList(mock(MessageAttachmentFile.class)));

    fileCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertEquals("<mention uid=\"userId\"/> message has 1 attachment(s):",
        commandResponse.getMessage());
    assertNotNull(commandResponse.getAttachments());
    assertEquals(1, commandResponse.getAttachments().size());
  }

  @Test
  public void shouldSetManyAttachment() throws SymphonyClientException {
    MessageEvent message = new MessageEvent();
    message.setUserId("userId");
    message.setAttachments(Arrays.asList(
        new MessageAttachment(),
        new MessageAttachment(),
        new MessageAttachment()));
    BotCommand command = mock(BotCommand.class);
    when(command.getMessageEvent()).thenReturn(message);
    SymphonyMessage commandResponse = new SymphonyMessage();
    when(messageClient.downloadMessageAttachments(any(MessageEvent.class))).thenReturn(
        Arrays.asList(
            mock(MessageAttachmentFile.class),
            mock(MessageAttachmentFile.class),
            mock(MessageAttachmentFile.class)));

    fileCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertEquals("<mention uid=\"userId\"/> message has 3 attachment(s):",
        commandResponse.getMessage());
    assertNotNull(commandResponse.getAttachments());
    assertEquals(3, commandResponse.getAttachments().size());
  }

}
