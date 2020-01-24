package com.symphony.ms.bot.sdk.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.UsersClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@ExtendWith(MockitoExtension.class)
public class TemplateHandlerTest {

  private static final String BOT_DISPLAY_NAME = "BotDisplayName";

  @InjectMocks
  private TemplateHandler templateHandler;

  @Mock
  private UsersClient usersClient;

  @BeforeEach
  public void init() {
    when(usersClient.getBotDisplayName()).thenReturn(BOT_DISPLAY_NAME);
    templateHandler.init();
  }

  @Test
  public void shouldMatchWithPatterns() {
    Predicate<String> predicate = templateHandler.getCommandMatcher();

    assertTrue(predicate.test("@" + BOT_DISPLAY_NAME + " /template"));
    assertTrue(predicate.test("@" + BOT_DISPLAY_NAME + " /template any"));
  }

  @Test
  public void shouldNotMatchWithPatterns() {
    Predicate<String> predicate = templateHandler.getCommandMatcher();

    assertFalse(predicate.test(""));
    assertFalse(predicate.test(" "));
    assertFalse(predicate.test("@" + BOT_DISPLAY_NAME));
    assertFalse(predicate.test("@" + BOT_DISPLAY_NAME + " /any"));
    assertFalse(predicate.test("@" + BOT_DISPLAY_NAME + " /any any"));
  }

