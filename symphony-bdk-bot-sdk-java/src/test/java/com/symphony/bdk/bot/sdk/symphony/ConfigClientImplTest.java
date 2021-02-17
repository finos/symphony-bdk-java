package com.symphony.bdk.bot.sdk.symphony;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import clients.SymBotClient;
import configuration.SymConfig;
import configuration.SymConfigLoader;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ConfigClientImplTest {

  private SymConfig symConfig;
  private SymBotClient symBotClient;
  private ConfigClientImpl configClient;

  @Before
  public void initConfigClient(){
    this.symConfig = SymConfigLoader.loadConfig("src/test/resources/sym-config.json");

    this.symBotClient = Mockito.mock(SymBotClient.class);
    Mockito.when(this.symBotClient.getConfig()).thenReturn(this.symConfig);

    this.configClient = new ConfigClientImpl(this.symBotClient);
    assertNotNull(this.configClient);
  }

  @Test
  public void testGetAppAuthPathWithoutSlash(){
    assertEquals("auth/", this.configClient.getExtAppAuthPath());
  }

  @Test
  public void testGetAppAuthPathWithSlash(){
    assertEquals("auth/", this.configClient.getExtAppAuthPath());
  }

  @Test
  public void testGetExtAppId(){
    assertEquals("testapp", this.configClient.getExtAppId());
  }

  @Test
  public void testGetPodBaseUrl(){
    assertEquals("https://localhost:7443", this.configClient.getPodBaseUrl());
  }
}
