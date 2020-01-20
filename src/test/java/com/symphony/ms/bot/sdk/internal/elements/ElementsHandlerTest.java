package com.symphony.ms.bot.sdk.internal.elements;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.ms.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.ms.bot.sdk.internal.command.CommandFilter;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.event.EventDispatcher;
import com.symphony.ms.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.ms.bot.sdk.internal.event.model.SymphonyElementsEvent;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.message.MessageService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.UsersClient;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
public class ElementsHandlerTest {

  @Mock
  private EventDispatcher eventDispatcher;

  @Mock
  private CommandDispatcher commandDispatcher;

  @Mock
  private CommandFilter commandFilter;

  @Mock
  private MessageService messageService;

  @Mock
  private FeatureManager featureManager;

  @Mock
  private UsersClient usersClient;

  @InjectMocks
  private TestElementsHandler elementsHandler;

  static class TestElementsHandler extends ElementsHandler {

    private BiConsumer<BotCommand, SymphonyMessage> internalDisplayElements;
    private BiConsumer<SymphonyElementsEvent, SymphonyMessage> internalHandleAction;
    private Predicate<String> commandMatcher;

    @Override
    protected Predicate<String> getCommandMatcher() {
      if (commandMatcher == null) {
        commandMatcher = Pattern
            .compile("^@BotName /test$")
            .asPredicate();
      }
      return commandMatcher;
    }

    @Override
    protected String getElementsFormId() {
      return "test-form-id";
    }

    @Override
    public void displayElements(BotCommand command,
        SymphonyMessage elementsResponse) {
      if (internalDisplayElements != null) {
        internalDisplayElements.accept(command, elementsResponse);
      }
    }

    @Override
    public void handleAction(SymphonyElementsEvent event,
        SymphonyMessage elementsResponse) {
      if (internalHandleAction != null) {
        internalHandleAction.accept(event, elementsResponse);
      }
    }

    // Helper to ease changing the behavior of displayElements method on each test
    private void setInternalDisplayElements(
        BiConsumer<BotCommand, SymphonyMessage> consumer) {
      this.internalDisplayElements = consumer;
    }

    // Helper to ease changing the behavior of handleAction method on each test
    private void setInternalHandleAction(
        BiConsumer<SymphonyElementsEvent, SymphonyMessage> consumer) {
      this.internalHandleAction = consumer;
    }
  }

  @Test
  public void registerTest() {
    elementsHandler.register();

    verify(commandDispatcher, times(1))
      .register(TestElementsHandler.class.getCanonicalName(), elementsHandler);
    verify(commandFilter, times(1))
      .addFilter(TestElementsHandler.class.getCanonicalName(), elementsHandler.getCommandMatcher());
    verify(eventDispatcher, times(1))
      .register("test-form-id", elementsHandler);
  }

