package com.symphony.ms.songwriter.internal.lib.templating.config;

import com.symphony.ms.songwriter.internal.lib.templating.TemplateService;
import com.symphony.ms.songwriter.internal.lib.templating.TemplateServiceImpl;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import services.HandlebarsTemplateLoader;

/**
 * Creates and configures an instance of the Freemarker-based implementation of the {@link
 * TemplateService} if no other implementation is provided.
 *
 * @author Marcus Secato
 */
@Configuration
public class TemplateServiceConfig {

  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceConfig.class);

  @Bean(name = "templateServiceImpl")
  @ConditionalOnMissingBean
  public TemplateService getHandleBarsConfiguration() {
    LOGGER.info("Initializing Template Engine");
    TemplateLoader symphonyTemplateLoader = HandlebarsTemplateLoader.getLoader();
    TemplateLoader internalTemplateLoader = new ClassPathTemplateLoader("/templates", ".hbs");
    return new TemplateServiceImpl(
        new Handlebars().with(internalTemplateLoader, symphonyTemplateLoader));
  }

}
