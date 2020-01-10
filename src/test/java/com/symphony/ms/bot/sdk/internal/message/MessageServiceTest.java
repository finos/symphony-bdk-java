package com.symphony.ms.bot.sdk.internal.message;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.ms.bot.sdk.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SendMessageException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

  @Mock
  private SymphonyService symphonyService;

  @Mock
  private TemplateService templateService;

  @Mock
  private JsonMapper jsonMapper;

  @InjectMocks
  private MessageServiceImpl messageService;

  @Test
  public void sendMessageErrorTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.getMessage()).thenReturn("some message");
    when(message.hasTemplate()).thenReturn(false);
    when(message.isEnrichedMessage()).thenReturn(false);
    doThrow(new SendMessageException())
      .when(symphonyService).sendMessage(any(), any(), any());

    messageService.sendMessage("1234", message);
  }

  @Test
  public void sendSimpleMessageTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.getMessage()).thenReturn("some message");
    when(message.hasTemplate()).thenReturn(false);
    when(message.isEnrichedMessage()).thenReturn(false);

    messageService.sendMessage("1234", message);

    verify(symphonyService, times(1)).sendMessage(
        eq("1234"), eq("some message"), eq(null));
  }

  @Test
  public void sendSimpleMessageWithEnrichedMessageTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.getMessage()).thenReturn("some message");
    when(message.hasTemplate()).thenReturn(false);
    when(message.isEnrichedMessage()).thenReturn(true);
    when(message.getEntityName()).thenReturn("entity.name");
    doReturn("payload data")
      .when(jsonMapper).toEnricherString(anyString(), any(), any());

    messageService.sendMessage("1234", message);

    verify(symphonyService, times(1)).sendMessage(
        eq("1234"),
        eq("<div class='entity' data-entity-id='entity.name'>some message</div>"),
        eq("payload data"));
  }

  @Test
  public void sendTemplateStringMessageTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.hasTemplate()).thenReturn(true);
    when(message.usesTemplateFile()).thenReturn(false);
    doReturn("some template message")
      .when(templateService).processTemplateString(any(), any());
    when(message.isEnrichedMessage()).thenReturn(false);

    messageService.sendMessage("1234", message);

    verify(symphonyService, times(1)).sendMessage(
        eq("1234"), eq("some template message"), eq(null));
  }

  @Test
  public void sendTemplateStringMessageWithEnrichedMessageTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.hasTemplate()).thenReturn(true);
    when(message.usesTemplateFile()).thenReturn(false);
    doReturn("some template message")
      .when(templateService).processTemplateString(any(), any());
    when(message.isEnrichedMessage()).thenReturn(true);
    when(message.getEntityName()).thenReturn("entity.name");
    doReturn("payload data")
      .when(jsonMapper).toEnricherString(anyString(), any(), any());

    messageService.sendMessage("1234", message);

    verify(symphonyService, times(1)).sendMessage(
        eq("1234"),
        eq("<div class='entity' data-entity-id='entity.name'>some template message</div>"),
        eq("payload data"));
  }

  @Test
  public void sendTemplateFileMessageTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.hasTemplate()).thenReturn(true);
    when(message.usesTemplateFile()).thenReturn(true);
    doReturn("some template file")
      .when(templateService).processTemplateFile(any(), any());
    when(message.isEnrichedMessage()).thenReturn(false);

    messageService.sendMessage("1234", message);

    verify(symphonyService, times(1)).sendMessage(
        eq("1234"), eq("some template file"), eq(null));
  }

  @Test
  public void sendTemplateFileMessageWithEnrichedMessageTest() {
    SymphonyMessage message = mock(SymphonyMessage.class);
    when(message.hasTemplate()).thenReturn(true);
    when(message.usesTemplateFile()).thenReturn(true);
    doReturn("some template file")
      .when(templateService).processTemplateFile(any(), any());
    when(message.isEnrichedMessage()).thenReturn(true);
    when(message.getEntityName()).thenReturn("entity.name");
    doReturn("payload data")
      .when(jsonMapper).toEnricherString(anyString(), any(), any());

    messageService.sendMessage("1234", message);

    verify(symphonyService, times(1)).sendMessage(
        eq("1234"),
        eq("<div class='entity' data-entity-id='entity.name'>some template file</div>"),
        eq("payload data"));
  }

}
