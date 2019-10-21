package com.symphony.ms.songwriter.internal.command;

import com.symphony.ms.songwriter.internal.command.model.AuthenticationContext;
import com.symphony.ms.songwriter.internal.command.model.BotCommand;
import com.symphony.ms.songwriter.internal.message.model.SymphonyMessage;

public interface AuthenticationProvider {

  AuthenticationContext getAuthenticationContext(String userId);

  void handleUnauthenticated(
      BotCommand command, SymphonyMessage commandResponse);

}
