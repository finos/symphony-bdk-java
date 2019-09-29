package com.symphony.ms.songwriter.internal.lib.templating.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;
import com.symphony.ms.songwriter.internal.lib.jsonmapper.JsonMapper;
import com.symphony.ms.songwriter.internal.lib.templating.TemplateService;
import com.symphony.ms.songwriter.internal.lib.templating.TemplateServiceImpl;

@Configuration
public class TemplateServiceConfig {
  private static final Logger LOGGER = LoggerFactory.getLogger(TemplateServiceConfig.class);

  @Autowired
  private JsonMapper mapper;

  @Bean(name="templateServiceImpl")
  //@ConditionalOnProperty(value = "templateengine.disabled", havingValue="false", matchIfMissing=true)
  @ConditionalOnMissingBean
  public TemplateService getFreeMarkerConfiguration() {
    LOGGER.info("Initializing Template Engine");
    FreeMarkerConfigurationFactoryBean bean = new FreeMarkerConfigurationFactoryBean();
    bean.setTemplateLoaderPath("classpath:/templates/");

    return new TemplateServiceImpl(bean.getObject(), mapper);
  }

}