  @Test
  public void onCommandTest() {
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    BotCommand command = mock(BotCommand.class);

    spyElementsHandler.onCommand(command);

    verify(spyElementsHandler, times(1))
      .displayElements(eq(command), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandGetCommandMessageTest() {
    elementsHandler.setInternalDisplayElements((cmd, msg) -> cmd.getMessageEvent());
    BotCommand command = mock(BotCommand.class);

    elementsHandler.onCommand(command);

    verify(command, times(2)).getMessageEvent();
    verify(messageService, never())
        .sendMessage(anyString(), any(SymphonyMessage.class));

  }

  @Test
  public void onCommandGetBotNameTest() {
    elementsHandler.setInternalDisplayElements(
        (cmd, msg) -> elementsHandler.getBotName());
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    BotCommand command = mock(BotCommand.class);

    spyElementsHandler.onCommand(command);

    verify(usersClient, times(1)).getBotDisplayName();
    verify(messageService, never())
      .sendMessage(anyString(), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandSendResponseMessageTest() {
    elementsHandler.setInternalDisplayElements(
        (cmd, msg) -> msg.setMessage("symphony elements form"));
    BotCommand command = mock(BotCommand.class);
    MessageEvent message = mock(MessageEvent.class);
    when(message.getStreamId()).thenReturn("12345");
    when(command.getMessageEvent()).thenReturn(message);

    elementsHandler.onCommand(command);

    verify(messageService, times(1))
        .sendMessage(eq("12345"), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandProcessingErrorFeedbackDisabledTest() {
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    BotCommand command = mock(BotCommand.class);
    doThrow(new RuntimeException())
      .when(spyElementsHandler)
      .displayElements(eq(command), any(SymphonyMessage.class));
    when(featureManager.unexpectedErrorResponse()).thenReturn(null);

    spyElementsHandler.onCommand(command);

    verify(spyElementsHandler, times(1)).getCommandName();
    verify(featureManager, times(1)).unexpectedErrorResponse();
    verify(messageService, never())
      .sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandProcessingErrorWithFeedbackTest() {
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    BotCommand command = mock(BotCommand.class);
    doThrow(new RuntimeException())
        .when(spyElementsHandler)
        .displayElements(eq(command), any(SymphonyMessage.class));
    when(featureManager.unexpectedErrorResponse())
        .thenReturn("some error message");
    MessageEvent message = mock(MessageEvent.class);
    when(message.getStreamId()).thenReturn("STREAM_ID_1234");
    when(command.getMessageEvent()).thenReturn(message);

    spyElementsHandler.onCommand(command);

    verify(spyElementsHandler, times(1)).getCommandName();
    verify(featureManager, times(2)).unexpectedErrorResponse();
    verify(messageService, times(1))
        .sendMessage(eq("STREAM_ID_1234"), any(SymphonyMessage.class));
  }

  @Test
  public void onEventTest() {
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);

    spyElementsHandler.onEvent(event);

    verify(spyElementsHandler, times(1))
      .handleAction(eq(event), any(SymphonyMessage.class));
  }

  @Test
  public void onEventReadEvtDetailsTest() {
    elementsHandler.setInternalHandleAction((evt, msg) -> evt.getFormId());
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);

    elementsHandler.onEvent(event);

    verify(event, times(2)).getFormId();
  }

  @Test
  public void onEventFeedbackDisabledTest() {
    elementsHandler.setInternalHandleAction(
        (cmd, msg) -> msg.setMessage("some response message"));
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);
    when(featureManager.isCommandFeedbackEnabled()).thenReturn(false);

    elementsHandler.onEvent(event);

    verify(featureManager, times(1)).isCommandFeedbackEnabled();
    verify(messageService, never())
      .sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onEventSendResponseMessageTest() {
    elementsHandler.setInternalHandleAction(
        (cmd, msg) -> msg.setMessage("some response message"));
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);
    when(event.getStreamId()).thenReturn("STREAM_ID_1234");
    when(featureManager.isCommandFeedbackEnabled()).thenReturn(true);

    elementsHandler.onEvent(event);

    verify(featureManager, times(1)).isCommandFeedbackEnabled();
    verify(messageService, times(1))
      .sendMessage(eq("STREAM_ID_1234"), any(SymphonyMessage.class));
  }

  @Test
  public void onEventProcessingErrorFeedbackDisabledTest() {
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);
    doThrow(new RuntimeException())
      .when(spyElementsHandler)
      .handleAction(any(SymphonyElementsEvent.class), any(SymphonyMessage.class));
    when(featureManager.unexpectedErrorResponse())
      .thenReturn(null);


    spyElementsHandler.onEvent(event);

    verify(messageService, never())
      .sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onEventProcessingErrorFeedbackEnabledTest() {
    ElementsHandler spyElementsHandler = spy(elementsHandler);
    SymphonyElementsEvent event = mock(SymphonyElementsEvent.class);
    doThrow(new RuntimeException())
      .when(spyElementsHandler)
      .handleAction(any(SymphonyElementsEvent.class), any(SymphonyMessage.class));
    when(featureManager.unexpectedErrorResponse())
      .thenReturn("some error message");
    when(event.getStreamId()).thenReturn("STREAM_ID_1234");


    spyElementsHandler.onEvent(event);

    verify(messageService, times(1))
      .sendMessage(eq("STREAM_ID_1234"), any(SymphonyMessage.class));
  }

}
