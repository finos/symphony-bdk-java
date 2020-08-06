package com.symphony.bdk.examples;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.BotAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.client.ApiClientFactory;
import com.symphony.bdk.core.config.BdkConfigLoader;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.exceptions.BdkConfigException;

import lombok.extern.slf4j.Slf4j;

/**
 * Deep Dive example on how the Authentication works in the Java BDK.
 */
@Slf4j
public class AuthDeepDiveMain {

  public static void main(String[] args) throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

    // load configuration from classpath
    final BdkConfig config = BdkConfigLoader.loadFromClasspath("/config.yaml");

    // this factory provides ApiClient instances for further API calls
    final ApiClientFactory apiClientFactory = new ApiClientFactory(config);

    // this factory provides services allowing regular and OBO authentication
    final AuthenticatorFactory authFactory = new AuthenticatorFactory(
        config,
        apiClientFactory.getLoginClient(),
        apiClientFactory.getRelayClient()
    );

    // the BotAuthenticator interface process regular service account authentication for the main Bot account defined in the configuration
    final BotAuthenticator botAuth = authFactory.getBotAuthenticator();

    // returned auth session is not refreshed, it actually means that auth requests haven't been performed against the API
    final AuthSession botSession = botAuth.authenticateBot();
    botSession.refresh();

    log.info("Bot successfully authenticated!");
  }
}
