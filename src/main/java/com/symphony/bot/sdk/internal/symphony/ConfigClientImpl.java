package com.symphony.bot.sdk.internal.symphony;

import clients.SymBotClient;
import configuration.SymConfig;
import org.springframework.stereotype.Service;

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

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPodBaseUrl() {
    return "https://" + symBotConfig.getPodHost() + ":" + symBotConfig.getPodPort();
  }

}
