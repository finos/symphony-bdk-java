package com.symphony.ms.bot.sdk.internal.lib.templating.config;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateService;
import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

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
