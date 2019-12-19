package com.symphony.ms.bot.sdk.internal.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
public class DefaultCommandHandlerTest {
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

  static class TestCommandHandler extends DefaultCommandHandler {

    @Override
    protected Predicate<String> getCommandMatcher() {
      return Pattern
          .compile("^@BotName$")
          .asPredicate();
    }

    @Override
    public void handle(BotCommand command, SymphonyMessage commandResponse) {
      // Do nothing
    }
  }

  @Test
  public void registerTest() {
    commandHandler.register();

    verify(commandDispatcher, times(1)).register(any(String.class), any(CommandHandler.class));
    verify(commandFilter, times(1)).setDefaultFilter(anyString(), any());
  }

}
