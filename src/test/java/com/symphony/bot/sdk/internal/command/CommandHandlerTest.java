package com.symphony.bot.sdk.internal.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bot.sdk.internal.command.CommandDispatcher;
import com.symphony.bot.sdk.internal.command.CommandFilter;
import com.symphony.bot.sdk.internal.command.CommandHandler;
import com.symphony.bot.sdk.internal.command.model.BotCommand;
import com.symphony.bot.sdk.internal.event.model.MessageEvent;
import com.symphony.bot.sdk.internal.feature.FeatureManager;
import com.symphony.bot.sdk.internal.symphony.MessageClientImpl;
import com.symphony.bot.sdk.internal.symphony.UsersClient;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyMessage;

@ExtendWith(MockitoExtension.class)
public class CommandHandlerTest {

  @Mock
  private CommandDispatcher commandDispatcher;

  @Mock
  private CommandFilter commandFilter;

  @Mock
  private MessageClientImpl messageClient;

  @Mock
  private FeatureManager featureManager;

  @Mock
  private UsersClient usersClient;

  @InjectMocks
  private TestCommandHandler commandHandler;


  static class TestCommandHandler extends CommandHandler {

    private BiConsumer<BotCommand, SymphonyMessage> internalHandle;

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
    public void handle(BotCommand command, SymphonyMessage commandResponse) {
      if (internalHandle != null) {
        internalHandle.accept(command, commandResponse);
      }
    }

    // Helper to ease changing the behavior of handle method on each test
    private void setInternalHandle(BiConsumer<BotCommand, SymphonyMessage> consumer) {
      this.internalHandle = consumer;
    }
  }

  @Test
  public void onCommandTest() {
    CommandHandler spyCommandHandler = spy(commandHandler);
    BotCommand command = mock(BotCommand.class);

    spyCommandHandler.onCommand(command);

    verify(spyCommandHandler, times(1)).handle(
        any(BotCommand.class), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandGetCommandMessageTest() {
    commandHandler.setInternalHandle((cmd, msg) -> cmd.getMessageEvent());
    BotCommand command = mock(BotCommand.class);

    commandHandler.onCommand(command);

    verify(command, times(2)).getMessageEvent();
  }

  @Test
  public void onCommandGetBotNameTest() {
    commandHandler.setInternalHandle(
        (cmd, msg) -> commandHandler.getBotName());
    CommandHandler spyCommandHandler = spy(commandHandler);
    BotCommand command = mock(BotCommand.class);

    spyCommandHandler.onCommand(command);

    verify(usersClient, times(1)).getBotDisplayName();
    verify(messageClient, never())
        ._sendMessage(anyString(), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandFeedbackDisabledTest() {
    commandHandler.setInternalHandle(
        (cmd, msg) -> msg.setMessage("some response message"));
    BotCommand command = mock(BotCommand.class);
    when(featureManager.isCommandFeedbackEnabled()).thenReturn(false);

    commandHandler.onCommand(command);

    verify(featureManager, times(1)).isCommandFeedbackEnabled();
    verify(messageClient, never())
        ._sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandSendResponseMessageTest() {
    commandHandler.setInternalHandle(
        (cmd, msg) -> msg.setMessage("some response message"));
    BotCommand command = mock(BotCommand.class);
    MessageEvent message = mock(MessageEvent.class);
    when(message.getStreamId()).thenReturn("STREAM_ID_1234");
    when(command.getMessageEvent()).thenReturn(message);
    when(featureManager.isCommandFeedbackEnabled()).thenReturn(true);

    commandHandler.onCommand(command);

    verify(featureManager, times(1)).isCommandFeedbackEnabled();
    verify(messageClient, times(1))
        ._sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandProcessingErrorFeedbackDisabledTest() {
    CommandHandler spyCommandHandler = spy(commandHandler);
    BotCommand command = mock(BotCommand.class);
    doThrow(new RuntimeException())
        .when(spyCommandHandler)
        .handle(any(BotCommand.class), any(SymphonyMessage.class));
    when(featureManager.unexpectedErrorResponse()).thenReturn(null);

    spyCommandHandler.onCommand(command);

    verify(featureManager, times(1)).unexpectedErrorResponse();
    verify(messageClient, never())
        ._sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandProcessingErrorWithFeedbackTest() {
    CommandHandler spyCommandHandler = spy(commandHandler);
    BotCommand command = mock(BotCommand.class);
    doThrow(new RuntimeException())
        .when(spyCommandHandler)
        .handle(any(BotCommand.class), any(SymphonyMessage.class));
    when(featureManager.unexpectedErrorResponse())
        .thenReturn("some error message");
    MessageEvent message = mock(MessageEvent.class);
    when(message.getStreamId()).thenReturn("STREAM_ID_1234");
    when(command.getMessageEvent()).thenReturn(message);

    spyCommandHandler.onCommand(command);

    verify(featureManager, atLeastOnce()).unexpectedErrorResponse();
    verify(messageClient, times(1))
        ._sendMessage(any(String.class), any(SymphonyMessage.class));
  }

}
