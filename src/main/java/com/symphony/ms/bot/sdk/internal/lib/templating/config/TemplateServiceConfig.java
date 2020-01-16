package com.symphony.ms.bot.sdk.internal.lib.templating.config;

import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateService;
import com.symphony.ms.bot.sdk.internal.lib.templating.TemplateServiceImpl;

import com.github.jknack.handlebars.Handlebars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.HandlebarsTemplateLoader;

/**
 * Creates and configures an instance of the Handlebars-based implementation of the {@link
 * TemplateService} if no other implementation is provided.
 *
 * @author Marcus Secato
 */
@Configuration
public class TemplateServiceConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceConfig.class);

  @Bean(name = "templateServiceImpl")
  @ConditionalOnMissingBean
  public TemplateService getHandlebarsService() {
    LOGGER.info("Initializing Template Engine");
    Handlebars handlebars = new HandlebarsTemplateLoader().getHandlebars();
    return new TemplateServiceImpl(handlebars);
  }

}
