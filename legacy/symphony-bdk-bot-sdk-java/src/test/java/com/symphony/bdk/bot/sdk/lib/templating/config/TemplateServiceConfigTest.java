package com.symphony.bdk.bot.sdk.lib.templating.config;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.bdk.bot.sdk.lib.templating.TemplateService;
import com.symphony.bdk.bot.sdk.lib.templating.TemplateServiceImpl;
import com.symphony.bdk.bot.sdk.lib.templating.config.TemplateServiceConfig;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceConfigTest {

  @InjectMocks
  private TemplateServiceConfig templateServiceConfig;

  @Test
  public void shouldGetHandlebarsService() {
    TemplateService templateService = templateServiceConfig.getHandlebarsService();

    assertNotNull(templateService);
    assertTrue(templateService instanceof TemplateServiceImpl);
  }

}
