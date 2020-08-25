package com.symphony.bdk.bot.sdk.command;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bdk.bot.sdk.command.AuthenticatedCommandHandler;
import com.symphony.bdk.bot.sdk.command.AuthenticationProvider;
import com.symphony.bdk.bot.sdk.command.CommandDispatcher;
import com.symphony.bdk.bot.sdk.command.CommandFilter;
import com.symphony.bdk.bot.sdk.command.model.AuthenticationContext;
import com.symphony.bdk.bot.sdk.command.model.BotCommand;
import com.symphony.bdk.bot.sdk.event.model.MessageEvent;
import com.symphony.bdk.bot.sdk.feature.FeatureManager;
import com.symphony.bdk.bot.sdk.symphony.MessageClientImpl;
import com.symphony.bdk.bot.sdk.symphony.UsersClient;
import com.symphony.bdk.bot.sdk.symphony.model.SymphonyMessage;

import java.util.function.Predicate;
import java.util.regex.Pattern;

@ExtendWith(MockitoExtension.class)
public class AuthenticatedCommandHandlerTest {

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
    MessageEvent message = mock(MessageEvent.class);
    when(message.getUserId()).thenReturn(1234L);
    when(command.getMessageEvent()).thenReturn(message);
    when(authenticationProvider.getAuthenticationContext(anyLong())).thenReturn(null);

    commandHandler.handle(command, response);

    verify(authenticationProvider, times(1)).handleUnauthenticated(command, response);

  }

  @Test
  public void handleUserNotAuthenticatedTest() {
    BotCommand command = mock(BotCommand.class);
    SymphonyMessage response = mock(SymphonyMessage.class);
    AuthenticationContext context = mock(AuthenticationContext.class);
    MessageEvent message = mock(MessageEvent.class);
    when(message.getUserId()).thenReturn(1234L);
    when(command.getMessageEvent()).thenReturn(message);
    when(authenticationProvider.getAuthenticationContext(anyLong())).thenReturn(context);
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
    MessageEvent message = mock(MessageEvent.class);
    when(message.getUserId()).thenReturn(1234L);
    when(command.getMessageEvent()).thenReturn(message);
    when(authenticationProvider.getAuthenticationContext(anyLong())).thenReturn(context);
    when(context.isAuthenticated()).thenReturn(true);

    AuthenticatedCommandHandler spyCommandHandler = spy(commandHandler);
    spyCommandHandler.handle(command, response);

    verify(spyCommandHandler, times(1)).handle(command, response, context);

  }

}
