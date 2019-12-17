package com.symphony.ms.bot.sdk.internal.sse.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import com.symphony.ms.bot.sdk.internal.scan.BaseBeanFactoryPostProcessor;
import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;

/**
 * Automatically scans for {@link SsePublisher}, instantiates them,
 * injects all dependencies and registers to Spring bean registry.
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class SsePublisherBeanFactoryProcessor extends BaseBeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(SsePublisherBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    Set<BeanDefinition> beanDefinitionSet = scanComponents(SsePublisher.class);
    LOGGER.info("Scanning for sse publishers found {} beans", beanDefinitionSet.size());

    for (BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinition ssePublisher = BeanDefinitionBuilder
          .genericBeanDefinition(beanDefinition.getBeanClassName())
          .setInitMethodName("register")
          .addPropertyReference("ssePublisherRouter", "ssePublisherRouterImpl")
          .getBeanDefinition();

      beanDefinitionRegistry.registerBeanDefinition(
          beanDefinition.getBeanClassName(), ssePublisher);
    }
  }

}
