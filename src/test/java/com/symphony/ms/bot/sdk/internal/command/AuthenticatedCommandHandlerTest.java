package com.symphony.ms.bot.sdk.internal.command;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.symphony.ms.bot.sdk.internal.command.model.AuthenticationContext;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.feature.FeatureManager;
import com.symphony.ms.bot.sdk.internal.message.MessageService;
import com.symphony.ms.bot.sdk.internal.message.model.SymphonyMessage;
import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedCommandHandlerTest {

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

  @Mock
  private AuthenticationProvider authenticationProvider;

  @InjectMocks
  private AuthCommandHandler commandHandler;

  static class AuthCommandHandler extends AuthenticatedCommandHandler {
    @Override
    protected Predicate<String> getCommandMatcher() {
      return Pattern
          .compile("^@BotName /test$")
          .asPredicate();
    }

    @Override
    public void handle(BotCommand command, SymphonyMessage commandResponse,
        AuthenticationContext authenticationContext) {
    }
  }

  @Test
  public void handleAuthContextNullTest() {
    BotCommand command = mock(BotCommand.class);
    SymphonyMessage response = mock(SymphonyMessage.class);
    when(command.getUserId()).thenReturn("1234");
    when(authenticationProvider.getAuthenticationContext(anyString())).thenReturn(null);

    commandHandler.handle(command, response);

    verify(authenticationProvider, times(1)).handleUnauthenticated(command, response);

  }

  @Test
  public void handleUserNotAuthenticatedTest() {
    BotCommand command = mock(BotCommand.class);
    SymphonyMessage response = mock(SymphonyMessage.class);
    AuthenticationContext context = mock(AuthenticationContext.class);
    when(command.getUserId()).thenReturn("1234");
    when(authenticationProvider.getAuthenticationContext(anyString())).thenReturn(context);
    when(context.isAuthenticated()).thenReturn(false);

    commandHandler.handle(command, response);

    verify(context, times(1)).isAuthenticated();
    verify(authenticationProvider, times(1)).handleUnauthenticated(command, response);

  }

  @Test
  public void handleUserAuthenticatedTest() {
    BotCommand command = mock(BotCommand.class);
    SymphonyMessage response = mock(SymphonyMessage.class);
    AuthenticationContext context = mock(AuthenticationContext.class);
    when(command.getUserId()).thenReturn("1234");
    when(authenticationProvider.getAuthenticationContext(anyString())).thenReturn(context);
    when(context.isAuthenticated()).thenReturn(true);

    AuthenticatedCommandHandler spyCommandHandler = spy(commandHandler);
    spyCommandHandler.handle(command, response);

    verify(spyCommandHandler, times(1)).handle(command, response, context);

  }

}