  @Test
  public void shouldDisplayInvalidTemplateMessageDuePatternMissMatch() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME);
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertInvalidTemplateMessage(elementsResponse);
  }

  @Test
  public void shouldDisplayInvalidTemplateMessageDueMissingTypeName() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertInvalidTemplateMessage(elementsResponse);
  }

  @Test
  public void shouldDisplayInvalidTemplateMessageDueInvalidTypeName() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template invalid");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertInvalidTemplateMessage(elementsResponse);
  }

  @Test
  public void shouldDisplaySimpleForm() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template simple");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertForm(elementsResponse, "simple-template");
  }

  @Test
  public void shouldDisplayAlertForm() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template alert");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertForm(elementsResponse, "alert-template");
  }

  @Test
  public void shouldDisplayInformationForm() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template information");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertForm(elementsResponse, "information-template");
  }

  @Test
  public void shouldDisplayNotificationForm() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template notification");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertForm(elementsResponse, "notification-template");
  }

  @Test
  public void shouldDisplayListForm() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template list");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertForm(elementsResponse, "list-template");
  }

  @Test
  public void shouldDisplayTableForm() {
    MessageEvent messageEvent = new MessageEvent();
    messageEvent.setMessage("@" + BOT_DISPLAY_NAME + " /template table");
    BotCommand command = new BotCommand(null, messageEvent, null);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.displayElements(command, elementsResponse);

    assertForm(elementsResponse, "table-template");
  }

  @Test
  public void shouldHandleSimpleAction() {
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "simple");
    formValues.put("title", "title");
    formValues.put("content", "content");
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("simple", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof Map);
    assertEquals(2, ((Map) message).size());
    assertEquals("title", ((Map) message).get("title"));
    assertEquals("content", ((Map) message).get("content"));
  }

  @Test
  public void shouldHandleAlertAction() {
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "simple");
    formValues.put("title", "title");
    formValues.put("content", "content");
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("simple", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof Map);
    assertEquals(2, ((Map) message).size());
    assertEquals("title", ((Map) message).get("title"));
    assertEquals("content", ((Map) message).get("content"));
  }

  @Test
  public void shouldHandleInformationAction() {
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "simple");
    formValues.put("title", "title");
    formValues.put("content", "content");
    formValues.put("description", "description");
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("simple", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof Map);
    assertEquals(3, ((Map) message).size());
    assertEquals("title", ((Map) message).get("title"));
    assertEquals("content", ((Map) message).get("content"));
    assertEquals("description", ((Map) message).get("description"));
  }

  @Test
  public void shouldHandleNotificationAction() throws SymphonyClientException {
    SymphonyUser symphonyUser = mock(SymphonyUser.class);
    when(symphonyUser.getDisplayName()).thenReturn("assignee");
    when(usersClient.getUserFromId(anyLong(), anyBoolean())).thenReturn(symphonyUser);
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "notification");
    formValues.put("title", "title");
    formValues.put("alert", "false");
    formValues.put("contentHeader", "header");
    formValues.put("contentBody", "body");
    formValues.put("description", "description");
    formValues.put("comment", "comment");
    formValues.put("assignee", Collections.singletonList(123L));
    formValues.put("showStatusBar", "true");
    formValues.put("type", "type");
    formValues.put("priority", "priority");
    formValues.put("status", "status");
    formValues.put("labels", "label1, label2, label3");
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("notification", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof Map);
    assertEquals(11, ((Map) message).size());
    assertEquals("title", ((Map) message).get("title"));
    assertEquals(false, ((Map) message).get("alert"));
    assertEquals("description", ((Map) message).get("description"));
    Object comment = ((Map) message).get("comment");
    assertNotNull(comment);
    assertTrue(comment instanceof Map);
    assertEquals("comment", ((Map) comment).get("body"));
    Object assignee = ((Map) message).get("assignee");
    assertEquals("assignee", ((Map) assignee).get("displayName"));
    assertEquals(true, ((Map) message).get("showStatusBar"));
    Object type = ((Map) message).get("type");
    assertTrue(type instanceof Map);
    assertEquals("type", ((Map) type).get("name"));
    Object priority = ((Map) message).get("priority");
    assertTrue(priority instanceof Map);
    assertEquals("priority", ((Map) priority).get("name"));
    Object status = ((Map) message).get("status");
    assertTrue(status instanceof Map);
    assertEquals("status", ((Map) status).get("name"));
    Object labels = ((Map) message).get("labels");
    assertNotNull(labels);
    assertTrue(labels instanceof List);
    assertEquals(3, ((List) labels).size());
    for (int i = 0; i < 3; i++) {
      Object label = ((List) labels).get(i);
      assertTrue(label instanceof Map);
      assertEquals("label" + (i + 1), ((Map) label).get("text"));
    }
    Object content = ((Map) message).get("content");
    assertNotNull(content);
    assertTrue(content instanceof Map);
    assertEquals("header", ((Map) content).get("header"));
    assertEquals("body", ((Map) content).get("body"));
  }

  @Test
  public void shouldHandleSimpleListAction() {
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "list");
    formValues.put("title", "title");
    formValues.put("content", "body1, body2, body3");
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("list", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof Map);
    assertEquals(2, ((Map) message).size());
    assertEquals("title", ((Map) message).get("title"));
    Object content = ((Map) message).get("content");
    assertNotNull(content);
    assertTrue(content instanceof List);
    assertEquals(3, ((List) content).size());
    assertEquals("body1", ((List) content).get(0));
    assertEquals("body2", ((List) content).get(1));
    assertEquals("body3", ((List) content).get(2));
  }

  @Test
  public void shouldHandleStructuredListAction() {
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "list");
    formValues.put("title", "title");
    formValues.put("content", "[ header1 | body1 ] [ header2 | body2 ] [ header3 | body3 ]");
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("list", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof Map);
    assertEquals(2, ((Map) message).size());
    assertEquals("title", ((Map) message).get("title"));
    Object content = ((Map) message).get("content");
    assertNotNull(content);
    assertTrue(content instanceof List);
    assertEquals(3, ((List) content).size());
    for (int i = 0; i < 3; i++) {
      Object contentElement = ((List) content).get(i);
      assertNotNull(contentElement);
      assertTrue(contentElement instanceof Map);
      assertEquals("header" + (i + 1), ((Map) contentElement).get("header"));
      assertEquals("body" + (i + 1), ((Map) contentElement).get("body"));
    }
  }

  @Test
  public void shouldHandleTableAction() {
    Map<String, Object> formValues = new HashMap<>();
    formValues.put("action", "table");
    formValues.put("content",
        "[ header1 | header2 | header3 ]" +
            "[ cell1 | cell2 | cell3 ]" +
            "[ cell4 | cell5 | cell6 ]" +
            "[ cell7 | cell8 | cell9 ]"
    );
    SymphonyElementsEvent symphonyElementsEvent = new SymphonyElementsEvent();
    symphonyElementsEvent.setFormValues(formValues);
    SymphonyMessage elementsResponse = new SymphonyMessage();

    templateHandler.handleAction(symphonyElementsEvent, elementsResponse);

    assertNotNull(elementsResponse);
    assertEquals("table", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals(1, ((Map) templateData).size());
    Object message = ((Map) templateData).get("message");
    assertNotNull(message);
    assertTrue(message instanceof List);
    assertEquals(3, ((List) message).size());
    for (int i = 0; i < 3; i++) {
      Object messageElement = ((List) message).get(i);
      assertTrue(messageElement instanceof Map);
      assertEquals(3, ((Map) messageElement).size());
      for (int j = 0; j < 3; j++) {
        Object messageElementValue = ((Map) messageElement).get("header" + (j + 1));
        assertEquals("cell" + (3 * i + j + 1), messageElementValue);
      }
    }
  }

  private void assertInvalidTemplateMessage(SymphonyMessage elementsResponse) {
    assertNotNull(elementsResponse);
    assertEquals("invalid-template", elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals("@" + BOT_DISPLAY_NAME, ((Map) templateData).get("botMention"));
    Object descriptions = ((Map) templateData).get("descriptions");
    assertNotNull(descriptions);
    assertTrue(descriptions instanceof String[]);
    String[] expectedDescriptions = new String[] {
        "/template simple",
        "/template alert",
        "/template information",
        "/template notification",
        "/template list",
        "/template table"
    };
    for (int i = 0; i < ((String[]) descriptions).length; i++) {
      assertEquals(expectedDescriptions[i], ((String[]) descriptions)[i]);
    }
  }

  private void assertForm(SymphonyMessage elementsResponse, String expectedTemplateFile) {
    assertNotNull(elementsResponse);
    assertEquals(expectedTemplateFile, elementsResponse.getTemplateFile());
    Object templateData = elementsResponse.getTemplateData();
    assertNotNull(templateData);
    assertTrue(templateData instanceof Map);
    assertEquals("template-form", ((Map) templateData).get("form-id"));
  }

}
