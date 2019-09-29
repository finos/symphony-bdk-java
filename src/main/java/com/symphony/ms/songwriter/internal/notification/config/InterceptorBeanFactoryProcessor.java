package com.symphony.ms.songwriter.internal.notification.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import com.symphony.ms.songwriter.internal.notification.NotificationInterceptor;

@Component
public class InterceptorBeanFactoryProcessor implements BeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(NotificationInterceptor.class));
    Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.symphony.ms");
    LOGGER.info("Scanning for notification interceptors found {} beans", beanDefinitionSet.size());

    for(BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinition commandHandler = BeanDefinitionBuilder
          .rootBeanDefinition(beanDefinition.getBeanClassName())
          .setInitMethodName("register")
          .addPropertyReference("interceptorChain", "interceptorChainImpl")
          .addPropertyReference("baseWebhookService", "baseWebhookServiceImpl")
          .getBeanDefinition();

      beanDefinitionRegistry.registerBeanDefinition(beanDefinition.getBeanClassName(), commandHandler);
    }
  }

}
