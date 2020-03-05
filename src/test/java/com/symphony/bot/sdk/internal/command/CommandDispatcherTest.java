package com.symphony.bot.sdk.internal.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bot.sdk.internal.command.CommandDispatcherImpl;
import com.symphony.bot.sdk.internal.command.CommandHandler;
import com.symphony.bot.sdk.internal.command.model.BotCommand;

@ExtendWith(MockitoExtension.class)
public class CommandDispatcherTest {

  @InjectMocks
  private CommandDispatcherImpl commandDispatcher;

  @Test
  public void dispatchSuccessTest() {
    CommandHandler commandHandler = mock(CommandHandler.class);
    BotCommand command = mock(BotCommand.class);

    commandDispatcher.register("TestCommand", commandHandler);
    commandDispatcher.push("TestCommand", command);

    verify(commandHandler, times(1)).onCommand(any(BotCommand.class));
  }

  @Test
  public void dispatchNoHandlerTest() {
    CommandHandler commandHandler = mock(CommandHandler.class);
    BotCommand command = mock(BotCommand.class);

    commandDispatcher.register("TestCommand1", commandHandler);
    commandDispatcher.push("TestCommand2", command);

    verify(commandHandler, never()).onCommand(any(BotCommand.class));
  }

}
