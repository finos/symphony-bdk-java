package com.symphony.bdk.examples;

import com.symphony.bdk.core.api.invoker.jersey2.ApiClientJersey2;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.exception.AuthenticationException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfig;
import com.symphony.bdk.core.service.V4MessageService;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.extern.slf4j.Slf4j;

/**
 * This very basic example demonstrates how send a message using both regular and OBO authentication modes.
 */
@Slf4j
public class AuthenticationMain {

  public static void main(String[] args) throws AuthenticationException {

    // load configuration
    final BdkConfig bdkConfig = BdkConfig.load("/config.yaml");

    // create the ApiClient factory
    final ApiClientFactory apiClientFactory = new ApiClientFactory(bdkConfig, ApiClientJersey2.class);

    // initialize the auth factory from config + login and relay clients
    final AuthenticatorFactory authenticatorFactory = new AuthenticatorFactory(
        bdkConfig,
        apiClientFactory.getLoginClient(),
        apiClientFactory.getRelayClient()
    );

    // create the message service using the Agent client
    final V4MessageService messageService = new V4MessageService(apiClientFactory.getAgentClient());
    final String streamId = "VGkonN0ysqZSY2scmMXnen___oxFBA6WdA";
    final String message = "<messageML>Hello, World!</messageML>";

    //
    // Regular auth example : send a message from the bot account
    //
    final AuthSession botSession = authenticatorFactory.authenticateBot();
    final V4Message regularMessage = messageService.sendMessage(botSession, streamId, message);
    log.info("Regular message sent : {}", regularMessage.getMessageId());

    //
    // OBO auth example : send a message on-behalf-of an user
    //
    final AuthSession oboSession = authenticatorFactory.getOboAuthenticator().authenticateByUsername("thibault.pensec");
    final V4Message oboMessage = messageService.sendMessage(oboSession, streamId, message);
    log.info("OBO message sent : {}", oboMessage.getMessageId());
  }
}
