package com.symphony.ms.bot.sdk.command;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.ms.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.event.model.UserDetails;
import com.symphony.ms.bot.sdk.internal.lib.jsonmapper.JsonMapperImpl;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.UsersClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TemplateCommandHandlerTest {

  @InjectMocks
  private TemplateCommandHandler templateCommandHandler;

  @BeforeEach
  public void setup() {
    templateCommandHandler = new TemplateCommandHandler(new JsonMapperImpl(new ObjectMapper()));

    UsersClient usersClient = mock(UsersClient.class);
    when(usersClient.getBotDisplayName()).thenReturn("botDisplayName");
    templateCommandHandler.setUsersClient(usersClient);
  }

  @Test
  public void shouldAskForTemplateDueMissMatch() {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /test");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getMessage());
    assertEquals("Please, specify the name of a valid template",
        commandResponse.getMessage());
  }

  @Test
  public void shouldAskForTemplateDueMissingTemplate() {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getMessage());
    assertEquals("Please, specify the name of a valid template",
        commandResponse.getMessage());
  }

  @Test
  public void shouldAskForParameter() {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template simple");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getMessage());
    assertEquals("Please, provide a valid parameter to the specified template",
        commandResponse.getMessage());
  }

  @Test
  public void shouldSetSimpleTemplate() throws JsonProcessingException {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template simple {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getTemplateFile());
    assertEquals("simple", commandResponse.getTemplateFile());
    assertNotNull(commandResponse.getTemplateData());
    assertEquals("{\"message\":{\"parameter\":\"value\"}}",
        new ObjectMapper().writeValueAsString(commandResponse.getTemplateData()));
  }

  @Test
  public void shouldSetAlertTemplate() throws JsonProcessingException {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template alert {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getTemplateFile());
    assertEquals("alert", commandResponse.getTemplateFile());
    assertNotNull(commandResponse.getTemplateData());
    assertEquals("{\"message\":{\"parameter\":\"value\"}}",
        new ObjectMapper().writeValueAsString(commandResponse.getTemplateData()));
  }

  @Test
  public void shouldSetInformationTemplate() throws JsonProcessingException {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template information {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getTemplateFile());
    assertEquals("information", commandResponse.getTemplateFile());
    assertNotNull(commandResponse.getTemplateData());
    assertEquals("{\"message\":{\"parameter\":\"value\"}}",
        new ObjectMapper().writeValueAsString(commandResponse.getTemplateData()));
  }

  @Test
  public void shouldSetNotificationTemplate() throws JsonProcessingException {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template notification {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getTemplateFile());
    assertEquals("notification", commandResponse.getTemplateFile());
    assertNotNull(commandResponse.getTemplateData());
    assertEquals("{\"message\":{\"parameter\":\"value\"}}",
        new ObjectMapper().writeValueAsString(commandResponse.getTemplateData()));
  }

  @Test
  public void shouldSetListTemplate() throws JsonProcessingException {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template list {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getTemplateFile());
    assertEquals("list", commandResponse.getTemplateFile());
    assertNotNull(commandResponse.getTemplateData());
    assertEquals("{\"message\":{\"parameter\":\"value\"}}",
        new ObjectMapper().writeValueAsString(commandResponse.getTemplateData()));
  }

  @Test
  public void shouldSetTableTemplate() throws JsonProcessingException {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template table {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getTemplateFile());
    assertEquals("table", commandResponse.getTemplateFile());
    assertNotNull(commandResponse.getTemplateData());
    assertEquals("{\"message\":{\"parameter\":\"value\"}}",
        new ObjectMapper().writeValueAsString(commandResponse.getTemplateData()));
  }

  @Test
  public void shouldAskForTemplateDueInvalidTemplate() {
    UserDetails userDetails = new UserDetails();
    userDetails.setUserId("userId");
    MessageEvent event = new MessageEvent();
    event.setMessage("@botDisplayName /template invalid {\"parameter\": \"value\"}");
    event.setUser(userDetails);
    BotCommand command = new BotCommand(
        TemplateCommandHandler.class.getCanonicalName(), event, mock(CommandDispatcher.class));
    SymphonyMessage commandResponse = new SymphonyMessage();

    templateCommandHandler.init();
    templateCommandHandler.handle(command, commandResponse);

    assertNotNull(commandResponse);
    assertNotNull(commandResponse.getMessage());
    assertEquals("Please, specify the name of a valid template",
        commandResponse.getMessage());
  }

}
