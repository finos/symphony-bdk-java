package com.symphony.ms.bot.sdk.internal.symphony;

import org.springframework.stereotype.Service;
import clients.SymBotClient;
import configuration.SymConfig;

@Service
public class ConfigClientImpl implements ConfigClient {

  private SymConfig symBotConfig;

  public ConfigClientImpl(SymBotClient symBotClient) {
    symBotConfig = symBotClient.getConfig();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getExtAppAuthPath() {
    String path = symBotConfig.getAuthenticationFilterUrlPattern();
    return path.endsWith("/") ? path : path.concat("/");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getExtAppId() {
    return symBotConfig.getAppId();
  }

}
