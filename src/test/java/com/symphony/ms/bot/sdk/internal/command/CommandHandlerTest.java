package com.symphony.ms.bot.sdk.internal.command;

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
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.message.MessageService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;

@ExtendWith(MockitoExtension.class)
public class CommandHandlerTest {

  @Mock
  private CommandDispatcher commandDispatcher;

  @Mock
  private CommandFilter commandFilter;

  @Mock
  private MessageService messageService;

  @Mock
  private FeatureManager featureManager;

  @Mock
  private SymphonyService symphonyService;

  @InjectMocks
  private TestCommandHandler commandHandler;

  static class TestCommandHandler extends CommandHandler {

    private BiConsumer<BotCommand, SymphonyMessage> internalHandle;

    @Override
    protected Predicate<String> getCommandMatcher() {
      return Pattern
          .compile("^@BotName /test$")
          .asPredicate();
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
  public void registerTest() {
    commandHandler.register();

    verify(commandDispatcher, times(1)).register(any(String.class), any(CommandHandler.class));
    verify(commandFilter, times(1)).addFilter(anyString(), any());
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
    commandHandler.setInternalHandle((cmd, msg) -> cmd.getMessage());
    BotCommand command = mock(BotCommand.class);

    commandHandler.onCommand(command);

    verify(command, atLeastOnce()).getMessage();
  }

  @Test
  public void onCommandFeedbackDisabledTest() {
    commandHandler.setInternalHandle(
        (cmd, msg) -> msg.setMessage("some response message"));
    BotCommand command = mock(BotCommand.class);
    when(featureManager.isCommandFeedbackEnabled()).thenReturn(false);

    commandHandler.onCommand(command);

    verify(featureManager, times(1)).isCommandFeedbackEnabled();
    verify(messageService, never())
      .sendMessage(any(String.class), any(SymphonyMessage.class));
  }

  @Test
  public void onCommandSendResponseMessageTest() {
    commandHandler.setInternalHandle(
        (cmd, msg) -> msg.setMessage("some response message"));
    BotCommand command = mock(BotCommand.class);
    when(command.getStreamId()).thenReturn("STREAM_ID_1234");
    when(featureManager.isCommandFeedbackEnabled()).thenReturn(true);

    commandHandler.onCommand(command);

    verify(featureManager, times(1)).isCommandFeedbackEnabled();
    verify(messageService, times(1))
      .sendMessage(any(String.class), any(SymphonyMessage.class));
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

    verify(spyCommandHandler, times(1)).getCommandName();
    verify(featureManager, times(1)).unexpectedErrorResponse();
    verify(messageService, never())
      .sendMessage(any(String.class), any(SymphonyMessage.class));
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
    when(command.getStreamId()).thenReturn("STREAM_ID_1234");

    spyCommandHandler.onCommand(command);

    verify(spyCommandHandler, times(1)).getCommandName();
    verify(featureManager, atLeastOnce()).unexpectedErrorResponse();
    verify(messageService, times(1))
      .sendMessage(any(String.class), any(SymphonyMessage.class));
  }

}
