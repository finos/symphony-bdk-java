package com.symphony.ms.songwriter.internal.lib.templating.config;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.songwriter.internal.lib.templating.TemplateService;
import com.symphony.ms.songwriter.internal.lib.templating.TemplateServiceImpl;
import freemarker.template.TemplateException;

/**
 * Creates and configures an instance of the Freemarker-based implementation
 * of the {@link TemplateService} if no other implementation is provided.
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class TemplateServiceConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceConfig.class);

  @Bean(name="templateServiceImpl")
  //@ConditionalOnProperty(value = "templateengine.disabled", havingValue="false", matchIfMissing=true)
  @ConditionalOnMissingBean
  public TemplateService getFreeMarkerConfiguration(JsonMapper mapper) throws TemplateException, IOException {
    LOGGER.info("Initializing Template Engine");
    FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
    bean.setTemplateLoaderPath("classpath:/templates/");
    bean.setDefaultEncoding("UTF-8");
    bean.afterPropertiesSet();

    return new TemplateServiceImpl(bean.getObject(), mapper);
  }

}
